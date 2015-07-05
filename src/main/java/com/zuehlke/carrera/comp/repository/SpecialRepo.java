package com.zuehlke.carrera.comp.repository;

import com.zuehlke.carrera.comp.domain.FuriousRun;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigInteger;
import java.util.List;

/**
 *  repository to provide smart sql without orm
 */
@Component
public class SpecialRepo {

    @PersistenceContext
    private EntityManager em;

    @Inject
    private FuriousRunRepository runRepository;

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
}
