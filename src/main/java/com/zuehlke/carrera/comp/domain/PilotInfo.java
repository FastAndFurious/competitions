package com.zuehlke.carrera.comp.domain;

import com.zuehlke.carrera.relayapi.messages.PilotLifeSign;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *  Info about recently connected pilots
 */
public class PilotInfo {

    final List<PilotLifeSign> pilotLifeSigns = new ArrayList<>();

    public PilotInfo() {}

    /**
     * copies the map of lifesigns to not reveal state.
     * @param pilotLifeSigns the orignal map of life signs.
     */
    public PilotInfo(Map<String, PilotLifeSign> pilotLifeSigns) {

        pilotLifeSigns.values().forEach(
                this.pilotLifeSigns::add);

    }

    public List<PilotLifeSign> getPilotLifeSigns() {
        return pilotLifeSigns;
    }
}
