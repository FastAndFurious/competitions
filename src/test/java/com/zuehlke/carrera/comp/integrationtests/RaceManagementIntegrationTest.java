package com.zuehlke.carrera.comp.integrationtests;

import com.zuehlke.carrera.comp.CompetitionManagerApp;
import com.zuehlke.carrera.comp.domain.*;
import com.zuehlke.carrera.comp.repository.TeamRegistrationRepository;
import com.zuehlke.carrera.comp.service.ErroneousLifeSign;
import com.zuehlke.carrera.comp.service.MockCompetitionStatePublisher;
import com.zuehlke.carrera.comp.service.MockPilotInfoResource;
import com.zuehlke.carrera.comp.service.TeamRegistrationService;
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
    private TestSystem t;

    @Autowired
    private PerformTraining training;

    @Autowired
    private TeamRegistrationService registrationService;

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

        training.create_training_schedule ();

        create_quali_schedule ( true ); // expect empty result

        simulate_free_training_schedule();

        simulate_training_runs();

        create_quali_schedule ( false );

        register_batch ();

        simulate_lifesigns ();

        erroneousLifeSigns ();

    }

    private void simulate_lifesigns() {
        long now = System.currentTimeMillis();
        t.pilotInfoResource.registerLifeSign ( new PilotLifeSign("wolfies", "no_access", "url", now ));
        t.pilotInfoResource.registerLifeSign ( new PilotLifeSign("mummies", "access", "url", now ));
        t.pilotInfoResource.registerLifeSign ( new PilotLifeSign("harries", "access", "url", now ));
        t.pilotInfoResource.registerLifeSign ( new PilotLifeSign("bernies", "access", "url", now ));
        t.pilotInfoResource.registerLifeSign ( new PilotLifeSign("steffies", "access", "url", now ));
    }

    private void erroneousLifeSigns() {

        List<ErroneousLifeSign> errors = t.sessionResource.findErroneousLifeSigns();
        Assert.assertEquals ( 2, errors.size());
        Assert.assertTrue ( errors.stream().anyMatch(e -> e.getTeamId().equals("mummies")));
        Assert.assertTrue ( errors.stream().anyMatch(e -> e.getTeamId().equals("wolfies")));
    }

    private void sleepWell(int millies) {
        try {
            Thread.sleep( millies );
        } catch ( Exception e ) {
            throw new RuntimeException(e);
        }
    }

    private void simulate_free_training_schedule() {


        List<RacingSession> allSessions = t.sessionResource.getAll();
        allSessions.stream().filter((s)->s.getType()== RacingSession.SessionType.Training).forEach((s)-> {

            /**
             * the first three teams apply and will start in the order of application
             */
            try {
                t.freeTrainingResource.applyForTraining(new ApplicationNotification(null, "harries", s.getId()));
                Assert.assertEquals ( 1, t.twitterService.getMessages().size());
                Assert.assertEquals ( 4, t.publisher.getSchedule().getCurrentPositions().size());
                sleepWell(1000);
                t.freeTrainingResource.applyForTraining(new ApplicationNotification(null, "wolfies", s.getId()));
                Assert.assertEquals ( 2, t.twitterService.getMessages().size());
                sleepWell(1000);
                t.freeTrainingResource.applyForTraining(new ApplicationNotification(null, "steffies", s.getId()));
                Assert.assertEquals ( 2, t.twitterService.getMessages().size());
                sleepWell(1000);
            } catch (URISyntaxException e) {
                Assert.fail("Caught Exception. Wasn't expecting that.");
            }
            List<ScheduledRun> runs = t.freeTrainingResource.getSchedule(s.getId());
            assertOrder ( runs, "harries", "wolfies", "steffies", "bernies");

            /**
             * the first team runs, the fourth team applies
             */
            try {
                t.freeTrainingResource.applyForTraining(new ApplicationNotification(null, "bernies", s.getId()));
                t.freeTrainingResource.registerPerformedRun ( new RunPerformedNotification("harries", s.getId()));
            } catch (URISyntaxException e) {
                Assert.fail("Caught Exception. Wasn't expecting that.");
            }
            runs = t.freeTrainingResource.getSchedule(s.getId());
            assertOrder ( runs, "wolfies", "steffies", "bernies", "harries");

        });
    }

    private void assertOrder ( List<ScheduledRun> runs, String first, String second, String third, String fourth ) {

        ScheduleUtils.println ( runs, runs.size() );
        Assert.assertTrue (first + " should be first", first.equals(runs.get(0).getTeamId()));
        Assert.assertTrue (second + " should be second", second.equals(runs.get(1).getTeamId()));
        Assert.assertTrue (third + " should be third", third.equals(runs.get(2).getTeamId()));
        Assert.assertTrue (fourth + " should be fourth", fourth.equals(runs.get(3).getTeamId()));
    }

    private void simulate_training_runs() {

        roundEventCounter = 0;

        for ( String team : new String[]{"wolfies", "harries", "steffies", "bernies"}) {

            simulate_training_run( team );
        }

        List<RoundTime> roundTimes = t.roundTimeResource.getAll();
        Assert.assertEquals ( 12, roundTimes.size() );
    }

    private void simulate_training_run(String team) {
        List<RacingSession> allSessions = t.sessionResource.getAll();
        allSessions.stream().filter((s)->s.getType()== RacingSession.SessionType.Training).forEach((s)->{

            t.runResource.getSchedule( s.getId() ).stream().filter((r)->r.getTeam().equals(team)).forEach((r)->{
                t.runResource.startRun(r.getId());
                try {
                    simulate_roundTimes ( team, s.getId(), r.getId());
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
                t.runResource.stopRun(r.getId());
            });
        });
    }

    private void simulate_roundTimes(String team, Long sessionId, Long runId) throws URISyntaxException {

        for ( int round = 0; round < 3; round ++ ) {
            RoundTimeMessage message = new RoundTimeMessage(TRACK_ID, team, System.currentTimeMillis(),
                    roundTimes.get(sessionId).get(runId)[round]);
            t.roundTimeResource.register(message);

            assertState ( roundEventCounter++, t.publisher.getState());
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

    private void create_quali_schedule( boolean expectEmpty ) {

        List<RacingSession> allSessions = t.sessionResource.getAll();
        allSessions.stream().filter((s)->s.getType()== RacingSession.SessionType.Qualifying).forEach((s)->{
            List<FuriousRunDto> runs = t.runResource.getSchedule( s.getId() );
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
        wolfies.setTwitterNames("@wgiersche");
        t.registrationResource.create(wolfies);

        TeamRegistration harries  = new TeamRegistration(null, COMPETITION_NAME, "harries", "access", protocol, encoding, second);
        harries.setTwitterNames("@wgiersche");
        t.registrationResource.create(harries);

        TeamRegistration steffies  = new TeamRegistration(null, COMPETITION_NAME, "steffies", "access", protocol, encoding, third);
        steffies.setTwitterNames("@wgiersche");
        t.registrationResource.create(steffies);

        TeamRegistration bernies  = new TeamRegistration(null, COMPETITION_NAME, "bernies", "access", protocol, encoding, fourth);
        bernies.setTwitterNames("@wgiersche");
        t.registrationResource.create(bernies);

        TeamRegistration reg = t.teamRepo.findByTeam("Wolfies");
        Assert.assertNull ( reg );
    }

    private void register_batch () {
        registrationService.createRegistrations(COMPETITION_NAME, 5);
    }

    private void create_sessions() throws URISyntaxException {

        RacingSession training = new RacingSession(null, COMPETITION_NAME, RacingSession.SessionType.Training,
                1, new LocalDateTime(), TRACK_ID, "Hollywood", 180);
        ResponseEntity<Void> result = t.sessionResource.create(training);
        Assert.assertEquals ( HttpStatus.CREATED, result.getStatusCode());

        RacingSession quali = new RacingSession(null, COMPETITION_NAME, RacingSession.SessionType.Qualifying,
                1, new LocalDateTime(), TRACK_ID, "Hollywood", 180 );
        t.sessionResource.create(quali);

        RacingSession finale = new RacingSession(null, COMPETITION_NAME, RacingSession.SessionType.Competition,
                1, new LocalDateTime(), TRACK_ID, "Hollywood", 180);
        t.sessionResource.create(finale);

        Assert.assertEquals ( 3, t.sessionResource.findByCompetition(COMPETITION_NAME).size());
    }


    private void create_a_new_competition() throws URISyntaxException {

        Competition competition = new Competition(null,  "TEST_COMPETITION", TRACK_ID, new LocalDate());
        t.competitionResource.create(competition);
    }
}
