package com.eventu;

import java.io.Serializable;
import java.util.ArrayList;

public class ClubPage implements Serializable{
    private String description;
    private String name;
    private String contact;
    private ArrayList<String> social;
    private String website;
    private String id;


    public ClubPage(String n, String i){
        description = "";
        name = n;
        contact = "";
        website = "";
        id = i;
        social = new ArrayList<>();
    }

    String getDescription(){return description;}

    String getName(){return name;}

    String getContact(){return contact;}

    ArrayList<String> getSocial(){return social;}

    String getWebsite(){return website;}

    String getId(){return id;}

    void setDescription(String d){description = d;}

    void setContact(String c){contact = c;}

    void setWebsite(String w){website = w;}

    void setSocial(String s){
        String temp[] = s.split("\n");
        for (int i = 0; i < temp.length; i++){
            social.set(i, temp[i]);
        }
    }
}
