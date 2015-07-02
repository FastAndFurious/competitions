package com.zuehlke.carrera.comp.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zuehlke.carrera.comp.domain.util.CustomLocalDateSerializer;
import com.zuehlke.carrera.comp.domain.util.ISO8601LocalDateDeserializer;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Round Time Record to be persisted in the database
 */
@Entity
@Table(name = "ROUND_TIMES")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class RoundTime implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "timestamp", nullable = false)
    private long timestamp;

    @NotNull
    @Column(name = "duration", nullable = false)
    private long duration;

    @NotNull
    @Column(name = "team", nullable = false)
    private String team;

    @NotNull
    @Column(name = "track", nullable = false)
    private String track;

    public RoundTime(long timestamp, long duration, String team, String track) {
        this.timestamp = timestamp;
        this.duration = duration;
        this.team = team;
        this.track = track;
    }

    public RoundTime() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getTrack() {
        return track;
    }

    public void setTrack(String track) {
        this.track = track;
    }
}
