package com.zuehlke.carrera.comp.web.rest;

/**
 *  represents a run request from the competition manager
 */
public class RunRequest {

    private String team;
    private String accessCode;
    private String track;
    private String description;
    private Long furiousRunId;

    public RunRequest(String team, String accessCode, String track, String description, Long furiousRunId) {
        this.team = team;
        this.accessCode = accessCode;
        this.track = track;
        this.description = description;
        this.furiousRunId = furiousRunId;
    }

    public RunRequest() {} // for Serialization

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getFuriousRunId() {
        return furiousRunId;
    }

    public void setFuriousRunId(Long furiousRunId) {
        this.furiousRunId = furiousRunId;
    }
}
