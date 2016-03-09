package com.zuehlke.carrera.comp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.zuehlke.carrera.comp.domain.ApplicationNotification;
import com.zuehlke.carrera.comp.domain.RunMissedNotification;
import com.zuehlke.carrera.comp.domain.RunPerformedNotification;
import com.zuehlke.carrera.comp.domain.ScheduledRun;
import com.zuehlke.carrera.comp.service.FreeTrainingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 *  RESTful Services to manage the automatic schedule for free trainings
 */

@RestController
@RequestMapping("/api/trainingschedule")
public class FreeTrainingResource {

    private static final Logger logger = LoggerFactory.getLogger(FreeTrainingResource.class);

    @Autowired
    private FreeTrainingService freeTrainingService;

    /**
     * GET  /schedule/
     * @param sessionId the id of the session that the schedule is for
     * @return a sorted list of scheduled runs, one for each team
     */
    @RequestMapping(value = "/schedule/{sessionId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ScheduledRun> getSchedule(@PathVariable Long sessionId) {
        logger.info("REST request to get all Scheduled Runs");
        return freeTrainingService.getSchedule (sessionId);
    }


    /**
     * POST  /freetrainings/applications -> Apply for a training run
     */
    @RequestMapping(value = "/applications",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> applyForTraining(@Valid @RequestBody ApplicationNotification application) throws URISyntaxException {
        logger.info("REST request from {} to apply for training.", application.getTeamName());

        try {
            freeTrainingService.applyForTraining(application);
            return ResponseEntity.created(new URI("/api/freetrainings/applications/" )).build();
        } catch ( IllegalStateException ise ) {
            return ResponseEntity.badRequest().header("Failure", ise.getMessage()).build();
        }
    }

    @RequestMapping(value = "/performed",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> registerPerformedRun(@Valid @RequestBody RunPerformedNotification notification ) throws URISyntaxException{
        logger.info("REST request to register {}'s performed run.", notification.getTeamName());

        freeTrainingService.registerPerformedRun ( notification );

        return ResponseEntity.created(new URI("/api/trainingschedule/performed/" )).build();
    }

    @RequestMapping(value = "/missed",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> registerMissedRun(@Valid @RequestBody RunMissedNotification notification ) throws URISyntaxException  {
        logger.info("REST request to register {}'s performed run.", notification.getTeamName());

        freeTrainingService.registerMissedRun ( notification );

        return ResponseEntity.created(new URI("/api/trainingschedule/missed/" )).build();

    }
}
