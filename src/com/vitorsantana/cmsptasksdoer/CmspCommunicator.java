/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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
import org.json.JSONObject;

/**
 *
 * @author vitor
 */
public class CmspCommunicator{
    HttpClient httpClient;
    CookieManager cookieManager;
    String authToken = "";
    
    ArrayList<Task> taskList = new ArrayList<>();
    
    private String roomName;

    public CmspCommunicator(){
        cookieManager = new CookieManager();
        CookieHandler.setDefault(cookieManager);
        
        
        httpClient = HttpClient.newBuilder().cookieHandler(cookieManager).build();
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create("https://cmspweb.ip.tv/")).GET().build();
        sendRequest(httpClient, httpRequest);;
    }
    
    public User loginToCmsp(int ra, char digit, char[] password) throws Exception{
        String body = "{"+"\"realm\":\"edusp\",\"platform\":\"webclient\",\"id\":\""+"0000"+ra+digit+"sp"+"\",\"password\":\""+String.copyValueOf(password)+'"'+"}";
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://edusp-api.ip.tv/registration/edusp"))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("content-type", "application/json")
                .header("x-api-realm", "edusp")
                .header("x-api-platform", "webclient")
                .build();
        HttpResponse<String> send = sendRequest(httpClient, httpRequest);;
        if(send.body().contains("wrong credentials")){
            throw new Exception("Wrong Credentials");
        }
        JSONObject jsonBody = new JSONObject(send.body());
        
        authToken = (String) jsonBody.get("auth_token");
        taskList.clear();
        return new User((String)jsonBody.get("name"),(String)jsonBody.get("nick"));
    }
    
    public ArrayList getTasks(){
        if(!taskList.isEmpty()){
            return taskList;
        }
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://edusp-api.ip.tv/room/user?list_all=true&with_cards=false"))
                .header("x-api-key", authToken)
                .build();
        HttpResponse<String> send = sendRequest(httpClient, httpRequest);;
        JSONObject jsonBody = new JSONObject(send.body());
        
        roomName = null;
        int categoryId = 0;
        for(int i = 0; !jsonBody.getJSONArray("rooms").isNull(i);i++){
            if(!jsonBody.getJSONArray("rooms").getJSONObject(i).get("topic").toString().contains("[IF]")){
                roomName = (String) jsonBody.getJSONArray("rooms").getJSONObject(i).get("name");
                for(int j = 0; !jsonBody.getJSONArray("rooms").getJSONObject(i).getJSONArray("group_categories").isNull(j);j++){
                    if(!jsonBody.getJSONArray("rooms").getJSONObject(i).getJSONArray("group_categories").getJSONObject(j).getJSONArray("cards").isEmpty()){
                        categoryId = (int) jsonBody.getJSONArray("rooms").getJSONObject(i).getJSONArray("group_categories").getJSONObject(j).get("id");
                    }
                }
            }
        }
        httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://edusp-api.ip.tv/tms/task/todo?publication_target[]="+roomName+"&publication_target[]="+categoryId+"&expired_only=false&limit=100&filter_expired=true&offset=0"))
                .header("x-api-key", authToken)
                .build();
        HttpResponse<String> tasksResponse = sendRequest(httpClient, httpRequest);;
        JSONArray tasks = new JSONArray(tasksResponse.body());
        tasks.forEach((task) -> {
            
            taskList.add(new Task(((JSONObject)task).getInt("id"), ((JSONObject)task).getString("title"), ((JSONObject)task).getBoolean("is_exam"), ((JSONObject)task).getBoolean("is_essay"), roomName));
            getQuestion(taskList.get(taskList.size()-1));
        });
        
        
        return taskList;
    }
    
    private void getQuestion(Task task){
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://edusp-api.ip.tv/tms/task/"+task.getId()+"/apply?preview_mode=false"))
                .header("x-api-key", authToken)
                .build();
        HttpResponse<String> taskIn = sendRequest(httpClient, httpRequest);;
        JSONObject tasksInJson = new JSONObject(taskIn.body());
        tasksInJson.getJSONArray("questions").forEach((question) -> {
                task.getQuestions().add(new Question(((JSONObject)question).getInt("id"),
                        ((JSONObject)question).getString("type"),
                        ((JSONObject)question).getBoolean("required"),
                        ((JSONObject)question).isNull("options") ? null : ((JSONObject)question).getJSONObject("options"),
                        task));
            });
        
    }
    
    public JSONObject checkAnswer(Task task, Question question, JSONObject answer){
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://edusp-api.ip.tv/tms/task/"+task.getId()+"/question/"+question.getId()+"/correct"))
                .header("x-api-key", authToken)
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(answer.toString()))
                .build();
        return new JSONObject(sendRequest(httpClient, httpRequest).body());
    }
    
    private HttpResponse<String> sendRequest(HttpClient client, HttpRequest request){
        try{
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        }catch(IOException | InterruptedException ex){
            Logger.getLogger(CmspCommunicator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public void sendTaskSubmition(Task task, JSONObject form){
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create("https://edusp-api.ip.tv/tms/task/"+task.getId()+"/answer"))
                .header("x-api-key", authToken)
                .header("content-type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(form.toString()))
                .build();
        HttpResponse<String> result = sendRequest(httpClient, httpRequest);
    }

    public ArrayList<Task> getTaskList(){
        return taskList;
    }
    
}
