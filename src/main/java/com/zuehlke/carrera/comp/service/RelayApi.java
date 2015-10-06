package com.zuehlke.carrera.comp.service;

import com.zuehlke.carrera.comp.web.rest.ServiceResult;
import com.zuehlke.carrera.relayapi.messages.RunRequest;
import org.slf4j.Logger;

/**
 *
 */
public interface RelayApi {


    ServiceResult startRun (RunRequest request, Logger logger);

    ServiceResult stopRun ( RunRequest request, Logger logger);
}
