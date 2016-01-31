package com.zuehlke.carrera.comp.domain;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zuehlke.carrera.comp.domain.util.CustomDateTimeDeserializer;
import com.zuehlke.carrera.comp.domain.util.CustomDateTimeSerializer;
import com.zuehlke.carrera.relayapi.messages.PilotLifeSign;
import org.joda.time.LocalDateTime;


/**
 *  a single run as scheduled in a free training
 */
public class ScheduledRun implements Comparable<ScheduledRun> {

    private PilotLifeSign pilotLifeSign;

    private FuriousRunDto.PilotState pilotState = FuriousRunDto.PilotState.NO_LIFESIGNS;

    private final String teamId;

    private final Long sessionId;

    private TrainingApplication application;

    private int position;

    private long furiousId;

    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
    private LocalDateTime scheduledStart;

    private RunStatus status = RunStatus.SCHEDULED;

    public ScheduledRun(String teamId, Long sessionId) {
        this.teamId = teamId;
        this.sessionId = sessionId;
    }

    public String getTeamId() {
        return teamId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public LocalDateTime getScheduledStart() {
        return scheduledStart;
    }

    public int getNumberOfMissedRuns() {
        if ( application == null ) {
            return 0;
        }
        return application.getNumberOfMissedRuns();
    }

    public int getNumberOfPerformedRuns() {
        if ( application == null ) {
            return 0;
        }
        return application.getNumberOfPerformedRuns();
    }

    public TrainingApplication getApplication() {
        return application;
    }

    public void setApplication(TrainingApplication application) {
        this.application = application;
    }

    public void setScheduledStart(LocalDateTime scheduledStart) {
        this.scheduledStart = scheduledStart;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public PilotLifeSign getPilotLifeSign() {
        return pilotLifeSign;
    }

    public void setPilotLifeSign(PilotLifeSign pilotLifeSign) {
        this.pilotLifeSign = pilotLifeSign;
    }

    public FuriousRunDto.PilotState getPilotState() {
        return pilotState;
    }

    public void setPilotState(FuriousRunDto.PilotState pilotState) {
        this.pilotState = pilotState;
    }

    public long getFuriousId() {
        return furiousId;
    }

    public void setFuriousId(long furiousId) {
        this.furiousId = furiousId;
    }

    public GuaranteedPosition getGuaranteedPosition() {
        if ( application != null ) {
            return application.getGuaranteedPosition();
        } else {
            return GuaranteedPosition.NONE;
        }
    }

    public void setGuaranteedPosition(GuaranteedPosition guaranteedPosition) {
        if ( application == null ) {
            application = new TrainingApplication();
        }
        application.setGuaranteedPosition (guaranteedPosition);
    }

    private boolean applicationIsValid() {
        return application != null && application.getApplicationTime() != null;
    }

    /**
     * comparison for the order within a schedule
     * Rationale:
     * If only one team has applied that one comes first.
     * If a team had less opportunities - missed or actual - it should come first
     * if same number of opportunities the team with less actually performed runs should come first
     * if same number of performed runs the
     * @param other the other run to compare with
     * @return pos number: the other comes first, neg number: this comes first, 0: can't decide
     */
    @Override
    public int compareTo(ScheduledRun other) {

        if ( applicationIsValid() && !other.applicationIsValid()) {
            return -1;
        }

        if ( !applicationIsValid() && other.applicationIsValid()) {
            return +1;
        }

        int onGuarantees = getGuaranteedPosition().compareTo(other.getGuaranteedPosition());

        if ( onGuarantees != 0 ) {
            return onGuarantees;
        }

        int onRunOpportunities =
                getNumberOfMissedRuns() + getNumberOfPerformedRuns() -
                        (other.getNumberOfMissedRuns() + other.getNumberOfPerformedRuns());

        if (onRunOpportunities != 0) {
            return onRunOpportunities;
        }

        int onPerformedRuns = getNumberOfPerformedRuns() - other.getNumberOfPerformedRuns();

        if (onPerformedRuns != 0) {
            return onPerformedRuns;
        }

        if ((application != null) && (other.application != null)) {
            if ( application.getApplicationTime() == null && other.application.getApplicationTime() == null ) {
                return 0;
            } else if ( application.getApplicationTime() == null && other.application.getApplicationTime() != null ) {
                return +1;
            } else if ( application.getApplicationTime() != null && other.application.getApplicationTime() == null ) {
                return -1;
            }
            return application.getApplicationTime().compareTo(other.application.getApplicationTime());
        }

        return 0;
    }

    public void setNumberOfMissedRuns(int n) {

        if ( application == null ) {
            application = new TrainingApplication();
        }
        application.setNumberOfMissedRuns(n);
    }

    public void setNumberOfPerformedRuns(int n) {

        if ( application == null ) {
            application = new TrainingApplication();
        }
        application.setNumberOfPerformedRuns( n );
    }

    public void setStatus(RunStatus status) {
        this.status = status;
    }

    public RunStatus getStatus() {
        return status;
    }
}


