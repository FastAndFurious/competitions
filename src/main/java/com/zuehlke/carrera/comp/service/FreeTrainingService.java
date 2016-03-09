package com.zuehlke.carrera.comp.service;

import com.zuehlke.carrera.comp.domain.*;
import com.zuehlke.carrera.comp.nolog.CompetitionStatePublisher;
import com.zuehlke.carrera.comp.repository.*;
import com.zuehlke.carrera.comp.web.outbound.OutboundServiceException;
import com.zuehlke.carrera.comp.web.outbound.PilotInfoResource;
import org.joda.time.LocalDateTime;
import org.joda.time.Period;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * handles free training sessions
 */

@Service
public class FreeTrainingService {

    private static final Logger logger = LoggerFactory.getLogger(FreeTrainingService.class);

    @Autowired
    private CompetitionRepository compRepo;

    @Autowired
    private TeamRegistrationRepository teamRepo;

    @Autowired
    private FuriousRunRepository furiousRunRepository;

    @Autowired
    private RacingSessionRepository sessionRepo;

    @Autowired
    private TrainingApplicationRepository trainingApplicationRepository;

    @Autowired
    private PilotInfoResource pilotInfoResource;

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private SocialBroadcaster socialBroadcaster;

    @Autowired
    private CompetitionStatePublisher publisher;

    /**
     * Create a list of scheduled run <p/>
     * A complete schedule has a single entry for each team that's registered for the training session.
     * The schedule represents the starting order for the next runs.
     * A scheduled run will have an application with it, if a non-expired one exists.
     * An application with null applicationTime is considered expired and will be ignored.
     *
     * @param sessionId the training's session id to get the schedule for
     * @return a list of runs, one for each team, scheduled according to the rules
     */
    @Transactional
    public List<ScheduledRun> getSchedule(Long sessionId) {

        // Make sure furious run instances are created
        scheduleService.findOrCreateForSession(sessionId);

        List<ScheduledRun> schedule = new ArrayList<>();

        RacingSession session = sessionRepo.findOne(sessionId);

        List<TeamRegistration> registrations = teamRepo.findByCompetition(session.getCompetition());

        schedule.addAll(
                registrations.stream().map(
                        registration -> new ScheduledRun(
                                registration.getTeam(),
                                sessionId
                        )).collect(Collectors.toList()));


        schedule.stream().forEach(run -> {
                    FuriousRun furiousRun = furiousRunRepository.findOneBySessionIdAndTeam(run.getSessionId(), run.getTeamId());
                    if (furiousRun != null) {
                        run.setFuriousId(furiousRun.getId());
                        run.setStatus ( furiousRun.getStatus());
                    }
                    TrainingApplication application = assureApplication(run.getTeamId(), sessionId);
                    run.setApplication(application);
                }
        );

        schedule.sort(null);
        int position = 1;
        for (ScheduledRun run : schedule) {
            // Once in first or second positions, late applicants won't sneek in front of you anymore.
            if (position == 1) {
                run.setGuaranteedPosition(GuaranteedPosition.FIRST);
            } else if (position == 2) {
                run.setGuaranteedPosition(GuaranteedPosition.SECOND);
            } else {
                run.setGuaranteedPosition(GuaranteedPosition.NONE);
            }

            run.setScheduledStart(calculateStartTime(session, position));

            run.setPosition(position++);

        }

        tryEnrichWithLifeSignInfo(schedule);

        return schedule;
    }

    private LocalDateTime calculateStartTime(RacingSession session, int position) {

        Period lag = Period.minutes(1);

        LocalDateTime nextPossible = LocalDateTime.now().plus(lag)
                .plus(remainingCurrent(session));

        LocalDateTime start = nextPossible.isAfter(session.getPlannedStartTime()) ?
                nextPossible : session.getPlannedStartTime();

        Period othersDuration = Period.seconds(session.getRunDuration()).plus(lag).multipliedBy(position - 1);

        return start.plus(othersDuration);
    }

    private Period remainingCurrent(RacingSession session) {
        // TODO: Implement
        return Period.minutes(0);
    }

