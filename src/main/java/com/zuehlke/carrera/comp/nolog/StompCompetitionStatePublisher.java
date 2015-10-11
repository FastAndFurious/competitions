package com.zuehlke.carrera.comp.nolog;

import com.zuehlke.carrera.comp.domain.CompetitionState;
import com.zuehlke.carrera.comp.domain.FuriousRun;
import com.zuehlke.carrera.comp.domain.RecentRunInfo;
import com.zuehlke.carrera.comp.domain.RoundResult;
import com.zuehlke.carrera.comp.repository.FuriousRunRepository;
import com.zuehlke.carrera.comp.repository.SpecialRepo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * publishes the recent state of the competition to WS/STOMP subscribers
 */
@Component
public class StompCompetitionStatePublisher implements CompetitionStatePublisher {

    @Inject
    private SimpMessageSendingOperations messagingTemplate;

    @Inject
    private SpecialRepo specialRepo;

    @Inject
    private FuriousRunRepository runRepository;

    @Override
    public void publish(String competition, Long sessionId, String team ) {

        CompetitionState state = new CompetitionState(competition);

        state.setCurrentBoard ( specialRepo.findBestRoundTimes( competition, sessionId ));

        if ( team != null ) {
            RecentRunInfo recentRunInfo = createRunInfoIfOngoing(competition, sessionId, team);
            state.setRecentRunInfo(recentRunInfo);

            if (recentRunInfo != null) {
                recentRunInfo.setTeam(team);
                specialRepo.findBestRoundTimes(competition, sessionId).stream().filter((rt) -> rt.getTeam().equals(team)).forEach(
                        recentRunInfo::setBestOfThisTeam
                );
            }
        }
        messagingTemplate.convertAndSend("/topic/status", state);

    }

    private RecentRunInfo createRunInfoIfOngoing(String competition, Long sessionId, String team) {
        FuriousRun run = runRepository.findOneBySessionIdAndTeam(sessionId, team);

        if ( run != null ) {
            if ( run.getStatus() == FuriousRun.Status.ONGOING) {
                RecentRunInfo recentRunInfo = new RecentRunInfo();
                recentRunInfo.setCurrentRunResults(specialRepo.findLatestRoundTimes(competition, sessionId, team, 5));
                return recentRunInfo;
            }
            return null;
        }
        return null;
    }
}
