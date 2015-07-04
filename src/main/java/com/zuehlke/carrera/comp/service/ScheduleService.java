package com.zuehlke.carrera.comp.service;

import com.zuehlke.carrera.comp.domain.Competition;
import com.zuehlke.carrera.comp.domain.FuriousRun;
import com.zuehlke.carrera.comp.domain.RacingSession;
import com.zuehlke.carrera.comp.domain.TeamRegistration;
import com.zuehlke.carrera.comp.repository.CompetitionRepository;
import com.zuehlke.carrera.comp.repository.FuriousRunRepository;
import com.zuehlke.carrera.comp.repository.RacingSessionRepository;
import com.zuehlke.carrera.comp.repository.TeamRegistrationRepository;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 *   manages scheduled runs
 */
@Component
public class ScheduleService {

    @Autowired
    private FuriousRunRepository runRepo;

    @Autowired
    private TeamRegistrationRepository teamRepo;

    @Autowired
    private CompetitionRepository compRepo;

    @Autowired
    private RacingSessionRepository sessionRepo;

    public List<FuriousRun> findOrCreateForSession ( Long sessionId ) {

        List<FuriousRun> runs = runRepo.findBySessionId ( sessionId );

        if ( runs.size() == 0 ) {
            runs = createScheduleForSession ( sessionId );
        }

        return runs;
    }

    private List<FuriousRun> createScheduleForSession(Long sessionId) {

        RacingSession session = sessionRepo.findOne(sessionId);

        Competition comp = compRepo.findByName(session.getCompetition());

        List<TeamRegistration> registrations = teamRepo.findByCompetition(comp.getName());

        if ( session.getType().equals(RacingSession.SessionType.Training)) {
            if ( session.getSeqNo() == 1 ) {
                return createScheduleForFirstTraining (registrations, comp, session);
            } else {
                return createScheduleForSubsequentTrainings(session, registrations);
            }
        } else {
            return new ArrayList<>();
        }

    }

    private List<FuriousRun> createScheduleForSubsequentTrainings(RacingSession session, List<TeamRegistration> registrations) {
        return null;
    }

    /**
     * @param registrations the list of registrations to be considered
     * @param competition the actual competition
     * @param session the particular session to create the schedule for
     * @return a list of runs as the schedule for execution. Here, the earliest registrations earns the best position,
     * which is the last one.
     */
    private List<FuriousRun> createScheduleForFirstTraining(List<TeamRegistration> registrations,
                                                            Competition competition, RacingSession session) {

        // Note the reverse ordering!
        registrations.sort((l, r) -> r.getRegistrationTime().compareTo(l.getRegistrationTime()));

        List<FuriousRun> schedule = new ArrayList<>();

        LocalDateTime startTime = session.getPlannedStartTime();

        for ( TeamRegistration registration : registrations ) {
            FuriousRun run = new FuriousRun(registration.getTeam(), startTime, null, session.getId(),
                    competition.getId(), FuriousRun.Status.SCHEDULED);
            schedule.add(run);
        }

        return schedule;

    }
}
