package com.zuehlke.carrera.comp.nolog;

import com.zuehlke.carrera.comp.domain.CompetitionState;
import com.zuehlke.carrera.comp.domain.FuriousRun;
import com.zuehlke.carrera.comp.domain.RoundTime;
import com.zuehlke.carrera.comp.repository.FuriousRunRepository;
import com.zuehlke.carrera.comp.repository.RoundTimeRepository;
import com.zuehlke.carrera.comp.repository.SpecialRepo;
import com.zuehlke.carrera.relayapi.messages.RoundTimeMessage;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

/**
 */
@Component
public class RoundTimeService {

    @Inject
    private SimpMessageSendingOperations messagingTemplate;

    @Inject
    private RoundTimeRepository roundRepository;

    @Inject
    private SpecialRepo specialRepo;

    public Long register(RoundTimeMessage message) {

        RoundTime roundTime = new RoundTime(
            message.getTimestamp(),
            message.getRoundDuration(),
            message.getTeam(),
            message.getTrack());

        FuriousRun run = findOngoingRunOnTrack ( message.getTrack());

        roundTime.setRunId( run.getId());

        roundRepository.save(roundTime);

        messagingTemplate.convertAndSend("/topic/rounds", roundTime);

        return roundTime.getId();
    }

    private FuriousRun findOngoingRunOnTrack(String track) {

        return specialRepo.findOngoingRunOnTrack(track);
    }

    public CompetitionState assembleState () {
        return new CompetitionState();
    }

    @Scheduled(fixedDelay = 300000)
    public void updateSubscribers () {
        messagingTemplate.convertAndSend("/topic/status", assembleState());
    }

    public List<RoundTime> findAll() {
        return roundRepository.findAll();
    }

    public RoundTime findOne(Long id) {
        return roundRepository.findOne(id);
    }

    public void delete(Long id) {
        roundRepository.delete(id);
    }
}
