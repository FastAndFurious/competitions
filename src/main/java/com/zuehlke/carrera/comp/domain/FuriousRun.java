package com.zuehlke.carrera.comp.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zuehlke.carrera.comp.domain.util.CustomDateTimeDeserializer;
import com.zuehlke.carrera.comp.domain.util.CustomDateTimeSerializer;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Single run usually consisting of a number of laps.
 */
@Entity
@Table(name = "FURIOUS_RUNS")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class FuriousRun implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "team", nullable = false)
    private String team;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "scheduled_start_date", nullable = false)
    private LocalDateTime scheduledStartDate;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate;

    private Long sessionId;

    private Long competitionId;

    @Enumerated(EnumType.STRING)
    private Status status;

    public FuriousRun () {} // for JPA

    public FuriousRun(String team, LocalDateTime scheduledStartDate, LocalDateTime startDate, Long sessionId, Long competitionId, Status status) {
        this.team = team;
        this.scheduledStartDate = scheduledStartDate;
        this.startDate = startDate;
        this.sessionId = sessionId;
        this.competitionId = competitionId;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String name) {
        this.team = name;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getScheduledStartDate() {
        return scheduledStartDate;
    }

    public void setScheduledStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public void setCompetitionId(Long competitionId) {
        this.competitionId = competitionId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FuriousRun competition = (FuriousRun) o;

        return Objects.equals(id, competition.id);

    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Competition{" +
                "id=" + id +
                ", team='" + team + "'" +
                ", startDate='" + startDate + "'" +
                '}';
    }

    /**
     *  the current status of a particular
     */
    public static enum Status {
        SCHEDULED,
        ONGOING,
        SUSPENDED,
        QUALIFIED,
        DISQUALIFIED
    }
}
