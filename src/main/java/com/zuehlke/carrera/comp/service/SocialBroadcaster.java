package com.zuehlke.carrera.comp.service;

import com.zuehlke.carrera.comp.domain.ApplicationNotification;
import com.zuehlke.carrera.comp.domain.RunMissedNotification;
import com.zuehlke.carrera.comp.domain.RunPerformedNotification;
import com.zuehlke.carrera.comp.domain.ScheduledRun;

import java.util.List;

public interface SocialBroadcaster {
    void broadCast(ApplicationNotification application, List<ScheduledRun> schedule);

    void broadCast(RunMissedNotification missed, List<ScheduledRun> schedule);

    void broadCast(RunPerformedNotification performed, List<ScheduledRun> schedule);
}
