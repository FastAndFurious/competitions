package com.zuehlke.carrera.comp.service;

import com.zuehlke.carrera.comp.web.rest.ServiceResult;
import com.zuehlke.carrera.relayapi.messages.RunRequest;
import org.slf4j.Logger;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;

/**
 *  Mocks a Relay API to avoid network access for the integrationstests
 */
@Component
@Primary // Will only be found during test executions, and that's good
public class MockRelayApi implements RelayApi{
    @Override
    public ServiceResult startRun(RunRequest request, Logger logger) {
        return new ServiceResult(ServiceResult.Status.OK, "mocked anyway");
    }

    @Override
    public ServiceResult stopRun(RunRequest request, Logger logger) {
        return new ServiceResult(ServiceResult.Status.OK, "mocked anyway");
    }
}
