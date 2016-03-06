package com.zuehlke.carrera.comp.web.rest.dto;

import com.zuehlke.carrera.comp.domain.GuaranteedPosition;
import com.zuehlke.carrera.comp.domain.ScheduledRun;
import com.zuehlke.carrera.relayapi.messages.PilotLifeSign;

public class ScheduledRunDto {

    private int position;
    private GuaranteedPosition guaranteed;
    private String team;
    private String applied;
    private String schedule;
    private int performed;
    private int missed;
    private PilotLifeSign lifeSign;

    public ScheduledRunDto(ScheduledRun run) {

        this.position = run.getPosition();
        this.team = run.getTeamId();
        this.guaranteed = run.getGuaranteedPosition();
        formatApplicationTime(run);

        this.schedule = run.getScheduledStart().toString("EEE HH:mm");
        this.performed = run.getNumberOfPerformedRuns();
        this.missed = run.getNumberOfMissedRuns();
        this.lifeSign = run.getPilotLifeSign();
    }

    private void formatApplicationTime(ScheduledRun run) {
        this.applied = ( run.getApplication() == null ) ? "":
                (run.getApplication().getApplicationTime() == null ) ? "" :
                run.getApplication().getApplicationTime().toString("EEE HH:mm:ss");
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public GuaranteedPosition getGuaranteed() {
        return guaranteed;
    }

    public void setGuaranteed(GuaranteedPosition guaranteed) {
        this.guaranteed = guaranteed;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getApplied() {
        return applied;
    }

    public void setApplied(String applied) {
        this.applied = applied;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public int getPerformed() {
        return performed;
    }

    public void setPerformed(int performed) {
        this.performed = performed;
    }

    public int getMissed() {
        return missed;
    }

    public void setMissed(int missed) {
        this.missed = missed;
    }

    public PilotLifeSign getLifeSign() {
        return lifeSign;
    }

    public void setLifeSign(PilotLifeSign lifeSign) {
        this.lifeSign = lifeSign;
    }
}
