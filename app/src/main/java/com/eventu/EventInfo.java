package com.eventu;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Class to store all Event Information
 */
class EventInfo implements Comparable<EventInfo>, Serializable {
    private String EventName;
    private String EventDescription;
    private String EventLocation;
    private String EventCreator;
    private Date EventDate;
    private String EventID;
    private int EventTally;
    private String clubID;

    public EventInfo() {
    }

    public EventInfo(String eventName, String eventDescription, String eventLocation,
            String eventCreator, Timestamp eventDate, int eventTally, String clubID) {
        EventName = eventName;
        EventDescription = eventDescription;
        EventLocation = eventLocation;
        EventCreator = eventCreator;
        EventDate = eventDate;
        EventTally = eventTally;
        this.clubID = clubID;
    }

    //Getters for all EventInfo fields
    public String getEventName() {
        return EventName;
    }

    public String getEventDescription() {
        return EventDescription;
    }

    public String getEventLocation() {
        return EventLocation;
    }

    public String getEventCreator() {
        return EventCreator;
    }

    public Date getEventDate() {
        return EventDate;
    }

    public String getEventID() {
        return EventID;
    }

    public void setEventID(String eventID) {
        this.EventID = eventID;
    }

    public int getEventTally() {
        return EventTally;
    }

    public String getClubID() {
        return clubID;
    }

    /**
     * Increase the tally count for this event
     */
    public void increaseTallyCount(String schoolName) {
        EventTally++;
        updateEventTally(schoolName);
    }

    /**
     * Decrease the tally count for this event
     */
    public void decreaseTallyCount(String schoolName) {
        EventTally--;
        updateEventTally(schoolName);
    }

    /**
     * Update the given event's favorite tally count
     */
    public void updateEventTally(String schoolName) {
        DocumentReference doc
                = FirebaseFirestore.getInstance().collection(
                "universities")
                .document(schoolName).collection("Club Events")
                .document(EventID);
        doc.update("EventTally", EventTally);
    }

    /**
     * Two EventInfo objects are equal if the IDs of both of the events are the same.
     */
    public boolean equals(Object object) {
        return (object instanceof EventInfo && EventID.equals(((EventInfo) object).getEventID()));
    }

    /**
     * compareTo needs to be defined in order to be able to sort EventInfo objects.
     * An EventInfo object is defined as "less than" another EventInfo object if its date is before
     * the other's.
     */
    public int compareTo(@NonNull EventInfo e) {
        return EventDate.compareTo(e.EventDate);
    }
}
