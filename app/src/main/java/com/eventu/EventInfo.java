package com.eventu;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Class to store all Event Information
 */
class EventInfo {
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

    private String getEventID() {
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
}
