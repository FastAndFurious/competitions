package com.zuehlke.carrera.comp.web.outbound;

import com.zuehlke.carrera.comp.domain.PilotInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * encapsulates an outbound service request
 */
@Component
public class PilotInfoResource {

    @Value("${competitions.pilotInfoUrl}")
    private String pilotUrlInfo;

    public PilotInfo retrieveInfo () {
        PilotInfo response =
                new RestTemplate().getForObject(pilotUrlInfo, PilotInfo.class);

        return response;
    }
}
