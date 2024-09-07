/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vitorsantana.cmsptasksdoer;

/**
 *
 * @author vitor
 */
public class HttpClientException extends Exception{

    public HttpClientException(){
    }

    public HttpClientException(String message){
        super(message);
    }

    public HttpClientException(String message, Throwable cause){
        super(message, cause);
    }

    public HttpClientException(Throwable cause){
        super(cause);
    }

    public HttpClientException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace){
        super(message, cause, enableSuppression, writableStackTrace);
    }
    
    
    
}
