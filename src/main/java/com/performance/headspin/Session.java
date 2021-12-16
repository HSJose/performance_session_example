package com.performance.headspin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Session {

    @JsonProperty("companion_id")
    private int companion_id;

    @JsonProperty("session_type")
    private String session_type;

    @JsonProperty("start_time")
    private float start_time;

    @JsonProperty("state")
    private String state;

    @JsonProperty("session_id")
    private String session_id;

    @JsonProperty("device_id")
    private String device_id;




    public int getCompanion_id() {
        return this.companion_id;
    }

    public void setCompanion_id(int companion_id) {
        this.companion_id = companion_id;
    }

    public String getSession_type() {
        return this.session_type;
    }

    public void setSession_type(String session_type) {
        this.session_type = session_type;
    }

    public float getStart_time() {
        return this.start_time;
    }

    public void setStart_time(float start_time) {
        this.start_time = start_time;
    }

    public String getState() {
        return this.state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSession_id() {
        return this.session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }

    public String getDevice_id() {
        return this.device_id;
    }

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }


    @Override
    public String toString() {
        return "{" +
            " companion_id='" + getCompanion_id() + "'" +
            ", session_type='" + getSession_type() + "'" +
            ", start_time='" + getStart_time() + "'" +
            ", state='" + getState() + "'" +
            ", session_id='" + getSession_id() + "'" +
            ", device_id='" + getDevice_id() + "'" +
            "}";
    }

}
