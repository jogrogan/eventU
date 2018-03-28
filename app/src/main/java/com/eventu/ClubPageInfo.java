package com.eventu;

import java.util.ArrayList;

public class ClubPageInfo {
    private String ClubDescription;
    private String ClubName;
    private String ClubContact;
    private ArrayList<String> ClubSocial;
    private String ClubWebsite;
    private String ClubID;

    public ClubPageInfo() {
    }

    public ClubPageInfo(String description, String name, String contact, ArrayList<String> social,
            String website, String id) {
        ClubDescription = description;
        ClubName = name;
        ClubContact = contact;
        ClubSocial = new ArrayList<>(social);
        ClubWebsite = website;
        ClubID = id;
    }

    public String getClubDescription() {
        return ClubDescription;
    }

    public void setClubDescription(String d) {
        ClubDescription = d;
    }

    public String getClubName() {
        return ClubName;
    }

    public String getClubContact() {
        return ClubContact;
    }

    void setClubContact(String c) {
        ClubContact = c;
    }

    public ArrayList<String> getClubSocial() {
        return ClubSocial;
    }

    public void setClubSocial(String s) {
        String temp[] = s.split("\n");
        for (int i = 0; i < temp.length; i++) {
            ClubSocial.set(i, temp[i]);
        }
    }

    public String getClubWebsite() {
        return ClubWebsite;
    }

    public void setClubWebsite(String w) {
        ClubWebsite = w;
    }

    public String getClubID() {
        return ClubID;
    }
}
