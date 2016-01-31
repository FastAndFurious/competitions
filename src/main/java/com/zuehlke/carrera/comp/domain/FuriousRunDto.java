package com.zuehlke.carrera.comp.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zuehlke.carrera.comp.domain.util.CustomDateTimeDeserializer;
import com.zuehlke.carrera.comp.domain.util.CustomDateTimeSerializer;
import com.zuehlke.carrera.relayapi.messages.PilotLifeSign;
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
public class FuriousRunDto extends FuriousRun {

    private PilotLifeSign pilotLifeSign;

    private PilotState pilotState;

    public FuriousRunDto() {} // for JPA

    public FuriousRunDto ( FuriousRun run ) {
        this.id = run.id;
        this.team = run.team;
        this.scheduledStartDate = run.scheduledStartDate;
        this.startDate = run.startDate;
        this.startPosition = run.startPosition;
        this.sessionId = run.sessionId;
        this.competitionId = run.competitionId;
        this.status = run.status;
        this.pilotState = PilotState.NO_LIFESIGNS;
    }

    public PilotLifeSign getPilotLifeSign() {
        return pilotLifeSign;
    }

    public void setPilotLifeSign(PilotLifeSign pilotLifeSign) {
        this.pilotLifeSign = pilotLifeSign;
    }

    public PilotState getPilotState() {
        return pilotState;
    }

    public void setPilotState(PilotState pilotState) {
        this.pilotState = pilotState;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FuriousRunDto competition = (FuriousRunDto) o;

        return Objects.equals(id, competition.id);

    }


    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "FuriousRun{" +
                "id=" + id +
                ", team='" + team + "'" +
                ", startDate='" + startDate + "'" +
                '}';
    }

    public static enum PilotState {
        CURRENT_LIFESIGNS,
        FORMER_LIFESIGNS,
        NO_LIFESIGNS,
        UNKNOWN
    }
}
