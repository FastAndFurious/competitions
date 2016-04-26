package com.zuehlke.carrera.comp.integrationtests;

import com.zuehlke.carrera.comp.repository.TeamRegistrationRepository;
import com.zuehlke.carrera.comp.service.MockCompetitionStatePublisher;
import com.zuehlke.carrera.comp.service.MockPilotInfoResource;
import com.zuehlke.carrera.comp.web.rest.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * a class to bundle all the services involved in the comp server
 */
@Component
public class TestSystem {

    public final CompetitionResource competitionResource;

    public final RacingSessionResource sessionResource;

    public final TeamRegistrationResource registrationResource;

    public final FuriousRunResource runResource;

    public final RoundTimeResource roundTimeResource;

    public final MockCompetitionStatePublisher publisher;

    public final MockPilotInfoResource pilotInfoResource;

    public final TeamRegistrationRepository teamRepo;

    public final FreeTrainingResource freeTrainingResource;

    public final MockTwitterService twitterService;

    @Autowired
    public TestSystem(CompetitionResource competitionResource,
                      RacingSessionResource sessionResource,
                      TeamRegistrationResource registrationResource,
                      FuriousRunResource runResource,
                      RoundTimeResource roundTimeResource,
                      MockCompetitionStatePublisher publisher,
                      MockPilotInfoResource pilotInfoResource,
                      TeamRegistrationRepository teamRepo,
                      FreeTrainingResource freeTrainingResource,
                      MockTwitterService twitterService) {
        this.competitionResource = competitionResource;
        this.sessionResource = sessionResource;
        this.registrationResource = registrationResource;
        this.runResource = runResource;
        this.roundTimeResource = roundTimeResource;
        this.publisher = publisher;
        this.pilotInfoResource = pilotInfoResource;
        this.teamRepo = teamRepo;
        this.freeTrainingResource = freeTrainingResource;
        this.twitterService = twitterService;
    }
}
