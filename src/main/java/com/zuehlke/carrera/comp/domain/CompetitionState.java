package com.zuehlke.carrera.comp.domain;

/**
 *  represents all of the current state of a competition
 */
public class CompetitionState {

    private String name;
    private final long snapshotTime = System.currentTimeMillis();

    private long scheduledStartTime;
    private Phase phase = Phase.SCHEDULED;

    public CompetitionState() {
    }

    public CompetitionState ( String name ) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSnapshotTime() {
        return snapshotTime;
    }

    public long getScheduledStartTime() {
        return scheduledStartTime;
    }

    public void setScheduledStartTime(long scheduledStartTime) {
        this.scheduledStartTime = scheduledStartTime;
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }
}
