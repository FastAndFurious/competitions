package com.zuehlke.carrera.comp.domain;

/**
 *  represents the best result of a team in a particular session
 */
public class RoundResult {

    private String team;
    private Long sessionId;
    private String competion;
    private Long duration;

    public RoundResult(String team, Long sessionId, String competion, Long duration) {
        this.team = team;
        this.sessionId = sessionId;
        this.competion = competion;
        this.duration = duration;
    }

    public RoundResult(){} // for serialization

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public String getCompetion() {
        return competion;
    }

    public void setCompetion(String competion) {
        this.competion = competion;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }
}
