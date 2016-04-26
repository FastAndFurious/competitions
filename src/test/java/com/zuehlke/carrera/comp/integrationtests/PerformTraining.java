package com.zuehlke.carrera.comp.integrationtests;


import com.zuehlke.carrera.comp.domain.FuriousRunDto;
import com.zuehlke.carrera.comp.domain.RacingSession;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * A sub-set of the test scenario. All training handling is tested here.
 */
@Component
public class PerformTraining {

    @Autowired
    TestSystem t;

    public void create_training_schedule() {

        List<RacingSession> allSessions = t.sessionResource.getAll();
        allSessions.stream().filter((s)->s.getType()== RacingSession.SessionType.Training).forEach((s)->{
            List<FuriousRunDto> runs = t.runResource.getSchedule( s.getId() );
            Assert.assertEquals(4, runs.size());
            Assert.assertEquals ( "wolfies", runs.get(3).getTeam());
            Assert.assertEquals ( "harries", runs.get(2).getTeam());
            Assert.assertEquals ( "steffies", runs.get(1).getTeam());
            Assert.assertEquals ( "bernies", runs.get(0).getTeam());
        });
        Assert.assertTrue ( t.twitterService.getMessages().size() == 0 );

    }


}
