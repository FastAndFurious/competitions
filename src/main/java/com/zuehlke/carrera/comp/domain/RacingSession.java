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

    @Column(name = "type")
    private String type;

    @Column(name = "seq_no")
    private Integer seqNo;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    @Column(name = "planned_start_time", nullable = false)
    private DateTime plannedStartTime;

    @NotNull
    @Column(name = "track_layout", nullable = false)
    private String trackLayout;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(Integer seqNo) {
        this.seqNo = seqNo;
    }

    public DateTime getPlannedStartTime() {
        return plannedStartTime;
    }

    public void setPlannedStartTime(DateTime plannedStartTime) {
        this.plannedStartTime = plannedStartTime;
    }

    public String getTrackLayout() {
        return trackLayout;
    }

    public void setTrackLayout(String trackLayout) {
        this.trackLayout = trackLayout;
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

        if ( ! Objects.equals(id, racingSession.id)) return false;

        return true;
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
}
