package com.zuehlke.carrera.comp.service;

import com.zuehlke.carrera.comp.domain.*;
import com.zuehlke.carrera.comp.nolog.CompetitionStatePublisher;
import com.zuehlke.carrera.comp.repository.FuriousRunRepository;
import com.zuehlke.carrera.comp.repository.RacingSessionRepository;
import com.zuehlke.carrera.comp.repository.SpecialRepo;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.List;

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

        List< RoundResult> currentBoard = specialRepo.findBestRoundTimes( competition, sessionId );

        state.setCurrentBoard ( currentBoard );

        if ( currentBoard.size() > 0 ) {
            state.setBestOfSession(currentBoard.get(0));
        }

        state.setCurrentSession( sessionName );



        if ( team != null ) {
            RecentRunInfo recentRunInfo = createRunInfoIfOngoing(competition, sessionId, team);
            state.setRecentRunInfo(recentRunInfo);

            if (recentRunInfo != null) {
                List<RoundResult> allResults = specialRepo.findBestRoundTimes(competition, sessionId);

                recentRunInfo.setTeam(team);

                allResults.stream().filter((rt) -> rt.getTeam().equals(team)).forEach(
                        recentRunInfo::setBestOfThisTeam
                );

                RoundResult bestOfThisTeam = recentRunInfo.getBestOfThisTeam();
                if ( bestOfThisTeam != null ) {
                    int currentTeamsPosition = bestOfThisTeam.getPosition();

                    if (currentTeamsPosition != 1) {
                        RoundResult nextBest = allResults.stream().filter(
                                r -> r.getPosition() == currentTeamsPosition - 1)
                                .findAny().get();
                        recentRunInfo.setNextBest ( nextBest );
                    }
                }
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
