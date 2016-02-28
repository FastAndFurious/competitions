package com.zuehlke.carrera.comp.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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

    @NotNull
    @Column(name = "access_code", nullable = false)
    private String accessCode;

    @Column(name="protocol")
    private String protocol;

    @Column(name="encoding")
    private String encoding;

    @Column(name="twitter_names")
    private String twitterNames;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "registration_time", nullable = false)
    private LocalDateTime registrationTime;

    public TeamRegistration(){} // for JPA

    public TeamRegistration(Long id, String competition, String team, String accessCode,
                            String protocol, String encoding, LocalDateTime registrationTime) {
        this.id = id;
        this.competition = competition;
        this.team = team;
        this.accessCode = accessCode;
        this.protocol = protocol;
        this.encoding = encoding;
        this.registrationTime = registrationTime;
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

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getAccessCode() {
        return accessCode;
    }

    public void setAccessCode(String accessCode) {
        this.accessCode = accessCode;
    }

    public LocalDateTime getRegistrationTime() {
        return registrationTime;
    }

    public void setRegistrationTime(LocalDateTime registrationTime) {
        this.registrationTime = registrationTime;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public void setTwitterNames ( String twitterNames ) {
        this.twitterNames = twitterNames;
    }

    public String getTwitterNames () {
        return twitterNames;
    }

    @JsonIgnore
    public List<String> getListOfTwitterNames() {
        if ( twitterNames == null ) {
            return Collections.emptyList();
        }
        return Arrays.asList(twitterNames.split(","));
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
