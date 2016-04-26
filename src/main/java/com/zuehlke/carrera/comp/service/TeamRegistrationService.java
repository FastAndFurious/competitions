package com.zuehlke.carrera.comp.service;

import com.zuehlke.carrera.comp.domain.TeamRegistration;
import com.zuehlke.carrera.comp.repository.TeamRegistrationRepository;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *  A special service for simplification of the onboarding process
 *  creates a given number of anonymous registrations for a given competitions
 */
@Service
public class TeamRegistrationService {

    private final TeamRegistrationRepository repo;

    @Autowired
    public TeamRegistrationService(TeamRegistrationRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public void createRegistrations ( String competition, int numberOfRegistrations ) {

        for ( int i = 0; i < numberOfRegistrations; i++ ) {
            String team = "Team-" + i;
            String protocol = "rabbit";
            String encoding = "json";
            LocalDateTime time = LocalDateTime.now();
            TeamRegistration reg = new TeamRegistration(null, competition, team, team, protocol, encoding, time);
            repo.save(reg);
        }
    }
}
