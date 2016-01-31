package com.zuehlke.carrera.comp.domain;


public class RunMissedNotification {

    private String teamName;
    private Long sessionId;

    public RunMissedNotification() {
    }

    public RunMissedNotification(String teamName, Long sessionId ) {
        this.teamName = teamName;
        this.sessionId = sessionId;
    }

    public String getTeamName() {
        return teamName;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }
}
