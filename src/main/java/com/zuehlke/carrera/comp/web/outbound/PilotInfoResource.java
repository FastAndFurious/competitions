package com.zuehlke.carrera.comp.web.outbound;

import com.zuehlke.carrera.comp.domain.PilotInfo;


public interface PilotInfoResource {
    PilotInfo retrieveInfo() throws OutboundServiceException;
}
