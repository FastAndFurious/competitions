package com.zuehlke.carrera.comp.nolog;

import com.zuehlke.carrera.comp.domain.*;
import com.zuehlke.carrera.comp.repository.FuriousRunRepository;
import com.zuehlke.carrera.comp.repository.RacingSessionRepository;
import com.zuehlke.carrera.comp.repository.SpecialRepo;
import com.zuehlke.carrera.comp.repository.TrainingApplicationRepository;
import com.zuehlke.carrera.comp.service.CompetitionStateAssembler;
import com.zuehlke.carrera.comp.service.FreeTrainingService;
import com.zuehlke.carrera.comp.web.rest.dto.PublishableSchedule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

/**
 * publishes the recent state of the competition to WS/STOMP subscribers
 */
@Component
public class StompCompetitionStatePublisher implements CompetitionStatePublisher {

    static final Logger logger = LoggerFactory.getLogger(StompCompetitionStatePublisher.class);

    @Autowired
    private CompetitionStateAssembler assembler;

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @Autowired
    private FreeTrainingService trainingService;

    @Autowired
    private RacingSessionRepository sessionRepository;

    @Override
    public void publishStatus(String competition, Long sessionId, String team ) {

        CompetitionState state = assembler.assembleStateInfo(competition, sessionId, team);
        if ( state.getRecentRunInfo() == null ) {
            logger.info ( "Recent run info missing. Race is over.");
        }
        messagingTemplate.convertAndSend("/topic/status", state);
    }

    @Override
    public void publishSchedule(Long sessionId ) {

        String sessionName = sessionRepository.getOne(sessionId).shortName();

        List<ScheduledRun> runs = trainingService.getSchedule( sessionId);

        PublishableSchedule schedule = new PublishableSchedule( runs, sessionName );

        messagingTemplate.convertAndSend("/topic/schedule", schedule);
    }

}
