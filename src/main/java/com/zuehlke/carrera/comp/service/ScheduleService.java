package com.zuehlke.carrera.comp.service;

import com.zuehlke.carrera.comp.domain.Competition;
import com.zuehlke.carrera.comp.domain.FuriousRun;
import com.zuehlke.carrera.comp.domain.RacingSession;
import com.zuehlke.carrera.comp.domain.TeamRegistration;
import com.zuehlke.carrera.comp.repository.CompetitionRepository;
import com.zuehlke.carrera.comp.repository.FuriousRunRepository;
import com.zuehlke.carrera.comp.repository.RacingSessionRepository;
import com.zuehlke.carrera.comp.repository.TeamRegistrationRepository;
import com.zuehlke.carrera.relayapi.messages.RaceActivityMetadata;
import com.zuehlke.carrera.relayapi.messages.RunRequest;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.*;

/**
 *   manages scheduled runs
 */
@Component
public class ScheduleService {

    public static final Logger LOGGER = LoggerFactory.getLogger(ScheduleService.class);

    @Autowired
    private FuriousRunRepository runRepo;
    @Autowired
    private TeamRegistrationRepository teamRepo;
    @Autowired
    private CompetitionRepository compRepo;
    @Autowired
    private RacingSessionRepository sessionRepo;
    @Autowired
    private RelayApi relayApi;

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
        // Note the reverse ordering! First come - best place, which is the last one
        registrations.sort((l, r) -> r.getRegistrationTime().compareTo(l.getRegistrationTime()));

        List<FuriousRun> schedule = new ArrayList<>();

        LocalDateTime startTime = session.getPlannedStartTime();

        for ( TeamRegistration registration : registrations ) {
            FuriousRun run = new FuriousRun(registration.getTeam(), startTime, null, session.getId(),
                    competition.getId(), FuriousRun.Status.SCHEDULED);

            runRepo.save(run);
            schedule.add(run);
        }

        return schedule;

    }

    @Transactional
    public boolean startRun(Long id) {

        FuriousRun run = runRepo.findOne(id);
        RunRequest request = getRunRequest(id, run);

        boolean success = relayApi.startRun(request, LOGGER);
        if ( success ) {
            run.setStatus(FuriousRun.Status.ONGOING);
            runRepo.save(run);
        }

        return success;
    }


    public boolean stopRun(Long id) {

        FuriousRun run = runRepo.findOne(id);
        run.setStatus(FuriousRun.Status.QUALIFIED);
        runRepo.save(run);

        RunRequest request = getRunRequest(id, run);

        boolean success = relayApi.stopRun(request, LOGGER);

        return success;

    }

    private RunRequest getRunRequest(Long id, FuriousRun run) {
        RacingSession session = sessionRepo.findOne(run.getSessionId());
        Competition comp = compRepo.findByName(session.getCompetition());
        TeamRegistration registration = teamRepo.findByTeam(run.getTeam());

        String description = createDescription(run, session, comp);

        String protocol = registration.getProtocol();
        String encoding = registration.getEncoding();

        // no support in the application by now
        Set<String> tags = new HashSet<>();

        RaceActivityMetadata metadata = new RaceActivityMetadata(
                comp.getName(), tags, session.getType().toString(), description );

        return new RunRequest(run.getTeam(), registration.getAccessCode(), protocol, encoding, session.getTrackId(),
                metadata, id );
    }

    private String createDescription ( FuriousRun run, RacingSession session, Competition comp ) {
        StringBuffer buffer = new StringBuffer();
        LocalDateTime now = new LocalDateTime();
        String now_str = now.toString("yyyy-MMM-dd' - 'hh:mm:ss");
        buffer
                .append("Date: ").append(now_str)
                .append(", Comp: ").append(comp.getName())
                .append(", Session ").append(session.getType()).append(" Nr. ").append(session.getSeqNo())
                .append(", Team:  ").append(run.getTeam())
                .append(", Track: ").append(session.getTrackId())
                .append(", Layout: ").append(session.getTrackLayout());

        return buffer.toString();
    }

}
