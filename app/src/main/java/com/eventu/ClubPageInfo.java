package com.eventu;

import java.io.Serializable;
import java.util.ArrayList;

public class ClubPageInfo implements Serializable {
    private String ClubDescription;
    private String ClubName;
    private String ClubContant;
    private ArrayList<String> ClubSocial;
    private String ClubWebsite;
    private String ClubID;


    public ClubPageInfo(String n, String i) {
        ClubDescription = "";
        ClubName = n;
        ClubContant = "";
        ClubSocial = new ArrayList<>();
        ClubWebsite = "";
        ClubID = i;
    }

    String getDescription() {
        return ClubDescription;
    }

    void setDescription(String d) {
        ClubDescription = d;
    }

    String getName() {
        return ClubName;
    }

    String getContact() {
        return ClubContant;
    }

    void setContact(String c) {
        ClubContant = c;
    }

    ArrayList<String> getSocial() {
        return ClubSocial;
    }

    void setSocial(String s) {
        String temp[] = s.split("\n");
        for (int i = 0; i < temp.length; i++) {
            ClubSocial.set(i, temp[i]);
        }
    }

    String getWebsite() {
        return ClubWebsite;
    }

    void setWebsite(String w) {
        ClubWebsite = w;
    }

    String getId() {
        return ClubID;
    }
}
