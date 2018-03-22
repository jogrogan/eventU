package com.eventu.login_and_registration.school_selection;

import java.util.ArrayList;

/**
 * Used to turn the JSON found within the University Domains and Names Data API
 * into an object with a name and domains field
 */
public class SchoolInfo {

    private String name;
    private ArrayList<String> domains;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getDomains() {
        return domains;
    }

    public void setDomains(ArrayList<String> domains) {
        this.domains = new ArrayList<>(domains);
    }
}