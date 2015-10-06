package com.zuehlke.carrera.comp.service;

import com.zuehlke.carrera.comp.web.rest.ServiceResult;
import com.zuehlke.carrera.relayapi.messages.RunRequest;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
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
    public ServiceResult startRun(RunRequest request, Logger logger) {

        try {
            ResponseEntity<String> response = new RestTemplate().postForEntity(relayStartUrl, request, String.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                logger.error("Could not POST race start to " + relayStartUrl + "!");
                logger.error("HTTP Response was: " + response.getBody());
                return new ServiceResult(ServiceResult.Status.NOK, response.getBody());
            } else {
                logger.info("Successful sent POST race start to " + relayStartUrl);
                return new ServiceResult(ServiceResult.Status.OK, "Success");
            }
        } catch ( HttpServerErrorException rce ) {
                String originalCause = rce.getResponseBodyAsString();
                logger.error("Could't start. Relay at " + relayStartUrl + " says: " + originalCause);
                return new ServiceResult(ServiceResult.Status.NOK, originalCause);
        }
    }

    @Override
    public ServiceResult stopRun(RunRequest request, Logger logger) {

        ResponseEntity<String> response = new RestTemplate().postForEntity(relayStopUrl, request, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            logger.error("Could not POST race start to " + relayStopUrl + "!");
            logger.error("HTTP Response was: " + response.getBody());
            return new ServiceResult(ServiceResult.Status.NOK, response.getBody());
        } else {
            logger.info("Successful sent POST race stop to " + relayStopUrl);
            return new ServiceResult(ServiceResult.Status.OK, "Success");
        }
    }
}
