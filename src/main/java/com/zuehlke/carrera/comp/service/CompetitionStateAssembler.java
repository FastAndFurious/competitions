package com.zuehlke.carrera.comp.service;

import com.zuehlke.carrera.comp.domain.CompetitionState;
import com.zuehlke.carrera.comp.domain.FuriousRun;
import com.zuehlke.carrera.comp.domain.RacingSession;
import com.zuehlke.carrera.comp.domain.RecentRunInfo;
import com.zuehlke.carrera.comp.nolog.CompetitionStatePublisher;
import com.zuehlke.carrera.comp.repository.FuriousRunRepository;
import com.zuehlke.carrera.comp.repository.RacingSessionRepository;
import com.zuehlke.carrera.comp.repository.SpecialRepo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * publishes the recent state of the competition to WS/STOMP subscribers
 */
@Component
public class CompetitionStateAssembler {

    @Inject
    private SpecialRepo specialRepo;

    @Inject
    private FuriousRunRepository runRepository;

    @Inject
    private RacingSessionRepository sessionRepository;

    public CompetitionState assembleStateInfo (String competition, Long sessionId, String team ) {

        RacingSession session = sessionRepository.findOne(sessionId);

        String sessionName = session.getType() + " No " + session.getSeqNo();

        CompetitionState state = new CompetitionState(competition);

        state.setCurrentBoard ( specialRepo.findBestRoundTimes( competition, sessionId ));

        state.setCurrentSession( sessionName );

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
        return state;
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
