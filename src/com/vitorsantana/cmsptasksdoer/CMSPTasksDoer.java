/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.vitorsantana.cmsptasksdoer;

import com.vitorsantana.cmsptasksdoer.cmspobjects.Task;
import com.vitorsantana.cmsptasksdoer.cmspobjects.User;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author vitor
 */
public class CMSPTasksDoer{
    
    public static CmspCommunicator cmspCommunicator;
    private static LoginUI loginUI;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args){
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
    
    private static void loginUser(int ra, char digit, char[] password) {
        loginUI.errorLabel.setText("");
        try{
            User user = cmspCommunicator.loginToCmsp(ra, digit, password);
            LoginWarning loginWarning = new LoginWarning(loginUI, true);
            loginWarning.setNameAndNick(user.getName(), user.getNick());
            loginWarning.doTasks.addActionListener((e) -> {
                new Thread(){
                    @Override
                    public void run(){
                        
                        
                        Iterator<Task> iterator = cmspCommunicator.taskList.iterator();
                        while(iterator.hasNext()){
                            Task task = iterator.next();
                            if(task.isIsEssay() || task.isIsExam()){
                                System.out.println("NOT DOING THIS TASK: " + "isEssay: " + task.isIsEssay() + " isExam: " + task.isIsExam() + " title: " + task.getTitle());
                                iterator.remove();
                                continue;
                            }
                            loginWarning.progressBar.setMaximum(task.getQuestions().size()-1);
                            task.getQuestions().iterator().forEachRemaining((question) -> {
                                loginWarning.progressInfo.setText("Respondendo questão do ID: " + question.getId() + " " + task.getQuestions().indexOf(question) + "/" + (task.getQuestions().size()-1));
                                loginWarning.progressBar.setValue(task.getQuestions().indexOf(question));
                                question.answerQuestion();
                            });
                            task.submitTask();
                            iterator.remove();
                            loginWarning.setNameAndNick(user.getName(), user.getNick());
                            System.out.println("Task " + task.getTitle() + " submitted! :D");
                        }
                        
                        
                        
                    }
                    
                    
                }.start();
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
    
}
