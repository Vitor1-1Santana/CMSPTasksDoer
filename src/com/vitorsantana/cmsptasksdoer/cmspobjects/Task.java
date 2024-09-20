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
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    private boolean abort = false;
    private boolean shouldSaveAsADraft = true;

    public Task(int id, String title, boolean isExam, boolean isEssay, String publicationTarget){
        this.id = id;
        this.title = title;
        this.isExam = isExam;
        this.isEssay = isEssay;
        this.publicationTarget = publicationTarget;
    }
    
    public void submitTask(){
        if(abort){
            return;
        }
        
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
        
        
        answerForm.put("answers", answers).put("executed_on", publicationTarget).put("accessed_on", "room").put("status", shouldSaveAsADraft ? "draft" : "submitted");
        System.out.println(answerForm.toString());
        CMSPTasksDoer.cmspCommunicator.sendTaskSubmition(this, answerForm);
        
    }
    
    public void submitTaskAsADraft(){
        
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
    
    public void abortTask(){
        abort = true;
    }

    public boolean isAbort(){
        return abort;
    }

    public boolean isShouldSaveAsADraft(){
        return shouldSaveAsADraft;
    }

    public void setShouldSaveAsADraft(boolean shouldSaveAsADraft){
        this.shouldSaveAsADraft = shouldSaveAsADraft;
    }
    
    
    
}
