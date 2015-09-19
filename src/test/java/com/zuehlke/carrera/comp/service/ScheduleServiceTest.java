package com.zuehlke.carrera.comp.service;

import com.zuehlke.carrera.comp.domain.Competition;
import com.zuehlke.carrera.comp.domain.FuriousRun;
import com.zuehlke.carrera.comp.domain.RacingSession;
import com.zuehlke.carrera.comp.domain.TeamRegistration;
import com.zuehlke.carrera.comp.repository.CompetitionRepository;
import com.zuehlke.carrera.comp.repository.FuriousRunRepository;
import com.zuehlke.carrera.comp.repository.RacingSessionRepository;
import com.zuehlke.carrera.comp.repository.TeamRegistrationRepository;
import com.zuehlke.carrera.relayapi.messages.RunRequest;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;
import java.util.stream.Collectors;

/**
 *  Tests the scheduleService without actual persistence layer
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ScheduleServiceTest.ScheduleServiceTestContext.class)
public class ScheduleServiceTest {

    @Autowired
    private ScheduleService service;

    @Autowired
    private FuriousRunRepository runRepo;

    @Autowired
    private TeamRegistrationRepository teamRepo;

    @Autowired
    private RacingSessionRepository sessionRepo;

    @Autowired
    private CompetitionRepository compRepo;

    @Before
    public void setup () {

        runRepo.deleteAll();
        teamRepo.deleteAll();
        sessionRepo.deleteAll();
        compRepo.deleteAll();

        Competition comp = new Competition(1L, "HSR2015", "Ninco1", new LocalDate());
        compRepo.save(comp);

        RacingSession session = new RacingSession(1L, "HSR2015", RacingSession.SessionType.Training,
                1, new LocalDateTime(), "sim02", "Hollywood");
        sessionRepo.save(session);

        LocalDateTime first = new LocalDateTime(2015,9,1,15,0,0,0);
        LocalDateTime second = first.plusMinutes(10);
        LocalDateTime third = second.plusMinutes(10);
        LocalDateTime fourth = third.plusMinutes(10);

        String protocol = "rabbit";
        String encoding = "jason";

        TeamRegistration wolfies  = new TeamRegistration(1L, "HSR2015", "wolfies", "access", protocol, encoding, first);
        teamRepo.save(wolfies);
        TeamRegistration harries  = new TeamRegistration(2L, "HSR2015", "harries", "access", protocol, encoding, second);
        teamRepo.save(harries);
        TeamRegistration steffies  = new TeamRegistration(3L, "HSR2015", "steffies", "access", protocol, encoding, third);
        teamRepo.save(steffies);
        TeamRegistration bernies  = new TeamRegistration(4L, "HSR2015", "bernies", "access", protocol, encoding, fourth);
        teamRepo.save(bernies);
    }

    @Test
    public void testFirstTrainingSchedule() {

        List<FuriousRun> schedule = service.findOrCreateForSession(1L);

        Assert.assertEquals(4, schedule.size());
    }

    @Test
    public void testStartRun () {
        List<FuriousRun> schedule = service.findOrCreateForSession(1L);

        service.startRun(schedule.get(0).getId());

        Assert.assertNotNull(results.get("request"));
    }

    public static Map<String, Object> results = new HashMap<>();

    public static class ScheduleServiceTestContext {

        private List<FuriousRun> run_db = new ArrayList<>();
        private List<TeamRegistration> team_db = new ArrayList<>();
        private List<RacingSession> session_db = new ArrayList<>();
        private List<Competition> comp_db = new ArrayList<>();

        @Bean
        ScheduleService scheduleService () {
            return new ScheduleService();
        }

        @Bean RelayApi relayApi() {
            return new RelayApi() {
                @Override
                public boolean startRun(RunRequest request, Logger logger) {
                    results.put("request", request);
                    return true;
                }

                @Override
                public boolean stopRun ( RunRequest request, Logger logger ) {
                    return true;
                }
            };
        }

        @Bean
        TeamRegistrationRepository teamRepo () {
            return new TeamRegistrationRepository() {
                @Override
                public List<TeamRegistration> findByCompetition(String comp) {
                    return team_db.stream().filter(x -> x.getCompetition().equals(comp))
                            .collect(Collectors.toCollection(ArrayList::new));
                }

                @Override
                public TeamRegistration findByTeam(String team) {
                    for ( TeamRegistration registration : team_db ) {
                        if ( registration.getTeam().equals(team)) {
                            return registration;
                        }
                    }
                    return null;
                }

                @Override
                public List<TeamRegistration> findAll() {
                    return team_db;
                }

                @Override
                public List<TeamRegistration> findAll(Sort sort) {
                    return null;
                }

                @Override
                public List<TeamRegistration> findAll(Iterable<Long> longs) {
                    return null;
                }

                @Override
                public <S extends TeamRegistration> List<S> save(Iterable<S> entities) {
                    return null;
                }

                @Override
                public void flush() {

                }

                @Override
                public <S extends TeamRegistration> S saveAndFlush(S entity) {
                    return null;
                }

                @Override
                public void deleteInBatch(Iterable<TeamRegistration> entities) {

                }

                @Override
                public void deleteAllInBatch() {

                }

                @Override
                public TeamRegistration getOne(Long aLong) {
                    return null;
                }

                @Override
                public Page<TeamRegistration> findAll(Pageable pageable) {
                    return null;
                }

                @Override
                public <S extends TeamRegistration> S save(S entity) {
                    if ( !exists(entity.getId())) {
                        team_db.add(entity);
                    }
                    return entity;
                }

                @Override
                public TeamRegistration findOne(Long aLong) {
                    for ( TeamRegistration x : team_db ) {
                        if ( x.getId().equals(aLong)) {
                            return x;
                        }
                    }
                    return null;
                }

                @Override
                public boolean exists(Long aLong) {
                    for ( TeamRegistration team : team_db) {
                        if ( team.getId().equals(aLong) ) {
                            return true;
                        }
                    }
                    return false;
                }

                @Override
                public long count() {
                    return 0;
                }

                @Override
                public void delete(Long aLong) {

                }

                @Override
                public void delete(TeamRegistration entity) {
                    team_db.remove(entity);
                }

                @Override
                public void delete(Iterable<? extends TeamRegistration> entities) {

                }

                @Override
                public void deleteAll() {
                    team_db.clear();
                }
            };
        }

        @Bean

        FuriousRunRepository runRepository () {
            return new FuriousRunRepository() {
                @Override
                public List<FuriousRun> findBySessionId(Long sessionId) {
                    return run_db.stream().filter(furiousRun -> furiousRun.getSessionId().equals(sessionId))
                            .collect(Collectors.toCollection(ArrayList::new));
                }

                @Override
                public List<FuriousRun> findAll() {
                    return run_db;
                }

                @Override
                public List<FuriousRun> findAll(Sort sort) {
                    return run_db;
                }

                @Override
                public List<FuriousRun> findAll(Iterable<Long> longs) {
                    return null;
                }

                @Override
                public <S extends FuriousRun> List<S> save(Iterable<S> entities) {
                    return null;
                }

                @Override
                public void flush() {

                }

                @Override
                public <S extends FuriousRun> S saveAndFlush(S entity) {
                    return null;
                }

                @Override
                public void deleteInBatch(Iterable<FuriousRun> entities) {

                }

                @Override
                public void deleteAllInBatch() {
                    run_db.clear();
                }

                @Override
                public FuriousRun getOne(Long aLong) {
                    return null;
                }

                @Override
                public Page<FuriousRun> findAll(Pageable pageable) {
                    return null;
                }

                @Override
                public <S extends FuriousRun> S save(S entity) {
                    if ( entity.getId() == null ) {
                        entity.setId(((long) (run_db.size() + 1)));
                    }
                    if (! exists ( entity.getId())) {
                        run_db.add(entity);
                    }
                    return entity;
                }

                @Override
                public FuriousRun findOne(Long aLong) {
                    for ( FuriousRun run : run_db) {
                        if ( run.getId().equals(aLong) ) {
                            return run;
                        }
                    }
                    return null;
                }

                @Override
                public boolean exists(Long aLong) {
                    for ( FuriousRun run : run_db) {
                        if ( run.getId().equals(aLong) ) {
                            return true;
                        }
                    }
                    return false;
                }

                @Override
                public long count() {
                    return run_db.size();
                }

                @Override
                public void delete(Long aLong) {
                    run_db.stream().filter(run -> run.getId().equals(aLong)).forEach(run_db::remove);
                }

                @Override
                public void delete(FuriousRun entity) {
                    run_db.remove(entity);
                }

                @Override
                public void delete(Iterable<? extends FuriousRun> entities) {
                }

                @Override
                public void deleteAll() {
                    run_db.clear();
                }
            };
        }

        @Bean
        CompetitionRepository compRepo () {
            return new CompetitionRepository() {
                @Override
                public Competition findByName(String compName) {
                    for ( Competition comp : comp_db ) {
                        if ( comp.getName().equals ( compName )) {
                            return comp;
                        }
                    }
                    return null;
                }

                @Override
                public List<Competition> findAll() {
                    return comp_db;
                }

                @Override
                public List<Competition> findAll(Sort sort) {
                    return null;
                }

                @Override
                public List<Competition> findAll(Iterable<Long> longs) {
                    return null;
                }

                @Override
                public <S extends Competition> List<S> save(Iterable<S> entities) {
                    return null;
                }

                @Override
                public void flush() {

                }

                @Override
                public <S extends Competition> S saveAndFlush(S entity) {
                    return null;
                }

                @Override
                public void deleteInBatch(Iterable<Competition> entities) {

                }

                @Override
                public void deleteAllInBatch() {
                    comp_db.clear();
                }

                @Override
                public Competition getOne(Long aLong) {
                    for ( Competition comp : comp_db ) {
                        if ( comp.getId().equals(aLong)) {
                            return comp;
                        }
                    }
                    return null;
                }

                @Override
                public Page<Competition> findAll(Pageable pageable) {
                    return null;
                }

                @Override
                public <S extends Competition> S save(S entity) {
                    if ( !comp_db.contains(entity)) {
                        comp_db.add ( entity);
                    }
                    return entity;
                }

                @Override
                public Competition findOne(Long aLong) {
                    return null;
                }

                @Override
                public boolean exists(Long aLong) {
                    for ( Competition comp : comp_db ) {
                        if ( comp.getId().equals(aLong) ) {
                            return true;
                        }
                    }
                    return false;
                }

                @Override
                public long count() {
                    return comp_db.size();
                }

                @Override
                public void delete(Long aLong) {
                    comp_db.stream().filter(x->x.getId().equals((aLong))).forEach(comp_db::remove);
                }

                @Override
                public void delete(Competition entity) {
                    comp_db.remove(entity);

                }

                @Override
                public void delete(Iterable<? extends Competition> entities) {
                }

                @Override
                public void deleteAll() {
                    comp_db.clear();
                }
            };
        }

        @Bean
        RacingSessionRepository sesionRepo () {
            return new RacingSessionRepository() {
                @Override
                public List<RacingSession> findByCompetition(String competitionName) {
                    return session_db.stream().filter(s->s.getCompetition().equals(competitionName))
                            .collect(Collectors.toCollection(ArrayList::new));
                }

                @Override
                public List<RacingSession> findAll() {
                    return session_db;
                }

                @Override
                public List<RacingSession> findAll(Sort sort) {
                    return null;
                }

                @Override
                public List<RacingSession> findAll(Iterable<Long> longs) {
                    return null;
                }

                @Override
                public <S extends RacingSession> List<S> save(Iterable<S> entities) {
                    return null;
                }

                @Override
                public void flush() {

                }

                @Override
                public <S extends RacingSession> S saveAndFlush(S entity) {
                    return null;
                }

                @Override
                public void deleteInBatch(Iterable<RacingSession> entities) {

                }

                @Override
                public void deleteAllInBatch() {
                    session_db.clear();
                }

                @Override
                public RacingSession getOne(Long aLong) {
                    return null;
                }

                @Override
                public Page<RacingSession> findAll(Pageable pageable) {
                    return null;
                }

                @Override
                public <S extends RacingSession> S save(S entity) {
                    if ( !exists( entity.getId())) {
                        session_db.add(entity);
                    }
                    return entity;
                }

                @Override
                public RacingSession findOne(Long aLong) {
                    for ( RacingSession session: session_db ) {
                        if (session.getId().equals ( aLong )) {
                            return session;
                        }
                    }
                    return null;
                }

                @Override
                public boolean exists(Long aLong) {
                    for ( RacingSession session: session_db ) {
                        if (session.getId().equals ( aLong )) {
                            return true;
                        }
                    }
                    return false;
                }

                @Override
                public long count() {
                    return session_db.size();
                }

                @Override
                public void delete(Long aLong) {
                    session_db.stream().filter(s->s.getId().equals(aLong)).forEach(session_db::remove);
                }

                @Override
                public void delete(RacingSession entity) {
                    session_db.remove(entity);
                }

                @Override
                public void delete(Iterable<? extends RacingSession> entities) {

                }

                @Override
                public void deleteAll() {
                    session_db.clear();
                }
            };
        }
    }
}
