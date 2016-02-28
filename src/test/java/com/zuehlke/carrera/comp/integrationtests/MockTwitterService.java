package com.zuehlke.carrera.comp.integrationtests;


import com.zuehlke.carrera.comp.service.TwitterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
@Primary // overrides hot implementation when in classpath (that's during tests).
public class MockTwitterService extends TwitterService {

    private static final Logger logger = LoggerFactory.getLogger(MockTwitterService.class);

    @Override
    protected void tweet(List<String> twitterNames, String message) {
        logger.info("Would send message to " +
                StringUtils.collectionToCommaDelimitedString(twitterNames) + ": " + message);
    }
}
