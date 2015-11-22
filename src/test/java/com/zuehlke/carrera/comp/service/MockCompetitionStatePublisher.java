package com.zuehlke.carrera.comp.service;


import com.zuehlke.carrera.comp.domain.CompetitionState;
import com.zuehlke.carrera.comp.nolog.CompetitionStatePublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class MockCompetitionStatePublisher implements CompetitionStatePublisher{

    @Autowired
    private CompetitionStateAssembler assembler;

    private CompetitionState state;

    @Override
    public void publish(String competition, Long sessionId, String team) {

        state = assembler.assembleStateInfo(competition, sessionId, team);
    }

    public CompetitionState getState () {
        return state;
    }
}
