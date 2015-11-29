package com.zuehlke.carrera.comp.nolog;

import com.zuehlke.carrera.comp.domain.CompetitionState;
import com.zuehlke.carrera.comp.domain.FuriousRun;
import com.zuehlke.carrera.comp.domain.RecentRunInfo;
import com.zuehlke.carrera.comp.domain.RoundResult;
import com.zuehlke.carrera.comp.repository.FuriousRunRepository;
import com.zuehlke.carrera.comp.repository.SpecialRepo;
import com.zuehlke.carrera.comp.service.CompetitionStateAssembler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * publishes the recent state of the competition to WS/STOMP subscribers
 */
@Component
public class StompCompetitionStatePublisher implements CompetitionStatePublisher {

    @Autowired
    private CompetitionStateAssembler assembler;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Override
    public void publish(String competition, Long sessionId, String team ) {

        CompetitionState state = assembler.assembleStateInfo(competition, sessionId, team);
        if ( state.getRecentRunInfo() == null ) {
            System.out.println("Gotcha!");
        }
        messagingTemplate.convertAndSend("/topic/status", state);
    }

}
