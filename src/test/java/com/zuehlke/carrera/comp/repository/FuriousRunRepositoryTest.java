package com.zuehlke.carrera.comp.repository;

import com.zuehlke.carrera.comp.CompetitionManagerApp;
import com.zuehlke.carrera.comp.domain.FuriousRun;
import org.joda.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 *  Simple save-and-retrieve tests for FuriousRuns
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CompetitionManagerApp.class)
@WebAppConfiguration
@IntegrationTest
@Transactional
public class FuriousRunRepositoryTest {

    @Autowired
    private FuriousRunRepository repo;

    @Test
    public void testSaveAndRetrieve() {

        FuriousRun run = new FuriousRun("koba", new LocalDateTime(), new LocalDateTime(), 1L, 1L, FuriousRun.Status.SCHEDULED);

        repo.save ( run );

         run = repo.findOne(run.getId());

        Assert.assertEquals("koba", run.getTeam());
    }


}
