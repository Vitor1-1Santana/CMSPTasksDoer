/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package com.vitorsantana.cmsptasksdoer;

import com.vitorsantana.cmsptasksdoer.cmspobjects.Task;
import com.vitorsantana.cmsptasksdoer.cmspobjects.User;
import java.awt.Toolkit;
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
                new Thread(){
                    @Override
                    public void run(){
                        
                        Iterator<Task> iterator = cmspCommunicator.taskList.iterator();
                        while(iterator.hasNext() && doTasksBoolean){
                            loginWarning.doTasks.setText("Parar");
                            Task task = iterator.next();
                            if(task.isIsEssay() || task.isIsExam()){
                                System.out.println("NOT DOING THIS TASK: " + "isEssay: " + task.isIsEssay() + " isExam: " + task.isIsExam() + " title: " + task.getTitle());
                                iterator.remove();
                                continue;
                            }
                            loginWarning.progressInfo.setText("Respondendo tarefa: "+task.getTitle());
                            loginWarning.progressBar.setMaximum(task.getQuestions().size()-1);
                            task.getQuestions().iterator().forEachRemaining((question) -> {
                                loginWarning.progressBar.setString("Respondendo questão do ID: " + question.getId() + " " + task.getQuestions().indexOf(question) + "/" + (task.getQuestions().size()-1));
                                loginWarning.progressBar.setValue(task.getQuestions().indexOf(question));
                                question.answerQuestion();
                            });
                            
                            task.submitTask();
                            iterator.remove();
                            loginWarning.setNameAndNick(user.getName(), user.getNick());
                            if(task.isAbort()){
                                System.out.println("TASK HAS TEXT QUESTION! NOT DOING TASK: " + task.getTitle());
                            }else{
                                System.out.println("Task " + task.getTitle() + " submitted! :D");
                            }
                        }
                        loginWarning.progressInfo.setText("FINALIZADO :D");
                        loginWarning.progressBar.setString("FINALIZADO :D");
                        Toolkit.getDefaultToolkit().beep();
                        
                        loginWarning.doTasks.setText("Realizar tarefas");
                    }
                    
                    
                }.start();
    }
    
}
