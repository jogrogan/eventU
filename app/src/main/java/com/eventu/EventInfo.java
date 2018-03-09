package com.eventu;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Class to store all EventInformation
 */

public class EventInfo {
    private String EventName;
    private String EventDescription;
    private String EventLocation;
    private String EventCreator;
    private Date EventDate;

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
}
