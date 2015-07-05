package com.zuehlke.carrera.comp.service;

import com.zuehlke.carrera.comp.web.rest.RunRequest;
import org.slf4j.Logger;

/**
 *
 */
public interface RelayApi {


    boolean startRun (RunRequest request, Logger logger);

    boolean stopRun ( RunRequest request, Logger logger);
}
