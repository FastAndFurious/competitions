package com.zuehlke.carrera.comp.web.rest;

public class BatchRegistration {

    private  int numberOfRegistrations;
    private  String competition;

    public BatchRegistration () {}

    public BatchRegistration(int numberOfRegistrations, String competition) {
        this.numberOfRegistrations = numberOfRegistrations;
        this.competition = competition;
    }

    public int getNumberOfRegistrations() {
        return numberOfRegistrations;
    }

    public String getCompetition() {
        return competition;
    }

    public void setNumberOfRegistrations(int numberOfRegistrations) {
        this.numberOfRegistrations = numberOfRegistrations;
    }

    public void setCompetition(String competition) {
        this.competition = competition;
    }
}
