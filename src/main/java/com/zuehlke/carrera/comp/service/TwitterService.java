package com.zuehlke.carrera.comp.service;

import com.google.common.base.Preconditions;
import com.zuehlke.carrera.comp.domain.*;
import com.zuehlke.carrera.comp.repository.TeamRegistrationRepository;
import org.joda.time.Duration;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.social.twitter.api.DirectMessage;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class TwitterService implements SocialBroadcaster {

    private static final Duration NOTIFY_WHEN_THIS_CLOSE = Duration.standardMinutes(15);

    private static final Logger logger = LoggerFactory.getLogger(TwitterService.class);

    @Autowired
    private TeamRegistrationRepository teamRepo;

    @Autowired
    private Environment env;

    private Twitter getTwitterTemplate(String accountName) {
        String consumerKey = env.getProperty(accountName + ".consumerKey");
        String consumerSecret = env.getProperty(accountName + ".consumerSecret");
        String accessToken = env.getProperty(accountName + ".accessToken");
        String accessTokenSecret = env.getProperty(accountName + ".accessTokenSecret");
        Preconditions.checkNotNull(consumerKey);
        Preconditions.checkNotNull(consumerSecret);
        Preconditions.checkNotNull(accessToken);
        Preconditions.checkNotNull(accessTokenSecret);

        TwitterTemplate twitterTemplate =
                new TwitterTemplate(consumerKey, consumerSecret, accessToken, accessTokenSecret);
        return twitterTemplate;

    }


    protected void tweet(List<String> twitterNames, String message) {

        logger.info("Informing " + StringUtils.collectionToDelimitedString(twitterNames, ", ") + ": " + message);
        Twitter template = getTwitterTemplate("twitter");

        for ( String twitterName : twitterNames ) {
            try {
                DirectMessage directMessage = template.directMessageOperations().sendDirectMessage(twitterName, message);
            } catch ( Exception e ) {
                logger.error("Failed sending message to " + twitterName );
                logger.error(e.getMessage());
            }
        }
    }

    @Override
    public void broadCast (ApplicationNotification application, List<ScheduledRun> schedule ) {

        LocalDateTime now = LocalDateTime.now();
        for ( ScheduledRun run : schedule ) {
            if (run.getScheduledStart().minus(NOTIFY_WHEN_THIS_CLOSE).isBefore(now)) {
                TeamRegistration registration = teamRepo.findByTeam(run.getTeamId());
                List<String> names = registration.getListOfTwitterNames();
                tweet(names, createMessage(run));
            }
        }
    }

    @Override
    public void broadCast(RunMissedNotification missed, List<ScheduledRun> schedule ) {
        LocalDateTime now = LocalDateTime.now();
        for ( ScheduledRun run : schedule ) {
            if (run.getScheduledStart().minus(NOTIFY_WHEN_THIS_CLOSE).isBefore(now)) {
                TeamRegistration registration = teamRepo.findByTeam(run.getTeamId());
                List<String> names = registration.getListOfTwitterNames();
                tweet(names, createMessage(run));
            }
        }

    }

    @Override
    public void broadCast(RunPerformedNotification performed, List<ScheduledRun> schedule ) {
        LocalDateTime now = LocalDateTime.now();
        for ( ScheduledRun run : schedule ) {
            if (run.getScheduledStart().minus(NOTIFY_WHEN_THIS_CLOSE).isBefore(now)) {
                TeamRegistration registration = teamRepo.findByTeam(run.getTeamId());
                List<String> names = registration.getListOfTwitterNames();
                tweet(names, createMessage(run));
            }
        }

    }

    private String createMessage(ScheduledRun run) {
        return "Hi " + run.getTeamId() + ". Get ready! You're scheduled to run at " + run.getScheduledStart().toString("HH:mm");
    }


}
