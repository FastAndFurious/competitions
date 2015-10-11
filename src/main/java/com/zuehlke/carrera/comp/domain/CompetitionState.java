package com.zuehlke.carrera.comp.domain;

import java.util.List;

/**
 *  represents all of the current state of a competition
 */
public class CompetitionState {

    private String name;
    private final long snapshotTime = System.currentTimeMillis();
    private List<RoundResult> currentBoard;
    private RecentRunInfo recentRunInfo;

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

    public List<RoundResult> getCurrentBoard() {
        return currentBoard;
    }

    public void setCurrentBoard(List<RoundResult> currentBoard) {
        this.currentBoard = currentBoard;
    }

    public RecentRunInfo getRecentRunInfo() {
        return recentRunInfo;
    }

    public void setRecentRunInfo(RecentRunInfo recentRunInfo) {
        this.recentRunInfo = recentRunInfo;
    }
}
