package com.zuehlke.carrera.comp.integrationtests;

import com.zuehlke.carrera.comp.CompetitionManagerApp;
import com.zuehlke.carrera.comp.domain.*;
import com.zuehlke.carrera.comp.repository.TeamRegistrationRepository;
import com.zuehlke.carrera.comp.service.ErroneousLifeSign;
import com.zuehlke.carrera.comp.service.MockCompetitionStatePublisher;
import com.zuehlke.carrera.comp.service.MockPilotInfoResource;
import com.zuehlke.carrera.comp.web.rest.*;
import com.zuehlke.carrera.relayapi.messages.PilotLifeSign;
import com.zuehlke.carrera.relayapi.messages.RoundTimeMessage;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  A complete integration test for running a comp
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CompetitionManagerApp.class)
@WebAppConfiguration
@IntegrationTest
@Transactional
public class RaceManagementIntegrationTest {

    private static final String COMPETITION_NAME = "TEST_COMPETITION";
    private static final String TRACK_ID = "TRACK_ID";
    private static final Map<Long, Map<Long, Long[]>> roundTimes = new HashMap<>();
    private static final List<Integer[]> scores = new ArrayList<>();
    private static final Map<String, Integer> names = new HashMap<>();

    @Autowired
    private CompetitionResource competitionResource;

    @Autowired
    private RacingSessionResource sessionResource;

    @Autowired
    private TeamRegistrationResource registrationResource;

    @Autowired
    private FuriousRunResource runResource;

    @Autowired
    private RoundTimeResource roundTimeResource;

    @Autowired
    MockCompetitionStatePublisher publisher;

    @Autowired
    MockPilotInfoResource pilotInfoResource;

    @Autowired
    TeamRegistrationRepository teamRepo;


    private int roundEventCounter;

    @Before
    public void init () {
        for ( Long l = 1L; l <= 4L; l++ ) {
            roundTimes.put ( l, new HashMap<>());
        }
        // Training bernies
        roundTimes.get(1L).put(1L, new Long[]{35000L, 24000L, 19000L});
        // Training steffies
        roundTimes.get(1L).put(2L, new Long[]{41000L, 28000L, 25000L});
        // Training harries
        roundTimes.get(1L).put(3L, new Long[]{45000L, 35000L, 29000L});
        // Training wolfies
        roundTimes.get(1L).put(4L, new Long[]{40000L, 30000L, 20000L});

        // in the order w, h, s, b
        // Wolfie's rounds
        scores.add(new Integer[]{1,0,0,0});
        scores.add(new Integer[]{1,0,0,0});
        scores.add(new Integer[]{1,0,0,0});
        // Harrie's rounds
        scores.add(new Integer[]{1,2,0,0});
        scores.add(new Integer[]{1,2,0,0});
        scores.add(new Integer[]{1,2,0,0});
        // Steffie's rounds
        scores.add(new Integer[]{1,2,3,0});
        scores.add(new Integer[]{1,3,2,0});
        scores.add(new Integer[]{1,3,2,0});
        // Bernie's rounds
        scores.add(new Integer[]{1,3,2,4});
        scores.add(new Integer[]{1,4,3,2});
        scores.add(new Integer[]{2,4,3,1});

        names.put("wolfies", 0);
        names.put("harries", 1);
        names.put("steffies", 2);
        names.put("bernies", 3);

    }



    @Test
    public void testFullRaceLifeCycle () throws URISyntaxException {

        create_a_new_competition ();

        create_sessions ();

        register_teams ();

        create_quali_schedule ( true ); // expect empty result

        create_training_schedule ();

        create_quali_schedule ( true ); // expect empty result

        simulate_training_runs();

        create_quali_schedule ( false );

        simulate_lifesigns ();

        erroneousLifeSigns ();

    }

    private void simulate_lifesigns() {
        long now = System.currentTimeMillis();
        pilotInfoResource.registerLifeSign ( new PilotLifeSign("wolfies", "no_access", "url", now ));
        pilotInfoResource.registerLifeSign ( new PilotLifeSign("mummies", "access", "url", now ));
        pilotInfoResource.registerLifeSign ( new PilotLifeSign("harries", "access", "url", now ));
        pilotInfoResource.registerLifeSign ( new PilotLifeSign("bernies", "access", "url", now ));
        pilotInfoResource.registerLifeSign ( new PilotLifeSign("steffies", "access", "url", now ));
    }

    private void erroneousLifeSigns() {

        List<ErroneousLifeSign> errors = sessionResource.findErroneousLifeSigns();
        Assert.assertEquals ( 2, errors.size());
        Assert.assertTrue ( errors.stream().anyMatch(e -> e.getTeamId().equals("mummies")));
        Assert.assertTrue ( errors.stream().anyMatch(e -> e.getTeamId().equals("wolfies")));
    }


    private void simulate_training_runs() {

        roundEventCounter = 0;

        for ( String team : new String[]{"wolfies", "harries", "steffies", "bernies"}) {

            simulate_training_start ( team );
        }

        List<RoundTime> roundTimes = roundTimeResource.getAll();
        Assert.assertEquals ( 12, roundTimes.size() );
    }

