/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vitorsantana.cmsptasksdoer.cmspobjects;

import com.vitorsantana.cmsptasksdoer.CMSPTasksDoer;
import java.util.ArrayList;
import java.util.Random;
import org.json.JSONObject;

/**
 *
 * @author vitor
 */
public class Task{
    
    private int id;
    private String title;
    private boolean isExam;
    private boolean isEssay;
    private ArrayList<Question> questions = new ArrayList<>();
    private String publicationTarget = "";

    public Task(int id, String title, boolean isExam, boolean isEssay, String publicationTarget){
        this.id = id;
        this.title = title;
        this.isExam = isExam;
        this.isEssay = isEssay;
        this.publicationTarget = publicationTarget;
    }
    
    public void submitTask(){
        JSONObject answerForm = new JSONObject();
        JSONObject answers = new JSONObject();
        answerForm.put("duration", new Random(System.currentTimeMillis()).nextDouble(30*questions.size(), 40*questions.size()));
        questions.forEach((question) -> {
            if(question.getTypeT().equals(Question.Types.info)){
                return;
            }
            JSONObject answerData = new JSONObject();
            //JSONObject answer = new JSONObject();
            answerData.put("answer", question.getCorrectAnswer());
            answerData.put("question_type", question.type);
            answerData.put("question_id", ""+question.getId());
            
            //answer.append("answer", question.getCorrectAnswer());
            
            
            answers.put(""+question.getId(),answerData);
            
        });
        
        
        answerForm.put("answers", answers).put("executed_on", publicationTarget).put("accessed_on", "room").put("status", "submitted");
        //System.out.println(answerForm.toString());
        CMSPTasksDoer.cmspCommunicator.sendTaskSubmition(this, answerForm);
    }
    

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getTitle(){
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public boolean isIsExam(){
        return isExam;
    }

    public void setIsExam(boolean isExam){
        this.isExam = isExam;
    }

    public boolean isIsEssay(){
        return isEssay;
    }

    public void setIsEssay(boolean isEssay){
        this.isEssay = isEssay;
    }

    public ArrayList<Question> getQuestions(){
        return questions;
    }

    public void setQuestions(ArrayList<Question> questions){
        this.questions = questions;
    }
    
    
    
}
