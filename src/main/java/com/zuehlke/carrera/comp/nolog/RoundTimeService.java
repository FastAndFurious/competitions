package com.zuehlke.carrera.comp.nolog;

import com.zuehlke.carrera.comp.domain.FuriousRun;
import com.zuehlke.carrera.comp.domain.RoundTime;
import com.zuehlke.carrera.comp.repository.CompetitionRepository;
import com.zuehlke.carrera.comp.repository.RoundTimeRepository;
import com.zuehlke.carrera.comp.repository.SpecialRepo;
import com.zuehlke.carrera.relayapi.messages.RoundTimeMessage;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

/**
 */
@Component
public class RoundTimeService {

    @Inject
    private RoundTimeRepository roundRepository;

    @Inject
    private CompetitionRepository compRepo;

    @Inject
    private SpecialRepo specialRepo;

    @Inject
    private CompetitionStatePublisher publisher;

    /**
     * Register a round result with the comp manager
     * @param message the most recent round time
     * @return the id of the persisted round time
     */
    public Long register(RoundTimeMessage message) {

        RoundTime roundTime = new RoundTime(
            message.getTimestamp(),
            message.getRoundDuration(),
            message.getTeam(),
            message.getTrack());

        FuriousRun run = findOngoingRunOnTrack ( message.getTrack());

        roundTime.setRunId(run.getId());

        roundRepository.save(roundTime);

        String competition = compRepo.findOne(run.getCompetitionId()).getName();

        publisher.publishStatus( competition, run.getSessionId(), run.getTeam());

        return roundTime.getId();
    }

    private FuriousRun findOngoingRunOnTrack(String track) {

        return specialRepo.findOngoingRunOnTrack(track);
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
