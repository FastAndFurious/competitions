package com.zuehlke.carrera.comp.web.rest.dto;

import com.zuehlke.carrera.comp.domain.ScheduledRun;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PublishableSchedule {

    private String currentSession;

    private List<ScheduledRunDto> currentPositions = new ArrayList<>();

    public PublishableSchedule(List<ScheduledRun> runs, String currentSession ) {
        currentPositions.addAll(runs.stream().map(ScheduledRunDto::new).collect(Collectors.toList()));
        this.currentSession = currentSession;
    }

    public List<ScheduledRunDto> getCurrentPositions() {
        return currentPositions;
    }

    public void setCurrentPositions(List<ScheduledRunDto> currentPositions) {
        this.currentPositions = currentPositions;
    }

    public String getCurrentSession() {
        return currentSession;
    }
}
