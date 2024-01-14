package org.example.model;

import org.example.model.ElementState;

public class ActionEvent {
    private String  eventID;
    private ElementState eventState;

    public ActionEvent() {
    }

    public ActionEvent(String eventID, ElementState eventState) {
        this.eventID = eventID;
        this.eventState = eventState;
    }

    public String getEventID() {
        return eventID;
    }

    public void setEventID(String eventID) {
        this.eventID = eventID;
    }

    public ElementState getEventState() {
        return eventState;
    }

    public void setEventState(ElementState eventState) {
        this.eventState = eventState;
    }
}
