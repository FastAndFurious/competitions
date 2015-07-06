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
public class SpecialRepo {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private FuriousRunRepository runRepository;

    @Inject
    private RoundTimeRepository roundRepository;

    public FuriousRun findOngoingRunOnTrack ( String track ) {

        String sql = "select r.id from t_racingsession s, furious_runs r " +
                "where s.id = r.sessionid " +
                "    and s.track_id = :track and r.status = 'ONGOING';";

        Query query = em.createNativeQuery(sql);

        query.setParameter("track", track);

        List result = query.getResultList();

        if ( result.size() > 1 ) {
            throw new IllegalStateException();
        }

        BigInteger id = (BigInteger) result.get(0);

        return runRepository.findOne(id.longValue());
    }

    public List<RoundResult> findBestRoundTimes ( String comp, Long sessionId ) {

        String sql = "select min(t.duration), t.team, r.id as \"raceid\", s.id as \"sessionid\", c.name as \"compid\" from " +
                "competition.round_times t, furious_runs r, t_racingsession s, competition c " +
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
            RoundResult roundResult = new RoundResult(team, sessionId, compName, duration );
            roundTimes.add(roundResult);
        }

        return roundTimes;
    }
}
