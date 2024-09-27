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
package com.vitorsantana.cmsptasksdoer.cmspobjects;

import com.vitorsantana.cmsptasksdoer.CMSPTasksDoer;
import com.vitorsantana.cmsptasksdoer.Options;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author vitor
 */
public class Question{
    
    public enum Types{info, multi, single, cloud, order_sentences, fill_words, essay, text, true_false, text_ai, undefined}
    
    private int id;
    private Types typeT;
    String type;
    private boolean required;
    private JSONObject options;
    private Task task;
    private Object correctAnswer;
    private boolean skipQuestion = false;
    

    public Question(int id, String type, boolean required, JSONObject options, Task task){
        this.id = id;
        this.type = type;
        try{
            this.typeT = Types.valueOf(type.replace("-", "_"));
        }catch(Exception e){
            Logger.getLogger(Question.class.getName()).log(Level.WARNING, e.fillInStackTrace().toString());
            this.typeT = Types.undefined;
        }
        this.required = required;
        this.options = options;
        this.task = task;
    }
    
    public void answerQuestion(){
        if(task.isAbort()){
            return;
        }
//        switch(typeT){
//            case single:
//                singleQuestionAnswerer();
//                break;
//            case multi:
//                multiQuestionAnswer();
//                break;
//            case cloud:
//                cloudQuestionAnswer();
//                break;
//            case order_sentences:
//                orderSentencesAnswer();
//                break;
//            case fill_words:
//                fillWordsAnswer();
//                break;
//            case true_false:
//                trueFalseAnswer();
//                break;
//            case text_ai:
//                task.abortTask();
//                break;
//            case text:
//                task.abortTask();
//                break;
//            default:
//                break;
//        }
        
        switch(typeT){
            case single -> singleQuestionAnswerer();
            case multi -> multiQuestionAnswer();
            case cloud -> cloudQuestionAnswer();
            case order_sentences -> orderSentencesAnswer();
            case fill_words -> fillWordsAnswer();
            case true_false -> trueFalseAnswer();
            case text_ai -> skipQuestion("Text Prduction AI");
            case text -> skipQuestion("Text Prduction");
            case undefined -> skipQuestion("Question is not supported or not yet handled");
            default -> {
                skipQuestion("Question is not supported or not yet handled. Or maybe it's just a title or something.");
            }
            
                
        }
    }
    
    private void singleQuestionAnswerer(){
        boolean[] answers = new boolean[options.length()];
        boolean isAnswerCorrect = false;
        int answerShift = 0;
        answers[answerShift] = true;
        JSONObject answer = null;
        StringBuilder optionsBuilder = null;
        while(!isAnswerCorrect){
            optionsBuilder = new StringBuilder();
            for(int i = 0; i < options.length();i++){
                if(answers[i] == true){
                    answers[i] = false;
                }
                if(i == answerShift){
                    answers[i] = true;
                }

                optionsBuilder.append("\"").append(i).append("\"").append(":").append(answers[i]).append(",");
            }
            answer = new JSONObject("{\"answer\":{\"answer\":{"+optionsBuilder.toString()+"}}}");
            JSONObject checkAnswer = CMSPTasksDoer.cmspCommunicator.checkAnswer(task, this, answer);
            isAnswerCorrect = checkAnswer.getBoolean("correct");
            answerShift++;
        }
        if(isAnswerCorrect){
            correctAnswer = new JSONObject("{"+optionsBuilder.toString()+"}");
        }
    }
    
    private void multiQuestionAnswer(){
        boolean[] answers = new boolean[options.length()];
        boolean isAnswerCorrect;
        int answerShift = 0;
        answers[answerShift] = true;
        JSONObject answer;
        StringBuilder optionsBuilder;
        for (int i = 0; i < Math.pow(2, answers.length); i++) {
            optionsBuilder = new StringBuilder();
            String bin = Integer.toBinaryString(i);
            while ((bin.length() < answers.length))
                bin = "0" + bin;
            char[] chars = bin.toCharArray();
            for (int j = 0; j < chars.length; j++) {
                answers[j] = chars[j] == '0';
                optionsBuilder.append("\"").append(j).append("\"").append(":").append(answers[j]).append(",");
            }
            
            
            answer = new JSONObject("{\"answer\":{\"answer\":{"+optionsBuilder.toString()+"}}}");
            JSONObject checkAnswer = CMSPTasksDoer.cmspCommunicator.checkAnswer(task, this, answer);
            isAnswerCorrect = checkAnswer.getBoolean("correct");
            try{
                Thread.sleep(Options.cooldownTimeForTryingAnswers);
            }catch(InterruptedException ex){
                Logger.getLogger(Question.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(isAnswerCorrect){
                correctAnswer = new JSONObject("{"+optionsBuilder+"}");
                return;
            }
        }
    }
    
    private void cloudQuestionAnswer(){
        JSONArray words = options.getJSONArray("words");
        List<Object> wordsString = words.toList();
        Collections.shuffle(wordsString);
        JSONObject answer = new JSONObject("{\"answer\":{\"answer\":"+new JSONArray(wordsString).toString()+"}}");
        JSONObject checkAnswer = CMSPTasksDoer.cmspCommunicator.checkAnswer(task, this, answer);
        correctAnswer = new JSONArray(wordsString);
    }
    
    private void orderSentencesAnswer(){
        JSONArray sentences = options.getJSONArray("sentences");
        List<Object> sentencesString = sentences.toList();
        
        Collections.shuffle(sentencesString);
        JSONObject answer = new JSONObject("{\"answer\":{\"answer\":"+new JSONArray(sentencesString).toString()+"}}");
        JSONObject checkAnswer = CMSPTasksDoer.cmspCommunicator.checkAnswer(task, this, answer);
        correctAnswer = new JSONArray(sentencesString);
    }
    
    private void fillWordsAnswer(){
        System.out.println(task.getTitle());
        JSONArray items = options.getJSONArray("items");
        List<Object> itemsString = items.toList();
        Collections.shuffle(itemsString);
        JSONObject answer = new JSONObject("{\"answer\":{\"answer\":"+new JSONArray(itemsString).toString()+"}}");
        JSONObject checkAnswer = CMSPTasksDoer.cmspCommunicator.checkAnswer(task, this, answer);
        correctAnswer = new JSONArray(itemsString);
    }
    
    private void skipQuestion(){
        skipQuestion("");
    }
    
    private void skipQuestion(String cause){
        skipQuestion = true;
        if(isRequired()){
            task.setShouldSaveAsADraft(true);
        }
        System.out.println("Questão id:"+getId()+" da tarefa com o id: " + task.getId() + " pulada. Motivo: " + (cause.isEmpty() ? "Not Specified" : cause));
    }
    
    private void trueFalseAnswer(){
        multiQuestionAnswer();
    }
    
    public int getId(){
        return id;
    }

    public Types getTypeT(){
        return typeT;
    }

    public boolean isRequired(){
        return required;
    }

    public JSONObject getOptions(){
        return options;
    }

    public Object getCorrectAnswer(){
        return correctAnswer;
    }

    public void setCorrectAnswer(Object correctAnswer){
        this.correctAnswer = correctAnswer;
    }

    public boolean isSkipQuestion(){
        return skipQuestion;
    }

    public void setSkipQuestion(boolean skipQuestion){
        this.skipQuestion = skipQuestion;
    }
    
    
}
