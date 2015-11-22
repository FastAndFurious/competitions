package com.zuehlke.carrera.comp.web.outbound;

import com.zuehlke.carrera.comp.domain.PilotInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

/**
 * encapsulates the service request to the relay for infos about the pilot.
 * Logs the failure and returns empty info when the REST call fails.
 */
@Component
public class RestfulPilotInfoResource implements PilotInfoResource {

    private final Logger LOGGER = LoggerFactory.getLogger(RestfulPilotInfoResource.class);

    @Value("${competitions.pilotInfoUrl}")
    private String pilotUrlInfo;

    @Override
    public PilotInfo retrieveInfo() {
        PilotInfo response = new PilotInfo();
        try {
            response =
                    new RestTemplate().getForObject(pilotUrlInfo, PilotInfo.class);
        } catch ( ResourceAccessException rae ) {
            rae.printStackTrace();
            LOGGER.warn("Couldn't retrieve pilot info from relay at " + pilotUrlInfo);
            LOGGER.warn("Returning an empty pilot info object, instead.");
        }

        return response;
    }
}
