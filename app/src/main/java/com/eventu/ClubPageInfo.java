package com.eventu;

import java.io.Serializable;
import java.util.ArrayList;

public class ClubPageInfo implements Serializable {
    private String ClubDescription;
    private String ClubName;
    private String ClubContact;
    private ArrayList<String> ClubSocial;
    private String ClubWebsite;
    private String ClubID;


    public ClubPageInfo(String n, String i) {
        ClubDescription = "";
        ClubName = n;
        ClubContact = "";
        ClubSocial = new ArrayList<>();
        ClubWebsite = "";
        ClubID = i;
    }

    String getClubDescription() {
        return ClubDescription;
    }

    void setClubDescription(String d) {
        ClubDescription = d;
    }

    String getClubName() {
        return ClubName;
    }

    String getClubContact() {
        return ClubContact;
    }

    void setClubContact(String c) {
        ClubContact = c;
    }

    ArrayList<String> getClubSocial() {
        return ClubSocial;
    }

    void setClubSocial(String s) {
        String temp[] = s.split("\n");
        for (int i = 0; i < temp.length; i++) {
            ClubSocial.set(i, temp[i]);
        }
    }

    String getClubWebsite() {
        return ClubWebsite;
    }

    void setClubWebsite(String w) {
        ClubWebsite = w;
    }

    String getClubID() {
        return ClubID;
    }
}
