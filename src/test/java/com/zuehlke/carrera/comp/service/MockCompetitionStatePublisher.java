package com.zuehlke.carrera.comp.service;


import com.zuehlke.carrera.comp.domain.CompetitionState;
import com.zuehlke.carrera.comp.domain.ScheduledRun;
import com.zuehlke.carrera.comp.nolog.CompetitionStatePublisher;
import com.zuehlke.carrera.comp.web.rest.dto.PublishableSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Primary
public class MockCompetitionStatePublisher implements CompetitionStatePublisher{

    @Autowired
    private CompetitionStateAssembler assembler;

    @Autowired
    private FreeTrainingService trainingService;

    private CompetitionState state;

    private PublishableSchedule schedule;

    @Override
    public void publishStatus(String competition, Long sessionId, String team) {

        state = assembler.assembleStateInfo(competition, sessionId, team);
    }

    @Override
    public void publishSchedule(Long sessionId) {
        List<ScheduledRun> runs = trainingService.getSchedule( sessionId);

        schedule = new PublishableSchedule( runs, "TestSession" );
    }

    public CompetitionState getState () {
        return state;
    }

    public PublishableSchedule getSchedule() {
        return schedule;
    }
}
