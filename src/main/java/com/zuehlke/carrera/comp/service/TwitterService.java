package com.zuehlke.carrera.comp.service;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.social.twitter.api.impl.TwitterTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class TwitterService extends SocialBroadcaster {

    private static final Logger logger = LoggerFactory.getLogger(TwitterService.class);

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

        return new TwitterTemplate(consumerKey, consumerSecret, accessToken, accessTokenSecret);
    }

    @Override
    protected void sendMessage(List<String> twitterNames, String message) {

        String concatenatedNames = StringUtils.collectionToDelimitedString(twitterNames, ", ");

        if (messagesEnabled) {
            logger.info("Informing {}: '{}'", concatenatedNames, message);
        } else {
            logger.info("Twitter messages disabled. Not sending '{}' to {}.", message, concatenatedNames);
            return;
        }

        Twitter template = getTwitterTemplate("twitter");


        for ( String twitterName : twitterNames ) {
            try {
                template.directMessageOperations().sendDirectMessage(twitterName, message);
            } catch ( Exception e ) {
                logger.error("Failed sending message to {}.", twitterName );
                logger.error(e.getMessage());
            }
        }
    }


}
