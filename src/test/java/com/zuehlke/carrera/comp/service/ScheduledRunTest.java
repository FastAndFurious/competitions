package com.zuehlke.carrera.comp.service;

import com.zuehlke.carrera.comp.domain.GuaranteedPosition;
import com.zuehlke.carrera.comp.domain.ScheduledRun;
import com.zuehlke.carrera.comp.domain.TrainingApplication;
import com.zuehlke.carrera.comp.integrationtests.ScheduleUtils;
import org.joda.time.LocalDateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *  test the comparison of two scheduled runs -> comparison determines the order of starts
 */
public class ScheduledRunTest {


    private List<ScheduledRun> runs = new ArrayList<>();
    ScheduledRun first = new ScheduledRun("first", 1L);
    ScheduledRun second = new ScheduledRun("second", 1L);
    ScheduledRun third = new ScheduledRun("third", 1L);
    ScheduledRun fourth = new ScheduledRun("fourth", 1L);

    @Before
    public void clearRuns () {
        runs = new ArrayList<>();
        runs.add(fourth);
        runs.add(third);
        runs.add(second);
        runs.add(first);
    }

    @Test
    public void test_less_opportunities_come_first () {

        first.setNumberOfMissedRuns(2);
        first.setNumberOfPerformedRuns(1);

        second.setNumberOfMissedRuns(4);
        second.setNumberOfPerformedRuns(0);

        third.setNumberOfMissedRuns(1);
        third.setNumberOfPerformedRuns(3);

        fourth.setNumberOfMissedRuns(0);
        fourth.setNumberOfPerformedRuns(4);

        assertOrder(4, "Less opportunities come first");
    }


    @Test
    public void test_equal_opportunities_less_performed_comes_first() {

        first.setNumberOfMissedRuns(2);
        first.setNumberOfPerformedRuns(1);
        second.setNumberOfMissedRuns(1);
        second.setNumberOfPerformedRuns(2);

        third.setNumberOfPerformedRuns(5);
        fourth.setNumberOfPerformedRuns(5);

        assertOrder(2, "Equal opportunities, less performed comes first.");
    }

    @Test
    public void test_equal_performed_applied_comes_first () {

        first.setNumberOfMissedRuns(4);
        first.setNumberOfPerformedRuns(1);
        first.setApplication(new TrainingApplication("first", 1L));
        second.setNumberOfMissedRuns(4);
        second.setNumberOfPerformedRuns(1);
        third.setNumberOfPerformedRuns(5);
        fourth.setNumberOfPerformedRuns(5);
        assertOrder (2, "All equal, applied comes first");

    }

    @Test
    public void test_equal_performed_earlier_applied_comes_first () {

        LocalDateTime now = LocalDateTime.now();

        first.setNumberOfMissedRuns(4);
        first.setNumberOfPerformedRuns(1);
        first.setApplication(new TrainingApplication("first", 1L, now.minusMinutes(1)));
        second.setNumberOfMissedRuns(4);
        second.setNumberOfPerformedRuns(1);
        first.setApplication(new TrainingApplication("first", 1L, now));
        third.setNumberOfPerformedRuns(5);
        fourth.setNumberOfPerformedRuns(5);

        assertOrder(2, "All Equal, applied earlier comes first");
    }

    @Test
    public void test_guaranteed_comes_first () {
        first.setNumberOfPerformedRuns(3);
        first.setGuaranteedPosition(GuaranteedPosition.FIRST);
        second.setNumberOfPerformedRuns(2);
        second.setGuaranteedPosition(GuaranteedPosition.SECOND);
        third.setGuaranteedPosition(GuaranteedPosition.NONE);
        third.setNumberOfPerformedRuns(1);
        fourth.setNumberOfPerformedRuns(5);

        assertOrder(3, "guarantees determine the order");
    }

    private void assertOrder ( int numberOfPositionsToTest, String title ) {
        Collections.sort(runs);
        System.out.println(title);
        ScheduleUtils.println(runs, numberOfPositionsToTest);
        if ( --numberOfPositionsToTest < 0 ) return;
        Assert.assertTrue(runs.get(0).getTeamId().contains("first"));
        if ( --numberOfPositionsToTest < 0 ) return;
        Assert.assertTrue(runs.get(1).getTeamId().contains("second"));
        if ( --numberOfPositionsToTest < 0 ) return;
        Assert.assertTrue(runs.get(2).getTeamId().contains("third"));
        if ( --numberOfPositionsToTest < 0 ) return;
        Assert.assertTrue(runs.get(3).getTeamId().contains("fourth"));
    }


}
