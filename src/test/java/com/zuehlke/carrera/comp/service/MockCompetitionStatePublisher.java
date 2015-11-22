package com.zuehlke.carrera.comp.service;


import com.zuehlke.carrera.comp.nolog.CompetitionStatePublisher;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class MockCompetitionStatePublisher implements CompetitionStatePublisher{


    @Override
    public void publish(String competition, Long sessionId, String team) {

    }
}
