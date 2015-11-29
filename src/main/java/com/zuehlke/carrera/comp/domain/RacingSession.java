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
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A RacingSession.
 */
@Entity
@Table(name = "T_RACINGSESSION")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class RacingSession implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "competition", nullable = false)
    private String competition;

    @Enumerated(EnumType.STRING)
    private SessionType type;

    @Column(name = "seq_no")
    private Integer seqNo;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "planned_start_time", nullable = false)
    private LocalDateTime plannedStartTime;

    @NotNull
    @Column(name = "track_layout", nullable = false)
    private String trackLayout;

    @NotNull
    @Column(name = "track_id", nullable = false)
    private String trackId;

    @Column(name="run_duration", nullable = true)
    private Integer runDuration;

    public RacingSession(){} // for JPA

    public RacingSession(Long id, String competition, SessionType type, Integer seqNo, LocalDateTime plannedStartTime,
                         String trackId, String trackLayout, int runDuration ) {
        this.id = id;
        this.competition = competition;
        this.type = type;
        this.seqNo = seqNo;
        this.plannedStartTime = plannedStartTime;
        this.trackId = trackId;
        this.trackLayout = trackLayout;
        this.runDuration = runDuration;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompetition() {
        return competition;
    }

    public void setCompetition(String competition) {
        this.competition = competition;
    }

    public SessionType getType() {
        return type;
    }

    public void setType(SessionType type) {
        this.type = type;
    }

    public Integer getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(Integer seqNo) {
        this.seqNo = seqNo;
    }

    public LocalDateTime getPlannedStartTime() {
        return plannedStartTime;
    }

    public void setPlannedStartTime(LocalDateTime plannedStartTime) {
        this.plannedStartTime = plannedStartTime;
    }

    public String getTrackLayout() {
        return trackLayout;
    }

    public void setTrackLayout(String trackLayout) {
        this.trackLayout = trackLayout;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public Integer getRunDuration() {
        return runDuration;
    }

    public void setRunDuration(Integer runDuration) {
        this.runDuration = runDuration;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RacingSession racingSession = (RacingSession) o;

        return Objects.equals(id, racingSession.id);

    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "RacingSession{" +
                "id=" + id +
                ", competition='" + competition + "'" +
                ", type='" + type + "'" +
                ", seqNo='" + seqNo + "'" +
                ", plannedStartTime='" + plannedStartTime + "'" +
                ", trackLayout='" + trackLayout + "'" +
                '}';
    }

    public enum SessionType {
        Training,
        Qualifying,
        Competition
    }
}
