package com.eventu.login_and_registration.school_selection;


import java.util.ArrayList;
import java.util.Collections;

/**
 * Used to turn the JSON found within the University Domains and Names Data API
 * into an object with a name and domains field
 */
public class SchoolInfo {

    private String name;
    private ArrayList<String> domains;

    public SchoolInfo() {
        this.name = "";
        this.domains = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getDomains() {
        return new ArrayList<>(domains);
    }

    public void setDomains(ArrayList<String> domains) {
        this.domains = new ArrayList<>(domains);
    }

    /**
     * Two SchoolInfo objects are equal if all fields are equal.
     */
    public boolean equals(Object other) {
        if (!(other instanceof SchoolInfo)) {
            return false;
        }
        SchoolInfo school = (SchoolInfo) other;
        if (!name.equals(school.getName())) {
            return false;
        }

        ArrayList<String> myDomains = getDomains();
        ArrayList<String> otherDomains = school.getDomains();
        Collections.sort(myDomains);
        Collections.sort(otherDomains);
        return myDomains.toString().equals(otherDomains.toString());
    }
}