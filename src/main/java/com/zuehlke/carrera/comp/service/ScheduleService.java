package com.zuehlke.carrera.comp.service;

import com.zuehlke.carrera.comp.domain.*;
import com.zuehlke.carrera.comp.nolog.CompetitionStatePublisher;
import com.zuehlke.carrera.comp.repository.*;
import com.zuehlke.carrera.comp.web.outbound.PilotInfoResource;
import com.zuehlke.carrera.comp.web.rest.ServiceResult;
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
 * manages scheduled runs
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
    @Autowired
    private PilotInfoResource pilotInfoResource;
    @Autowired
    private CompetitionStatePublisher publisher;
    @Autowired
    private SpecialRepo specialRepo;

    public List<FuriousRunDto> findOrCreateForSession(Long sessionId) {

        List<FuriousRun> runs = runRepo.findBySessionId(sessionId);

        if (runs.size() == 0) {
            runs = createScheduleForSession(sessionId);
        }

        return enrichWithLifeSignInfo(runs);

    }

    /**
     * enriches the FuriousRun entities with recent info about the pilot lifesigns
     *
     * @param runs the original list of runs
     * @return a list of dtos with additional status info
     */
    private List<FuriousRunDto> enrichWithLifeSignInfo(List<FuriousRun> runs) {

        PilotInfo pilotInfo = pilotInfoResource.retrieveInfo();

        List<FuriousRunDto> dtos = new ArrayList<>();

        runs.stream().forEach((FuriousRun run) -> dtos.add(new FuriousRunDto(run)));

        dtos.forEach(
                (run) -> pilotInfo.getPilotLifeSigns().stream().filter(
                        (l) -> l.getTeamId().equals(run.getTeam())
                ).forEach((l) -> {
                            run.setPilotLifeSign(l);
                            run.setPilotState(determineState(l.getTimestamp()));
                        }
                ));

        return dtos;
    }

    /**
     * The timestamp comparison is done here to fix the decision independent of the client clock.
     *
     * @param timestamp the timestamp of the most recent life sign
     * @return the status as an interpretation of the timestamp as a valid life sign.
     */
    private FuriousRunDto.PilotState determineState(long timestamp) {

        long ten_seconds = 10000;
        long five_minutes = 300000;

        long now = System.currentTimeMillis();
        if (now - timestamp < ten_seconds) {
            return FuriousRunDto.PilotState.CURRENT_LIFESIGNS;
        } else if (now - timestamp < five_minutes) {
            return FuriousRunDto.PilotState.FORMER_LIFESIGNS;
        } else {
            return FuriousRunDto.PilotState.NO_LIFESIGNS;
        }
    }


    private List<FuriousRun> createScheduleForSession(Long sessionId) {

        RacingSession session = sessionRepo.findOne(sessionId);

        Competition comp = compRepo.findByName(session.getCompetition());

        List<TeamRegistration> registrations = teamRepo.findByCompetition(comp.getName());

        switch ( session.getType()) {
            case Training:
                return createScheduleForTraining(registrations, comp, session);
            case Qualifying:
                return createScheduleForQualifying ( comp, session );
            case Competition:
                return createScheduleForCompetition ( comp, session );
            default:
                throw new IllegalStateException("Can't create schedule for " + session.getType() + ". Not implemented.");
        }
    }

    /**
     * schedule for qualifying: the best training run entitles for the best position: the last.
     * @param session the qualifying session to schedule for
     * @return
     */
    private List<FuriousRun> createScheduleForQualifying(Competition comp, RacingSession session) {

        RacingSession trainingSession = findSession( session.getCompetition(), RacingSession.SessionType.Training);

        List<RoundResult> trainingResults = specialRepo.findBestRoundTimes(session.getCompetition(), trainingSession.getId());

        if ( trainingResults.size() == 0 ) {
            return new ArrayList<>();
        }

        List<FuriousRun> schedule = new ArrayList<>();

        LocalDateTime startTime = session.getPlannedStartTime();

        int position = trainingResults.size();

        for (RoundResult result : trainingResults ) {
            FuriousRun run = new FuriousRun(result.getTeam(), startTime, null, session.getId(),
                    comp.getId(), FuriousRun.Status.SCHEDULED, position--);

            runRepo.save(run);
            schedule.add(run);
        }

        schedule.sort((r,l)->r.getStartPosition()-l.getStartPosition());
        return schedule;
    }

    private RacingSession findSession(String comp, RacingSession.SessionType type ) {
        for ( RacingSession s : sessionRepo.findByCompetition(comp)) {
            if (s.getType() == type) {
                return s;
            }
        }
        return null;
    }

    private List<FuriousRun> createScheduleForCompetition(Competition comp, RacingSession session) {
        return new ArrayList<>();
    }


    /**
     * @param registrations the list of registrations to be considered
     * @param competition   the actual competition
     * @param session       the particular session to create the schedule for
     * @return a list of runs as the schedule for execution. Here, the earliest registrations earns the best position,
     * which is the last one.
     */
    private List<FuriousRun> createScheduleForTraining(List<TeamRegistration> registrations,
                                                       Competition competition, RacingSession session) {
        // Note the reverse ordering! First come - best place, which is the last one
        registrations.sort((l, r) -> r.getRegistrationTime().compareTo(l.getRegistrationTime()));

        List<FuriousRun> schedule = new ArrayList<>();

        LocalDateTime startTime = session.getPlannedStartTime();

        int startPosition = 0;

        for (TeamRegistration registration : registrations) {
            FuriousRun run = new FuriousRun(registration.getTeam(), startTime, null, session.getId(),
                    competition.getId(), FuriousRun.Status.SCHEDULED, startPosition ++);

            runRepo.save(run);
            schedule.add(run);
        }

        return schedule;

    }

    @Transactional
    public ServiceResult startRun(Long id) {

        FuriousRun run = runRepo.findOne(id);
        RunRequest request = getRunRequest(id, run);
        Competition comp = compRepo.findOne(run.getCompetitionId());

        ServiceResult result = relayApi.startRun(request, LOGGER);
        if (result.getStatus() == ServiceResult.Status.OK) {
            run.setStatus(FuriousRun.Status.ONGOING);
            runRepo.save(run);
            publisher.publish(comp.getName(), run.getSessionId(), run.getTeam());
        }

        return result;
    }


    public ServiceResult stopRun(Long id) {

        FuriousRun run = runRepo.findOne(id);
        run.setStatus(FuriousRun.Status.QUALIFIED);
        runRepo.save(run);

        RacingSession session = sessionRepo.findOne(run.getSessionId());
        publisher.publish(session.getCompetition(), run.getSessionId(), null);

        RunRequest request = getRunRequest(id, run);
        return relayApi.stopRun(request, LOGGER);

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
                comp.getName(), tags, session.getType().toString(), description);

        return new RunRequest(run.getTeam(), registration.getAccessCode(), protocol, encoding, session.getTrackId(),
                metadata, id);
    }

    private String createDescription(FuriousRun run, RacingSession session, Competition comp) {
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
