package com.zuehlke.carrera.comp.repository;

import com.zuehlke.carrera.comp.domain.FuriousRun;
import com.zuehlke.carrera.comp.domain.RoundResult;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 *  repository to provide more complex db queries without orm
 */
@Component
public class NativeSqlSpecialRepo implements SpecialRepo {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private FuriousRunRepository runRepository;

    @Inject
    private RoundTimeRepository roundRepository;

    @Override
    public FuriousRun findOngoingRunOnTrack(String track) {

        String sql = "select r.id from t_racingsession s, furious_runs r " +
                "where s.id = r.sessionid " +
                "    and s.track_id = :track and r.status IN ('ONGOING', 'SUSPENDED');";

        Query query = em.createNativeQuery(sql);

        query.setParameter("track", track);

        List result = query.getResultList();

        if ( result.size() > 1 ) {
            throw new IllegalStateException();
        }

        BigInteger id = (BigInteger) result.get(0);

        return runRepository.findOne(id.longValue());
    }


    /**
     *
     * @param comp the name of the competition to search
     * @param sessionId the sessionId of the session to search
     * @param team the team the results of which to search for
     * @param noResults the max number of round result to return
     * @return the list of round results of the team within the given session, empty list if team is not enroled.
     */
    @Override
    public List<RoundResult> findLatestRoundTimes(String comp, Long sessionId, String team, int noResults) {

        List<RoundResult> roundTimes = new ArrayList<>();

        // find the run for the team and then query
        runRepository.findBySessionId(sessionId).stream().filter((run) -> run.getTeam().equals(team)).forEach(
                (run) -> {
                    String sql = "select id, timestamp, duration, team, track, run_id " +
                            "from round_times r where team = :team and run_id = :run " +
                            "order by timestamp desc limit 0,:noResults";

                    Query query = em.createNativeQuery(sql);

                    query.setParameter("team", team);
                    query.setParameter("run", run.getId());
                    query.setParameter("noResults", noResults);

                    List result = query.getResultList();

                    for ( Object obj : result ) {
                        Object[] row = (Object[]) obj;
                        long duration = ((BigInteger) row[2]).longValue();
                        RoundResult roundResult = new RoundResult(team, sessionId, comp, 0, duration );
                        roundTimes.add(roundResult);
                    }
                }
        );


        return roundTimes;
    }

    @Override
    public List<RoundResult> findBestRoundTimes(String comp, Long sessionId) {

        String sql = "select min(t.duration), t.team, r.id as \"raceid\", s.id as \"sessionid\", c.name as \"compid\" from  " +
                "round_times t, furious_runs r, t_racingsession s, competition c " +
                "where t.run_id = r.id and" +
                "    r.sessionid = s.id and" +
                "    c.name = s.competition and" +
                "    s.competition = :comp and" +
                "    r.status != 'DISQUALIFIED' and" +
                "    s.id = :sessionId " +
                "    group by t.team" +
                "    order by min(t.duration) asc;";

        Query query = em.createNativeQuery(sql);

        query.setParameter("comp", comp);
        query.setParameter("sessionId", sessionId);

        List result = query.getResultList();

        List<RoundResult> roundTimes = new ArrayList<>();

        int rownum = 0;
        for ( Object obj : result ) {
                Object[] row = (Object[]) obj;
            long duration = ((BigInteger) row[0]).longValue();
            String team = (String) row[1];
            String compName = (String) row[4];
            RoundResult roundResult = new RoundResult(team, sessionId, compName, ++rownum, duration );
            roundTimes.add(roundResult);
        }

        return roundTimes;
    }

    @Override
    public List<RoundResult> findBestRoundTimes_former(String comp, Long sessionId) {

        String sql = "select min(t.duration), t.team, r.id as \"raceid\", s.id as \"sessionid\", c.name as \"compid\", " +
                "@rownum \\:= @rownum + 1 as \"rownum\" from " +
                "competition.round_times t, furious_runs r, t_racingsession s, competition c, (SELECT @rownum\\:=0) rownum " +
                "where t.run_id = r.id and" +
                "    r.sessionid = s.id and" +
                "    c.name = s.competition and" +
                "    s.competition = :comp and" +
                "    r.status != 'DISQUALIFIED' and" +
                "    s.id = :sessionId " +
                "    group by t.team" +
                "    order by min(t.duration) asc;";

        Query query = em.createNativeQuery(sql);

        query.setParameter("comp", comp);
        query.setParameter("sessionId", sessionId);

        List result = query.getResultList();

        List<RoundResult> roundTimes = new ArrayList<>();

        for ( Object obj : result ) {
            Object[] row = (Object[]) obj;
            long duration = ((BigInteger) row[0]).longValue();
            String team = (String) row[1];
            String compName = (String) row[4];
            int rownum = 0;
            if ( row[5] instanceof BigInteger ) { // this happens on the ubuntu database
                rownum = ((BigInteger) row[5]).intValue();
            } else {
                rownum = ((Double) row[5]).intValue();
            }
            RoundResult roundResult = new RoundResult(team, sessionId, compName, rownum, duration );
            roundTimes.add(roundResult);
        }

        return roundTimes;
    }
}
