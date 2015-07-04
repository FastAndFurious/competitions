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
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A Competition.
 */
@Entity
@Table(name = "COMPETITION")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Competition implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "track_id", nullable = false)
    private String trackId;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "best_sequence")
    private Integer bestSequence;

    @Column(name = "best_set")
    private Integer bestSet;

    @Column(name = "first_priority")
    private String firstPriority;

    @Column(name = "second_priority")
    private String secondPriority;

    public Competition() {} // for JPA

    public Competition(Long id, String name, String trackId, LocalDate startDate) {
        this.id = id;
        this.name = name;
        this.trackId = trackId;
        this.startDate = startDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Integer getBestSequence() {
        return bestSequence;
    }

    public void setBestSequence(Integer bestSequence) {
        this.bestSequence = bestSequence;
    }

    public Integer getBestSet() {
        return bestSet;
    }

    public void setBestSet(Integer bestSet) {
        this.bestSet = bestSet;
    }

    public String getFirstPriority() {
        return firstPriority;
    }

    public void setFirstPriority(String firstPriority) {
        this.firstPriority = firstPriority;
    }

    public String getSecondPriority() {
        return secondPriority;
    }

    public void setSecondPriority(String secondPriority) {
        this.secondPriority = secondPriority;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Competition competition = (Competition) o;

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
                ", name='" + name + "'" +
                ", trackId='" + trackId + "'" +
                ", startDate='" + startDate + "'" +
                ", bestSequence='" + bestSequence + "'" +
                ", bestSet='" + bestSet + "'" +
                ", firstPriority='" + firstPriority + "'" +
                ", secondPriority='" + secondPriority + "'" +
                '}';
    }
}
