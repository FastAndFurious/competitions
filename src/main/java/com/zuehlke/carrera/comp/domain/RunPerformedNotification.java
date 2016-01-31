package com.zuehlke.carrera.comp.domain;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zuehlke.carrera.comp.domain.util.CustomDateTimeDeserializer;
import com.zuehlke.carrera.comp.domain.util.CustomDateTimeSerializer;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;

import javax.persistence.*;

public class RunPerformedNotification {

    private String teamName;
    private Long sessionId;

    public RunPerformedNotification() {
    }

    public RunPerformedNotification(String teamName, Long sessionId ) {
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
