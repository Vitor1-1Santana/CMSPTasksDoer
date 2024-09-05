/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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
    
    public enum Types{info, multi, single, cloud, order_sentences, fill_words, essay, text, true_false, text_ai}
    
    private int id;
    private Types typeT;
    String type;
    private boolean required;
    private JSONObject options;
    private Task task;
    private Object correctAnswer;
    

    public Question(int id, String type, boolean required, JSONObject options, Task task){
        this.id = id;
        this.type = type;
        this.typeT = Types.valueOf(type.replace("-", "_"));
        this.required = required;
        this.options = options;
        this.task = task;
    }
    
    public void answerQuestion(){
        if(task.isAbort()){
            return;
        }
        
        switch(typeT){
            case single -> singleQuestionAnswerer();
            case multi -> multiQuestionAnswer();
            case cloud -> cloudQuestionAnswer();
            case order_sentences -> orderSentencesAnswer();
            case fill_words -> fillWordsAnswer();
            case true_false -> trueFalseAnswer();
            case text_ai -> task.abortTask();
            case text -> task.abortTask();
            default -> {
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
    
    
}
