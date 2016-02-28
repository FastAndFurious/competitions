package com.zuehlke.carrera.comp.domain;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zuehlke.carrera.comp.domain.util.CustomDateTimeDeserializer;
import com.zuehlke.carrera.comp.domain.util.CustomDateTimeSerializer;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;

import javax.persistence.*;

/**
 *  A team applies for one of the next runs of a particular session
 */
@Entity
@Table( name = "T_TRAINING_APPLICATIONS")
public class TrainingApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String teamName;
    private Long sessionId;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "application_time", nullable = false)
    private LocalDateTime applicationTime;

    @Column(name="number_of_missed_runs")
    private int numberOfMissedRuns;

    @Column(name="number_of_performed_runs")
    private int numberOfPerformedRuns;

    @Enumerated(EnumType.STRING)
    @Column(name="guaranteed_position")
    private GuaranteedPosition guaranteedPosition = GuaranteedPosition.NONE;

    public TrainingApplication () {} // for Hibernate

    public TrainingApplication(String teamName, Long sessionId, LocalDateTime applicationTime ) {
        this.teamName = teamName;
        this.sessionId = sessionId;
        this.applicationTime = applicationTime;
    }

    public TrainingApplication(String teamName, Long sessionId ) {
        this.teamName = teamName;
        this.sessionId = sessionId;
        this.applicationTime = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getTeamName() {
        return teamName;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public LocalDateTime getApplicationTime() {
        return applicationTime;
    }

    public void setApplicationTime(LocalDateTime applicationTime) {
        this.applicationTime = applicationTime;
    }

    public int getNumberOfMissedRuns() {
        return numberOfMissedRuns;
    }

    public void setNumberOfMissedRuns(int numberOfMissedRuns) {
        this.numberOfMissedRuns = numberOfMissedRuns;
    }

    public int getNumberOfPerformedRuns() {
        return numberOfPerformedRuns;
    }

    public void setNumberOfPerformedRuns(int numberOfPerformedRuns) {
        this.numberOfPerformedRuns = numberOfPerformedRuns;
    }

    /**
     * register a performed run and "invalidate" the application
     */
    public void incrementPerformedRunsAndExpire() {
        numberOfPerformedRuns ++;
        applicationTime = null;
    }

    /**
     * register a missed run and "invalidate" the application
     */
    public void incrementMissedRunsAndExpire() {
        numberOfMissedRuns ++;
        applicationTime = null;
    }

    /**
     * set new application time if it is null, i.e. if this application is expired
     */
    public void refreshIfExpired() {
        if (applicationTime == null ) {
            applicationTime = LocalDateTime.now();
        }
    }

    public GuaranteedPosition getGuaranteedPosition() {
        return guaranteedPosition;
    }

    public void setGuaranteedPosition(GuaranteedPosition guaranteedPosition) {
        this.guaranteedPosition = guaranteedPosition;
    }
}
