package com.eventu;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Class to include all user info stored inside the firestore database
 */

public class UserInfo implements Serializable {
    @ServerTimestamp
    private Date accountCreation;

    private String email;
    private ArrayList<String> favorites;

    @ServerTimestamp
    private Date lastLogin;

    private String username;
    private String schoolName;
    private String userID;
    private boolean isClub;

    public UserInfo() {
    }

    public UserInfo(String email, ArrayList<String> favorites, String username, String schoolName,
            String userID, boolean isClub) {
        this.email = email;
        this.favorites = new ArrayList<String>(favorites);
        this.username = username;
        this.schoolName = schoolName;
        this.userID = userID;
        this.isClub = isClub;
    }

    public Date getAccountCreation() {
        return accountCreation;
    }

    public String getEmail() {
        return email;
    }

    public ArrayList<String> getFavorites() {
        return new ArrayList<String>(favorites);
    }

    public Date getLastLogin() {
        return lastLogin;
    }

    public String getUsername() {
        return username;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public String getUserID() {
        return userID;
    }

    public boolean isClub() {
        return isClub;
    }
}
