/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.vitorsantana.cmsptasksdoer.cmspobjects;

/**
 *
 * @author vitor
 */
public class User{
    
    private String name;
    private String nick;
    private int tasksToDo = 0;
    private String room;

    public User(String name, String nick){
        this.name = name;
        this.nick = nick;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getNick(){
        return nick;
    }

    public void setNick(String nick){
        this.nick = nick;
    }

    public String getRoom(){
        return room;
    }

    public void setRoom(String room){
        this.room = room;
    }
    
}
