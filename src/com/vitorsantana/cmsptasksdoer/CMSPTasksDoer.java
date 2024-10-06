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

import com.vitorsantana.cmsptasksdoer.cmspobjects.Task;
import com.vitorsantana.cmsptasksdoer.cmspobjects.User;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JOptionPane;

/**
 *
 * @author vitor
 */
public class CMSPTasksDoer{
    
    public static CmspCommunicator cmspCommunicator;
    private static LoginUI loginUI;
    private static boolean doTasksBoolean = false;
    private static ArrayList<Task> draftedTasks = new ArrayList();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
        System.out.println("CMSPTasksDoer Version 1.0.2");
        cmspCommunicator = new CmspCommunicator();
        loginUI = new LoginUI();
        loginUI.setVisible(true);
        if(args.length >= 2){
            loginUser(Integer.parseInt(args[0]), args[1].charAt(0), args[2].toCharArray());
            
        }
        
        loginUI.password.addActionListener((e) -> {
        
            loginUser(Integer.parseInt(loginUI.raNumber.getText()), loginUI.digitNumber.getText().charAt(0), loginUI.password.getPassword());
        });
        loginUI.enterUserButton.addActionListener((e) -> {
            loginUser(Integer.parseInt(loginUI.raNumber.getText()), loginUI.digitNumber.getText().charAt(0), loginUI.password.getPassword());
        });
    }
    
    /**
     * 
     * This method calls the cmspCommunicator, and sends the credentials to the iptv servers,
     * after this, it returns a User, the User object is used to set the Name, Nickname and 
     * number of tasks in the loginWarning UI.
     * 
     * @param ra
     * @param digit
     * @param password 
     */
    private static void loginUser(int ra, char digit, char[] password) {
        loginUI.errorLabel.setText("");
        try{
            User user = cmspCommunicator.loginToCmsp(ra, digit, password);
            LoginWarning loginWarning = new LoginWarning(loginUI, true);
            loginWarning.setNameAndNick(user.getName(), user.getNick());
            loginWarning.cancelButton.addActionListener((e) -> {
                doTasksBoolean = false;
                loginWarning.dispose();
            });
            loginWarning.doTasks.addActionListener((e) -> {
                doTasks(loginWarning, user);
            });
            loginWarning.setVisible(true);
        }catch(Exception ex){
            Logger.getLogger(CMSPTasksDoer.class.getName()).log(Level.SEVERE, null, ex);
            if(ex.getMessage().equals("Wrong Credentials")){
                loginUI.errorLabel.setText("Credenciais Incorretas");
            }else{
                loginUI.errorLabel.setText("Falha ao fazer login :(");
    }
    
        }
    }
    
    /**
     * 
     * This method iterates all the tasks and all the questions inside tasks,
     * and then, call the method answerQuestion() inside the question.
     * After all the questions are answered, the submitTask() is called, submitting
     * the task to the iptv servers. After submitted the task is removed from the task list.
     * 
     * @param loginWarning
     * @param user 
     */
    private static void doTasks(LoginWarning loginWarning, User user){
        doTasksBoolean = !doTasksBoolean;
        if(!doTasksBoolean){
            loginWarning.doTasks.setText("Parando...");
            loginWarning.doTasks.setEnabled(false);
            return;
        }
            new Thread(){
                @Override
                public void run(){
                    
                    Iterator<Task> iterator = cmspCommunicator.taskList.iterator();
                    while(iterator.hasNext() && doTasksBoolean){
                        loginWarning.doTasks.setText("Parar");
                        if(!iterator.hasNext()){
                            doTasksBoolean = !doTasksBoolean;
                        }
                        Task task = iterator.next();
                        if(task.isIsEssay() || task.isIsExam() || (!task.isIsAllowCheckAnswer() && Options.precisionLevel == 0)){
                            System.out.println("NOT DOING THIS TASK: " + "isEssay: " + task.isIsEssay() + " isExam: " + task.isIsExam() + " hasVerifyButton: " + !task.isIsAllowCheckAnswer() + " Title: " + task.getTitle());
                            iterator.remove();
                            continue;
                        }
                        
                        loginWarning.progressInfo.setText("Respondendo tarefa: "+task.getTitle());
                        loginWarning.progressBar.setMaximum(task.getQuestions().size()-1);
                        task.getQuestions().iterator().forEachRemaining((question) -> {
                            if(doTasksBoolean){
                                loginWarning.progressBar.setString("Respondendo questão do ID: " + question.getId() + " " + task.getQuestions().indexOf(question) + "/" + (task.getQuestions().size()-1));
                                loginWarning.progressBar.setValue(task.getQuestions().indexOf(question));
                                if(!question.isSkipQuestion()){
                                    question.answerQuestion();
                                }else{
                                    System.out.println("SKIPPING QUESTION: " + question.getId());
                                }

                            }
                        });
                        if(!doTasksBoolean){
                            break;
                        }
                        if(!task.isShouldSaveAsADraft()){
                            task.submitTask();
                        }else{
                            draftedTasks.add(task);
                            task.submitTask();
                            
                        }

                        loginWarning.setNameAndNick(user.getName(), user.getNick());
                        if(task.isAbort()){
                            System.out.println("TASK HAS UNRECOGNIZED QUESTION! NOT DOING TASK: " + task.getTitle());
                        }else{
                            System.out.println("Task " + task.getTitle() + " submitted! :D");
                        }

                        iterator.remove();
                        try{
                            Thread.sleep(Options.cooldownTimeBetweenTasks);
                        }catch(InterruptedException ex){
                            Logger.getLogger(Task.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    
                    if (doTasksBoolean) {
                        loginWarning.progressInfo.setText("FINALIZADO :D");
                        loginWarning.progressBar.setString("FINALIZADO :D");
                        Toolkit.getDefaultToolkit().beep();
                        if(!draftedTasks.isEmpty()){
                            new DraftedTasks(loginUI, true, draftedTasks).setVisible(true);
                        }
                    } else {
                        loginWarning.progressInfo.setText("INTERROMPIDO");
                        loginWarning.progressBar.setString("INTERROMPIDO");
                    }
                    loginWarning.doTasks.setEnabled(true);
                    loginWarning.doTasks.setText("Realizar tarefas");
                }


            }.start();
            
    }
    
    public static void addTaskById(long id){
        cmspCommunicator.addTaskById(id);
    }
    
}
