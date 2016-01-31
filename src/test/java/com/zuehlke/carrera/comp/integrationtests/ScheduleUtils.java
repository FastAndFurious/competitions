package com.zuehlke.carrera.comp.integrationtests;

import com.zuehlke.carrera.comp.domain.ScheduledRun;
import com.zuehlke.carrera.comp.domain.TrainingApplication;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

/**
 *  Helper class for operations on Lists of ScheduledRuns
 */
public class ScheduleUtils {

    public static void main ( String [] args ) {
        List<ScheduledRun> runs = new ArrayList<>();
        ScheduledRun run = new ScheduledRun("team", 1L);
        run.setScheduledStart(LocalDateTime.now());
        TrainingApplication application = new TrainingApplication("team", 1L, LocalDateTime.now());
        application.setNumberOfMissedRuns(10);
        application.setNumberOfPerformedRuns(2);
        run.setApplication(application);
        runs.add(run);
        println(runs, 1);
    }

    public static void println(List<ScheduledRun> runs, int numberOfRunsToShow ) {

        System.out.println(
                    " Team    | Pos  | Guara  | Opps  | Perfd | Appd  | Estd");
        System.out.println("---------|------|--------|-------|-------|-------|-------");

        for ( int index = 0; index < Math.min(runs.size(), numberOfRunsToShow); index ++ ) {
            ScheduledRun r = runs.get(index);
            System.out.format(
                    " %7s | %1d    | %6s | %2d    | %2d    | %5s | %5s\n",
                    r.getTeamId().substring(0,Math.min(7, r.getTeamId().length())),
                    index + 1,
                    r.getGuaranteedPosition(),
                    r.getNumberOfMissedRuns() + r.getNumberOfPerformedRuns(),
                    r.getNumberOfPerformedRuns(),
                    ( r.getApplication() == null ? "--:--" :
                            ( r.getApplication().getApplicationTime() == null ? "--:--" :
                        r.getApplication().getApplicationTime().toString("HH:mm"))),
                    ( r.getScheduledStart() == null ? "--:--" :
                        r.getScheduledStart().toString("HH:mm")));
        }
        System.out.println("---------------------------------------------------------");
        System.out.println();
    }
}
