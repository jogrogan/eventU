package com.eventu;

import android.support.annotation.NonNull;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Class to store all Event Information
 */
class EventInfo implements Comparable<EventInfo> {
    private String EventName;
    private String EventDescription;
    private String EventLocation;
    private String EventCreator;
    private Date EventDate;
    private String EventID;

    public EventInfo() {
    }

    public EventInfo(String eventName, String eventDescription, String eventLocation,
            String eventCreator, Timestamp eventDate) {
        EventName = eventName;
        EventDescription = eventDescription;
        EventLocation = eventLocation;
        EventCreator = eventCreator;
        EventDate = eventDate;
    }

    //Getters for all EventInfo fields
    String getEventName() {
        return EventName;
    }

    String getEventDescription() {
        return EventDescription;
    }
    String getEventLocation() {
        return EventLocation;
    }
    String getEventCreator() {
        return EventCreator;
    }
    Date getEventDate() {
        return EventDate;
    }
    String getEventID() {
        return EventID;
    }
    void setEventID(String eventID) {
        this.EventID = eventID;
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
