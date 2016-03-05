package com.zuehlke.carrera.comp.service;

import com.zuehlke.carrera.comp.domain.*;
import com.zuehlke.carrera.comp.repository.TeamRegistrationRepository;
import com.zuehlke.carrera.comp.repository.TrainingApplicationRepository;
import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public abstract class SocialBroadcaster {

    private static final Logger logger = LoggerFactory.getLogger(SocialBroadcaster.class);

    private static final Duration NOTIFY_WHEN_THIS_CLOSE = Duration.standardMinutes(6);

    @Autowired
    private TeamRegistrationRepository teamRepository;

    @Autowired
    private TrainingApplicationRepository applicationRepository;

    @Value("${competitions.twitter}")
    protected boolean messagesEnabled;

    protected abstract void sendMessage(List<String> addresses, String message );




    /**
     * Broadcast start times to the ones who need to know:
     * 1) Messages are only sent to participants when start is close enough to "now".
     *    Close enough is determined by NOTIFY_WHEN_THIS_CLOSE parameter.
     * 2) The applicant gets a message, when 1)
     * 3) other applicants get a message when they're after the applicant and 1)
     * @param application the new application to be considered
     * @param schedule the new schedule
     */
    public void broadCast (ApplicationNotification application, List<ScheduledRun> schedule) {
        broadCast( application, schedule, this::sendMessage);
    }


    public void broadCast (ApplicationNotification application, List<ScheduledRun> schedule, BroadCaster broadcaster ) {

        ScheduledRun applicantsRun = schedule.stream()
                .filter(r->r.getTeamId().equals(application.getTeamName()))
                .findFirst().get();

        LocalDateTime now = LocalDateTime.now();
        for ( ScheduledRun run : schedule ) {
            if (run.getScheduledStart().minus(NOTIFY_WHEN_THIS_CLOSE).isBefore(now)) {
                TeamRegistration registration = teamRepository.findByTeam(run.getTeamId());
                List<String> names = registration.getListOfTwitterNames();

                // applicant message
                if ( application.getTeamName().equals(run.getTeamId())) {
                    broadcaster.broadcast (names, createMessage(run));
                    continue;
                }

                TrainingApplication otherTeamsApplication = applicationRepository.findOneByTeamNameAndSessionId(
                        registration.getTeam(), application.getSessionId());

                // continue if other team hasn't applied yet.
                if (otherTeamsApplication == null || otherTeamsApplication.getApplicationTime() == null) {
                    logger.info("Not informing {}. They haven't applied yet.",
                            run.getTeamId());
                    continue;
                }

                // applicant is now scheduled before this run
                if ( applicantsRun.getScheduledStart().isBefore(run.getScheduledStart())) {
                    broadcaster.broadcast (names, createMessage(run));
                }
            }
            else {
                logger.info("Not informing {}. They'll start only in {} seconds at {}.",
                        run.getTeamId(),
                        (run.getScheduledStart().toDateTime().getMillis() - now.toDateTime().getMillis()) / 1000,
                        run.getScheduledStart().toString("HH:mm"));
            }
        }
    }


    public void broadCast(RunMissedNotification missed, List<ScheduledRun> schedule ) {
        broadCast(missed, schedule, this::sendMessage);
    }

    public void broadCast(RunMissedNotification missed, List<ScheduledRun> schedule, BroadCaster broadcaster ) {
        logger.info("Broadcasing new schedule after missed run");
        LocalDateTime now = LocalDateTime.now();
        for ( ScheduledRun run : schedule ) {
            if (run.getScheduledStart().minus(NOTIFY_WHEN_THIS_CLOSE).isBefore(now)) {
                TeamRegistration registration = teamRepository.findByTeam(run.getTeamId());
                List<String> names = registration.getListOfTwitterNames();
                broadcaster.broadcast(names, createMessage(run));
            } else {
                logger.info("Not informing {}. They'll start only in {} seconds",
                        run.getTeamId(),
                        (run.getScheduledStart().toDateTime().getMillis() - now.toDateTime().getMillis()) / 1000);
            }
        }
    }

    public void broadCast(RunPerformedNotification performed, List<ScheduledRun> schedule ) {
        broadCast(performed, schedule, this::sendMessage);
    }

    public void broadCast(RunPerformedNotification performed, List<ScheduledRun> schedule, BroadCaster broadcaster ) {
        logger.info("Broadcasing new schedule after performed run");
        LocalDateTime now = LocalDateTime.now();
        for ( ScheduledRun run : schedule ) {
            if (run.getScheduledStart().minus(NOTIFY_WHEN_THIS_CLOSE).isBefore(now)) {
                TeamRegistration registration = teamRepository.findByTeam(run.getTeamId());
                List<String> names = registration.getListOfTwitterNames();
                broadcaster.broadcast(names, createMessage(run));
            }
        }
    }

    private String createMessage(ScheduledRun run) {
        return "Hi " + run.getTeamId() + ". Get ready! You're scheduled to run at " + run.getScheduledStart().toString("HH:mm");
    }


}
