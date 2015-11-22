package com.zuehlke.carrera.comp.repository;

import com.zuehlke.carrera.comp.domain.FuriousRun;
import com.zuehlke.carrera.comp.domain.RoundResult;

import java.util.List;

/**
 * Created by wgiersche on 22/11/15.
 */
public interface SpecialRepo {
    FuriousRun findOngoingRunOnTrack(String track);

    List<RoundResult> findLatestRoundTimes(String comp, Long sessionId, String team, int noResults);

    List<RoundResult> findBestRoundTimes(String comp, Long sessionId);

    List<RoundResult> findBestRoundTimes_former(String comp, Long sessionId);
}
