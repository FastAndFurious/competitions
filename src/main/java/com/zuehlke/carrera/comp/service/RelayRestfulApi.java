package com.zuehlke.carrera.comp.service;

import com.zuehlke.carrera.relayapi.messages.RunRequest;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 *  The restful implementation of the relay api
 */
@Component
public class RelayRestfulApi implements RelayApi {

    @Value("${competitions.relayUrl}/start")
    private String relayStartUrl;

    @Value("${competitions.relayUrl}/stop")
    private String relayStopUrl;


    @Override
    public boolean startRun(RunRequest request, Logger logger) {

        ResponseEntity<String> response = new RestTemplate().postForEntity(relayStartUrl, request, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            logger.warn("Could not POST race start to " + relayStartUrl + "!");
            logger.warn("HTTP Response was: " + response.getBody());
            return false;
        } else {
            logger.debug("Successful sent POST race start to " + relayStartUrl);
            return true;
        }


    }

    @Override
    public boolean stopRun(RunRequest request, Logger logger) {

        ResponseEntity<String> response = new RestTemplate().postForEntity(relayStopUrl, request, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            logger.warn("Could not POST race start to " + relayStartUrl + "!");
            logger.warn("HTTP Response was: " + response.getBody());
            return false;
        } else {
            logger.debug("Successful sent POST race start to " + relayStartUrl);
            return true;
        }
    }
}
