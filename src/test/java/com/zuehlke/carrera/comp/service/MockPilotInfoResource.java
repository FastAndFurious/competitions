package com.zuehlke.carrera.comp.service;

import com.zuehlke.carrera.comp.domain.PilotInfo;
import com.zuehlke.carrera.comp.web.outbound.PilotInfoResource;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary // overrides for testing purposes
public class MockPilotInfoResource implements PilotInfoResource{
    @Override
    public PilotInfo retrieveInfo() {
        return new PilotInfo();
    }
}
