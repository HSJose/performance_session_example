package com.performance.headspin;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Sessions {

    @JsonProperty("sessions")
    private List<Session> Sessions;

    public List<Session> getSessions() {
        return this.Sessions;
    }

    public void setSessions(List<Session> Sessions) {
        this.Sessions = Sessions;
    }

    @Override
    public String toString() {
        return "{" +
            " Sessions='" + getSessions() + "'" +
            "}";
    }

}
