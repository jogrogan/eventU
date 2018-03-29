package com.eventu;

import java.util.ArrayList;
import java.util.Arrays;

public class ClubPageInfo {
    private String clubDescription;
    private String clubName;
    private String clubContact;
    private ArrayList<String> clubSocial;
    private String clubWebsite;
    private String clubID;

    public ClubPageInfo() {
    }

    public ClubPageInfo(String name, String id) {
        clubDescription = "";
        clubName = name;
        clubContact = "";
        clubSocial = new ArrayList<>();
        clubWebsite = "";
        clubID = id;
    }

    public String getClubDescription() {
        return clubDescription;
    }

    public void setClubDescription(String d) {
        clubDescription = d;
    }

    public String getClubName() {
        return clubName;
    }

    public String getClubContact() {
        return clubContact;
    }

    void setClubContact(String c) {
        clubContact = c;
    }

    public ArrayList<String> getClubSocial() {
        return new ArrayList<>(clubSocial);
    }

    public void setClubSocial(String s) {
        String temp[] = s.split("\n");
        clubSocial.clear();
        clubSocial.addAll(Arrays.asList(temp));
    }

    public String getClubWebsite() {
        return clubWebsite;
    }

    public void setClubWebsite(String w) {
        clubWebsite = w;
    }

    public String getClubID() {
        return clubID;
    }
}
