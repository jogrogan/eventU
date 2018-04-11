package com.eventu;

import java.util.ArrayList;
import java.util.Arrays;

public class ClubPageInfo {
    private String ClubDescription;
    private String ClubName;
    private String ClubContact;
    private ArrayList<String> ClubSocial;
    private String ClubWebsite;
    private String clubID;

    public ClubPageInfo() {
    }

    public ClubPageInfo(String name, String id) {
        ClubDescription = "";
        ClubName = name;
        ClubContact = "";
        ClubSocial = new ArrayList<>();
        ClubWebsite = "";
        clubID = id;
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
        return new ArrayList<>(ClubSocial);
    }

    public void setClubSocial(String s) {
        String temp[] = s.split("\n");
        ClubSocial.clear();
        ClubSocial.addAll(Arrays.asList(temp));
    }

    public String getClubWebsite() {
        return ClubWebsite;
    }

    public void setClubWebsite(String w) {
        ClubWebsite = w;
    }

    public String getClubID() {
        return clubID;
    }
}
