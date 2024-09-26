/*
 * Copyright (C) 2024 vitor
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.vitorsantana.cmsptasksdoer;

import com.vitorsantana.cmsptasksdoer.cmspobjects.Question;
import com.vitorsantana.cmsptasksdoer.cmspobjects.Task;
import com.vitorsantana.cmsptasksdoer.cmspobjects.User;
import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author vitor
 */
public class CmspCommunicator {
    HttpClient httpClient;
    CookieManager cookieManager;
    String authToken = "";

    ArrayList<Task> taskList = new ArrayList<>();

    private String roomName;

    public CmspCommunicator() {
        cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);

        httpClient = HttpClient.newBuilder().cookieHandler(cookieManager).build();
        HttpRequest httpRequest = createHttpRequest(URI.create("https://cmspweb.ip.tv/"), null, null, "GET", null).build();
        sendRequest(httpClient, httpRequest);
    }

    public User loginToCmsp(int ra, char digit, char[] password) throws Exception {
        String body = "{" + "\"realm\":\"edusp\",\"platform\":\"webclient\",\"id\":\"" + "0000" + ra + digit + "sp" + "\",\"password\":\"" + String.copyValueOf(password) + '"' + "}";
        HttpRequest httpRequest = createHttpRequest(URI.create("https://edusp-api.ip.tv/registration/edusp"), null, "application/json", "POST", HttpRequest.BodyPublishers.ofString(body)).header("x-api-realm", "edusp").header("x-api-platform", "webclient").build();

        HttpResponse<String> send = sendRequest(httpClient, httpRequest);
        if (send.body().contains("wrong credentials")) {
            throw new Exception("Wrong Credentials");
        }
        JSONObject jsonBody = new JSONObject(send.body());

        authToken = (String) jsonBody.get("auth_token");
        taskList.clear();
        return new User((String) jsonBody.get("name"), (String) jsonBody.get("nick"));
    }

    public ArrayList<Task> getTasks() {
        if (!taskList.isEmpty()) {
            return taskList;
        }
        HttpRequest httpRequest = createHttpRequest(URI.create("https://edusp-api.ip.tv/room/user?list_all=true&with_cards=false"), authToken, null, "GET", null).build();
        HttpResponse<String> send = sendRequest(httpClient, httpRequest);
        JSONObject jsonBody = new JSONObject(send.body());

        roomName = null;
        int categoryId = 0;
        for (int i = 0; !jsonBody.getJSONArray("rooms").isNull(i); i++) {
            if (!jsonBody.getJSONArray("rooms").getJSONObject(i).get("topic").toString().contains("[IF]")) {
                roomName = (String) jsonBody.getJSONArray("rooms").getJSONObject(i).get("name");
                for (int j = 0; !jsonBody.getJSONArray("rooms").getJSONObject(i).getJSONArray("group_categories").isNull(j); j++) {
                    if (!jsonBody.getJSONArray("rooms").getJSONObject(i).getJSONArray("group_categories").getJSONObject(j).getJSONArray("cards").isEmpty()) {
                        categoryId = (int) jsonBody.getJSONArray("rooms").getJSONObject(i).getJSONArray("group_categories").getJSONObject(j).get("id");
                    }
                }
            }
        }
        httpRequest = createHttpRequest(URI.create("https://edusp-api.ip.tv/tms/task/todo?publication_target[]=" + roomName + "&publication_target[]=" + categoryId + "&expired_only=false&limit=100&filter_expired=true&offset=0"), authToken, null, "GET", null).build();
        HttpResponse<String> tasksResponse = sendRequest(httpClient, httpRequest);
        JSONArray tasks = new JSONArray(tasksResponse.body());
        tasks.forEach((task) -> {
            try {
                taskList.add(new Task(((JSONObject) task).getInt("id"), ((JSONObject) task).getString("title"), ((JSONObject) task).getBoolean("is_exam"), ((JSONObject) task).getBoolean("is_essay"), roomName));
                getQuestion(taskList.get(taskList.size() - 1));
            } catch (JSONException ex) {
                ErrorHandler.handleException(CmspCommunicator.class.getName(), ex, "Failed to parse task: " + ((JSONObject) task).getString("title") + " with the following Exception: ");
                Logger.getLogger(CmspCommunicator.class.getName()).log(Level.WARNING, "Failed to parse task: " + ((JSONObject) task).getString("title") + " with the following Exception: ", ex.fillInStackTrace());
            }
        });

        return taskList;
    }

    private void getQuestion(Task task) {
        HttpRequest httpRequest = createHttpRequest(URI.create("https://edusp-api.ip.tv/tms/task/" + task.getId() + "/apply?preview_mode=false"), authToken, null, "GET", null).build();
        HttpResponse<String> taskIn = sendRequest(httpClient, httpRequest);
        JSONObject tasksInJson = new JSONObject(taskIn.body());
        tasksInJson.getJSONArray("questions").forEach((question) -> {
            task.getQuestions().add(new Question(((JSONObject) question).getInt("id"),
                    ((JSONObject) question).getString("type"),
                    ((JSONObject) question).getBoolean("required"),
                    ((JSONObject) question).isNull("options") ? null : ((JSONObject) question).getJSONObject("options"),
                    task));
        });

    }

    public JSONObject checkAnswer(Task task, Question question, JSONObject answer) {
        HttpRequest httpRequest = createHttpRequest(URI.create("https://edusp-api.ip.tv/tms/task/" + task.getId() + "/question/" + question.getId() + "/correct"), authToken, "application/json", "POST", HttpRequest.BodyPublishers.ofString(answer.toString())).build();
        return new JSONObject(sendRequest(httpClient, httpRequest).body());
    }

    private HttpResponse<String> sendRequest(HttpClient client, HttpRequest request) {
        try {
            HttpResponse<String> send = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (send.statusCode() == 400) {
                throw new HttpClientException("404 Bad Request");
            }
//            if(send.statusCode() == 401){
//                throw new HttpClientException("402 Unauthorized");
//            }
            if (send.statusCode() == 403) {
                throw new HttpClientException("403 Forbidden " + send.body());
            }
            return send;
        } catch (IOException | HttpClientException | InterruptedException ex) {
            if (ex.getClass().equals(IOException.class)) {
                ErrorHandler.handleException(CmspCommunicator.class.getName(), ex, "Failed to connect: ".concat(ex.getMessage()));
                Logger.getLogger(CmspCommunicator.class.getName()).log(Level.SEVERE, "Failed to connect: ".concat(ex.getMessage()), ex);
            } else if (ex.getClass().equals(InterruptedException.class)) {
                ErrorHandler.handleException(CmspCommunicator.class.getName(), ex, "Connection interrupted: ".concat(ex.getMessage()));
                Logger.getLogger(CmspCommunicator.class.getName()).log(Level.SEVERE, "Connection interrupted: ".concat(ex.getMessage()), ex);
            } else if (ex.getClass().equals(HttpClientException.class)) {
                ErrorHandler.handleException(CmspCommunicator.class.getName(), ex, "Connection failed: ".concat(ex.getMessage()));
                Logger.getLogger(CmspCommunicator.class.getName()).log(Level.SEVERE, "Connection failed: ".concat(ex.getMessage()), ex);
            }

        }
        return null;
    }

    public void sendTaskSubmition(Task task, JSONObject form) {
        HttpResponse<String> result = sendRequest(httpClient, createHttpRequest(
                URI.create("https://edusp-api.ip.tv/tms/task/" + task.getId() + "/answer"), authToken, "application/json", "POST", HttpRequest.BodyPublishers.ofString(form.toString())).build());
    }

    private HttpRequest.Builder createHttpRequest(URI uri, String token, String contentType, String type, HttpRequest.BodyPublisher body) {
        HttpRequest.Builder httpRequest = HttpRequest.newBuilder().uri(uri);
        if (token != null) {
            httpRequest = httpRequest.header("x-api-key", token);
        }
        if (contentType != null) {
            httpRequest = httpRequest.header("content-type", contentType);
        }
        switch (type) {
            case "GET" -> {
                httpRequest = httpRequest.GET();
            }
            case "POST" -> httpRequest = httpRequest.POST(body);
            default -> {
            }

        }
        return httpRequest;
    }

    public void addTaskById(long id) {
        HttpRequest httpRequest = createHttpRequest(URI.create("https://edusp-api.ip.tv/tms/task/" + id + "/apply?preview_mode=false"), authToken, null, "GET", null).build();
        HttpResponse<String> taskIn = sendRequest(httpClient, httpRequest);
        JSONObject tasksInJson = new JSONObject(taskIn.body());
        Task task = new Task((int) id, tasksInJson.getString("title"), tasksInJson.getBoolean("is_exam"), tasksInJson.getBoolean("is_essay"), tasksInJson.getString("publication_target"));
        getQuestion(task);
        taskList.add(task);
    }
}