    private void simulate_training_start(String team) {
        List<RacingSession> allSessions = sessionResource.getAll();
        allSessions.stream().filter((s)->s.getType()== RacingSession.SessionType.Training).forEach((s)->{
            runResource.getSchedule( s.getId() ).stream().filter((r)->r.getTeam().equals(team)).forEach((r)->{
                runResource.startRun(r.getId());
                try {
                    simulate_roundTimes ( team, s.getId(), r.getId());
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
                runResource.stopRun(r.getId());
            });
        });
    }

    private void simulate_roundTimes(String team, Long sessionId, Long runId) throws URISyntaxException {

        for ( int round = 0; round < 3; round ++ ) {
            RoundTimeMessage message = new RoundTimeMessage(TRACK_ID, team, System.currentTimeMillis(),
                    roundTimes.get(sessionId).get(runId)[round]);
            roundTimeResource.register(message);

            assertState ( roundEventCounter++, publisher.getState());
        }
    }


    private void assertState(int roundEvent, CompetitionState state) {

        Integer[] score = scores.get(roundEvent);

        for ( int pos = 0; pos < state.getCurrentBoard().size(); pos ++ ) {
            String team = state.getCurrentBoard().get(pos).getTeam();
            int id = names.get(team);
            if ( score[id] != pos + 1 ) {
                System.out.println("STOP!");
            }
            Assert.assertEquals((long) score[id], pos+1);
        }
    }

    /**
     * Training schedule: first to register may start latest, which is an advantage, because the team
     * can learn from the others while waiting for their term.
     */
    private void create_training_schedule() {

        List<RacingSession> allSessions = sessionResource.getAll();
        allSessions.stream().filter((s)->s.getType()== RacingSession.SessionType.Training).forEach((s)->{
            List<FuriousRunDto> runs = runResource.getSchedule( s.getId() );
            Assert.assertEquals ( 4, runs.size());
            Assert.assertEquals ( "wolfies", runs.get(3).getTeam());
            Assert.assertEquals ( "harries", runs.get(2).getTeam());
            Assert.assertEquals ( "steffies", runs.get(1).getTeam());
            Assert.assertEquals ( "bernies", runs.get(0).getTeam());
        });

    }

    private void create_quali_schedule( boolean expectEmpty ) {

        List<RacingSession> allSessions = sessionResource.getAll();
        allSessions.stream().filter((s)->s.getType()== RacingSession.SessionType.Qualifying).forEach((s)->{
            List<FuriousRunDto> runs = runResource.getSchedule( s.getId() );
            if ( expectEmpty ) {
                Assert.assertEquals(0, runs.size());
            } else {
                Assert.assertEquals(4, runs.size());
                Assert.assertEquals("harries", runs.get(0).getTeam());
                Assert.assertEquals("steffies", runs.get(1).getTeam());
                Assert.assertEquals("wolfies", runs.get(2).getTeam());
                Assert.assertEquals("bernies", runs.get(3).getTeam());
            }
        });

    }

    private void register_teams() throws URISyntaxException {

        LocalDateTime first = new LocalDateTime(2015,9,1,15,0,0,0);
        LocalDateTime second = first.plusMinutes(10);
        LocalDateTime third = second.plusMinutes(10);
        LocalDateTime fourth = third.plusMinutes(10);

        String protocol = "rabbit";
        String encoding = "jason";

        TeamRegistration wolfies  = new TeamRegistration(null, COMPETITION_NAME, "wolfies", "access", protocol, encoding, first);
        registrationResource.create(wolfies);

        TeamRegistration harries  = new TeamRegistration(null, COMPETITION_NAME, "harries", "access", protocol, encoding, second);
        registrationResource.create(harries);

        TeamRegistration steffies  = new TeamRegistration(null, COMPETITION_NAME, "steffies", "access", protocol, encoding, third);
        registrationResource.create(steffies);

        TeamRegistration bernies  = new TeamRegistration(null, COMPETITION_NAME, "bernies", "access", protocol, encoding, fourth);
        registrationResource.create(bernies);

        TeamRegistration reg = teamRepo.findByTeam("Wolfies");
        Assert.assertNull ( reg );
    }


    private void create_sessions() throws URISyntaxException {

        RacingSession training = new RacingSession(null, COMPETITION_NAME, RacingSession.SessionType.Training,
                1, new LocalDateTime(), TRACK_ID, "Hollywood");
        ResponseEntity<Void> result = sessionResource.create(training);
        Assert.assertEquals ( HttpStatus.CREATED, result.getStatusCode());

        RacingSession quali = new RacingSession(null, COMPETITION_NAME, RacingSession.SessionType.Qualifying,
                1, new LocalDateTime(), TRACK_ID, "Hollywood");
        sessionResource.create(quali);

        RacingSession finale = new RacingSession(null, COMPETITION_NAME, RacingSession.SessionType.Competition,
                1, new LocalDateTime(), TRACK_ID, "Hollywood");
        sessionResource.create(finale);

        Assert.assertEquals ( 3, sessionResource.findByCompetition(COMPETITION_NAME).size());
    }


    private void create_a_new_competition() throws URISyntaxException {

        Competition competition = new Competition(null,  "TEST_COMPETITION", TRACK_ID, new LocalDate());
        competitionResource.create(competition);
    }
}
