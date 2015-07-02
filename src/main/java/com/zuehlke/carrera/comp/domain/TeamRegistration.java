package com.zuehlke.carrera.comp.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zuehlke.carrera.comp.domain.util.CustomDateTimeDeserializer;
import com.zuehlke.carrera.comp.domain.util.CustomDateTimeSerializer;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A TeamRegistration.
 */
@Entity
@Table(name = "T_TEAMREGISTRATION")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TeamRegistration implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull
    @Column(name = "competition", nullable = false)
    private String competition;

    @NotNull
    @Column(name = "team", nullable = false)
    private String team;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "registration_time", nullable = false)
    private DateTime registrationTime;

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

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public DateTime getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(DateTime registrationTime) {
        this.registrationTime = registrationTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TeamRegistration teamRegistration = (TeamRegistration) o;

        if ( ! Objects.equals(id, teamRegistration.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "TeamRegistration{" +
                "id=" + id +
                ", competition='" + competition + "'" +
                ", team='" + team + "'" +
                ", registrationTime='" + registrationTime + "'" +
                '}';
    }
}
