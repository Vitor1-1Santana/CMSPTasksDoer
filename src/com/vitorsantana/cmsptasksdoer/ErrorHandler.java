/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vitorsantana.cmsptasksdoer;

import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author vitor
 */
public class ErrorHandler{
    
    public static void handleException(String name, Exception ex, String message){
            if(name.contains(CmspCommunicator.class.getName())){
                JOptionPane.showMessageDialog(null, message.concat(ex.toString()), "Falha", JOptionPane.ERROR_MESSAGE);
            }
    }
    
}