    /**
     * enrich the ScheduledRun entities with recent info about the pilot lifesigns
     * fail silently when pilot info source is not available.
     *
     * @param runs the original list of runs
     */
    private void tryEnrichWithLifeSignInfo(List<ScheduledRun> runs) {

        try {
            PilotInfo pilotInfo = pilotInfoResource.retrieveInfo();
            runs.forEach(
                    (run) -> {
                        run.setPilotState(FuriousRunDto.PilotState.NO_LIFESIGNS);
                        pilotInfo.getPilotLifeSigns().stream().filter(
                                (l) -> l.getTeamId().equals(run.getTeamId())
                        ).forEach((l) -> {
                                    run.setPilotLifeSign(l);
                                    run.setPilotState(
                                            ScheduleService.determineState(l.getTimestamp()));
                                }
                        );
                    });
        } catch (OutboundServiceException ose) {
            logger.error("Cannot access pilot info. Lifesigns not available");
            runs.forEach((r) -> r.setPilotState(FuriousRunDto.PilotState.UNKNOWN));
         }


    }


    /**
     * Create an application for the given session. Do nothing if there already is a non-expired application.
     * Refresh an expired one with recent time.
     *
     * @param notification the training application of a team for a particular session
     */
    @Transactional
    public void applyForTraining(ApplicationNotification notification) {

        if ( notification.getSessionId() == null ) {
            if ( notification.getCompetition() != null ) {
                Long theSingleTrainingSession = findTheSingleTrainingSessionOrFail(notification.getCompetition());
                notification.setSessionId(theSingleTrainingSession);
            }
        }
        TrainingApplication application = assureApplication(notification.getTeamName(), notification.getSessionId());
        application.refreshIfExpired();

        publisher.publishSchedule(notification.getSessionId());
        socialBroadcaster.broadCast ( notification, getSchedule(notification.getSessionId()) );
    }

    /**
     * only returns regularly if there is a single training session in this competition
     * @param competition name of the competition
     * @return the id of the single training session if there is just a single one.
     * @throws IllegalStateException if none or more than a single training sessions are available in the competition.
     */
    private Long findTheSingleTrainingSessionOrFail(String competition ) throws IllegalStateException {

        Long singleTrainingSessionId = null;
        List<RacingSession> sessions = sessionRepo.findByCompetition(competition);
        for ( RacingSession session : sessions ) {
            if ( session.getType() == RacingSession.SessionType.Training ) {
                if ( singleTrainingSessionId == null ) {
                    singleTrainingSessionId = session.getId();
                } else {
                    throw new IllegalStateException("More than a single training session.");
                }
            }
        }
        if ( singleTrainingSessionId == null ) {
            throw new IllegalStateException("Not even a single training session.");
        }
        return singleTrainingSessionId;
    }

    /**
     * increase the number of missed runs on the training application and expires the application
     * creates an application and expires it, if none existed yet.
     *
     * @param notification the context of the performed run
     */
    @Transactional
    public void registerMissedRun(RunMissedNotification notification) {

        TrainingApplication application = assureApplication(notification.getTeamName(), notification.getSessionId());
        application.incrementMissedRunsAndExpire();

        publisher.publishSchedule(notification.getSessionId());
        socialBroadcaster.broadCast ( notification, getSchedule(notification.getSessionId()) );
    }

    /**
     * increase the number of performed runs on the training application and expires the application
     * creates an application and expires it, if none existed yet.
     *
     * @param notification the context of the performed run
     */
    @Transactional
    public void registerPerformedRun(RunPerformedNotification notification) {

        TrainingApplication application = assureApplication(notification.getTeamName(), notification.getSessionId());
        application.incrementPerformedRunsAndExpire();

        publisher.publishSchedule(notification.getSessionId());
        socialBroadcaster.broadCast ( notification, getSchedule(notification.getSessionId()) );
    }

    /**
     * find or create the application for the given team and session
     * create an expired application if there's none yet.
     *
     * @param team      the team name
     * @param sessionId the id of the session
     * @return an existing or new application
     */
    private TrainingApplication assureApplication(String team, Long sessionId) {

        TrainingApplication application = trainingApplicationRepository.findOneByTeamNameAndSessionId(
                team, sessionId);

        if (application == null) {
            application = trainingApplicationRepository.save(new TrainingApplication(team, sessionId, null));
        }

        return application;
    }

}
