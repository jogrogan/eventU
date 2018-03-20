package com.eventu;

import com.google.firebase.firestore.ServerTimestamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Class to include all user info stored inside the Firestore database
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
    private boolean club;

    public UserInfo() {
    }

    public UserInfo(String email, ArrayList<String> favorites, String username, String schoolName,
            String userID, boolean club) {
        this.email = email;
        this.favorites = new ArrayList<>(favorites);
        this.username = username;
        this.schoolName = schoolName;
        this.userID = userID;
        this.club = club;
    }

    public Date getAccountCreation() {
        return accountCreation;
    }

    public String getEmail() {
        return email;
    }

    public ArrayList<String> getFavorites() {
        return new ArrayList<>(favorites);
    }

    /**
     * Adds the input string to the ArrayList of favorites if not already present.
     * Returns true if successfully added, false otherwise.
     */
    boolean addFavorite(String favorite) {
        return !favorites.contains(favorite) && favorites.add(favorite);
    }

    /**
     * Removes the input string from the ArrayList of favorites.
     * Returns true if successfully removed, false otherwise.
     */
    boolean removeFavorite(String favorite) {
        return favorites.contains(favorite) && favorites.remove(favorite);
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

    public boolean getClub() {
        return club;
    }
}
