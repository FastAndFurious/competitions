package com.zuehlke.carrera.comp.service;

import com.zuehlke.carrera.relayapi.messages.PilotLifeSign;

public class ErroneousLifeSign extends PilotLifeSign {

    public enum Reason {
        UNKNOWN_TEAM,
        NOT_REGISTERED,
        INVALID_ACCESS_CODE
    }

    private Reason reason;

    private int ageInSeconds;

    public ErroneousLifeSign() {} // for serialization purposes

    public ErroneousLifeSign ( PilotLifeSign originalLifeSign, Reason reason ) {
        super(originalLifeSign.getTeamId(), originalLifeSign.getAccessCode(), originalLifeSign.getOptionalUrl(), originalLifeSign.getTimestamp());
        this.reason = reason;
        // this is calculated here because clients may have clock offsets
        this.ageInSeconds = (int)(System.currentTimeMillis() - getTimestamp()) / 1000;
    }

    public Reason getReason() {
        return reason;
    }

    public void setReason(Reason reason) {
        this.reason = reason;
    }

    public int getAgeInSeconds() {
        return ageInSeconds;
    }

    public void setAgeInSeconds(int ageInSeconds) {
        this.ageInSeconds = ageInSeconds;
    }
}
