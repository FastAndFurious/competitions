package com.zuehlke.carrera.comp.service;

import com.zuehlke.carrera.comp.domain.PilotInfo;
import com.zuehlke.carrera.comp.web.outbound.PilotInfoResource;
import com.zuehlke.carrera.relayapi.messages.PilotLifeSign;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Primary // overrides for testing purposes
public class MockPilotInfoResource implements PilotInfoResource{

    Map<String, PilotLifeSign> recentInfo = new HashMap<>();

    @Override
    public PilotInfo retrieveInfo() {
        return new PilotInfo(recentInfo);
    }

    public void registerLifeSign(PilotLifeSign pilotLifeSign) {
        recentInfo.put(pilotLifeSign.getTeamId(), pilotLifeSign);
    }
}
