package com.zuehlke.carrera.comp.nolog;

import com.zuehlke.carrera.comp.domain.CompetitionState;
import com.zuehlke.carrera.comp.domain.RoundTime;
import com.zuehlke.carrera.comp.repository.RoundTimeRepository;
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
    private RoundTimeRepository repository;

    public Long register(RoundTimeMessage message) {

        RoundTime roundTime = new RoundTime(
            message.getTimestamp(),
            message.getRoundDuration(),
            message.getTeam(),
            message.getTrack());

        repository.save(roundTime);

        messagingTemplate.convertAndSend("/topic/rounds", roundTime);

        return roundTime.getId();
    }

    public CompetitionState assembleState () {
        return new CompetitionState();
    }

    @Scheduled(fixedDelay = 300000)
    public void updateSubscribers () {
        messagingTemplate.convertAndSend("/topic/status", assembleState());
    }

    public List<RoundTime> findAll() {
        return repository.findAll();
    }

    public RoundTime findOne(Long id) {
        return repository.findOne(id);
    }

    public void delete(Long id) {
        repository.delete(id);
    }
}
