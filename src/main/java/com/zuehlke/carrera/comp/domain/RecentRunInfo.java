package com.zuehlke.carrera.comp.domain;

import java.util.List;

/**
 *  encapsulates information about the recent run
 */
public class RecentRunInfo {

    private String team;
    private List<RoundResult> currentRunResults;
    private RoundResult bestOfThisTeam;
    private RoundResult nextBest;


    public List<RoundResult> getCurrentRunResults() {
        return currentRunResults;
    }

    public void setCurrentRunResults(List<RoundResult> currentRunResults) {
        this.currentRunResults = currentRunResults;
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public RoundResult getBestOfThisTeam() {
        return bestOfThisTeam;
    }

    public void setBestOfThisTeam(RoundResult bestOfThisTeam) {
        this.bestOfThisTeam = bestOfThisTeam;
    }

    public void setNextBest(RoundResult nextBest) {
        this.nextBest = nextBest;
    }

    public RoundResult getNextBest() {
        return nextBest;
    }
}
