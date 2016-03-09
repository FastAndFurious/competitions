package com.zuehlke.carrera.comp.domain;


public class ApplicationNotification {

    private String competition;
    private String teamName;
    private Long sessionId;

    public ApplicationNotification() {}

    public ApplicationNotification(String competition, String teamName, Long sessionId ) {
        this.competition = competition;
        this.teamName = teamName;
        this.sessionId = sessionId;
    }

    public String getCompetition() {
        return competition;
    }

    public void setCompetition(String competition) {
        this.competition = competition;
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
