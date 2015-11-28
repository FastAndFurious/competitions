package com.zuehlke.carrera.comp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.zuehlke.carrera.comp.domain.RacingSession;
import com.zuehlke.carrera.comp.repository.FuriousRunRepository;
import com.zuehlke.carrera.comp.repository.RacingSessionRepository;
import com.zuehlke.carrera.comp.service.ErroneousLifeSign;
import com.zuehlke.carrera.comp.service.ScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing RacingSession.
 */
@RestController
@RequestMapping("/api")
public class RacingSessionResource {

    private final Logger log = LoggerFactory.getLogger(RacingSessionResource.class);

    @Inject
    private RacingSessionRepository racingSessionRepository;

    @Inject
    private ScheduleService scheduleService;

    /**
     * POST  /racingSessions -> Create a new racingSession.
     */
    @RequestMapping(value = "/racingSessions",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody RacingSession racingSession) throws URISyntaxException {
        log.info("REST request to save RacingSession : {}", racingSession);
        if (racingSession.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new racingSession cannot already have an ID").build();
        }
        racingSessionRepository.save(racingSession);
        return ResponseEntity.created(new URI("/api/racingSessions/" + racingSession.getId())).build();
    }

    /**
     * PUT  /racingSessions -> Updates an existing racingSession.
     */
    @RequestMapping(value = "/racingSessions",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody RacingSession racingSession) throws URISyntaxException {
        log.info("REST request to update RacingSession : {}", racingSession);
        if (racingSession.getId() == null) {
            return create(racingSession);
        }
        racingSessionRepository.save(racingSession);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /racingSessions -> get all the racingSessions.
     */
    @RequestMapping(value = "/racingSessions",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<RacingSession> getAll() {
        log.debug("REST request to get all RacingSessions");
        return racingSessionRepository.findAll();
    }

    /**
     * GET  /racingSessions/:id -> get the "id" racingSession.
     */
    @RequestMapping(value = "/racingSessions/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<RacingSession> get(@PathVariable Long id) {
        log.debug("REST request to get RacingSession : {}", id);
        return Optional.ofNullable(racingSessionRepository.findOne(id))
            .map(racingSession -> new ResponseEntity<>(
                racingSession,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET  /racingSessions/:id -> get the "id" racingSession.
     */
    @RequestMapping(value = "/racingSessions/find",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<RacingSession> findByCompetition(@RequestParam String competitionName ) {
        log.debug("REST request to get RacingSessions for : {}", competitionName);
        return racingSessionRepository.findByCompetition(competitionName);
    }

    /**
     * DELETE  /racingSessions/:id -> delete the "id" racingSession.
     */
    @RequestMapping(value = "/racingSessions/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.info("REST request to delete RacingSession : {}", id);
        racingSessionRepository.delete(id);
    }


    /**
     * PUT  /racingSessions/newSchedule/:id -> create new schedule for this racingSession.
     */
    @RequestMapping(value = "/racingSessions/newSchedule/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> newSchedule(@PathVariable Long id) {
        log.info("REST request to create new Schedule for RacingSession : {}", id);
        try {
            scheduleService.createNewSchedule(id);
        } catch ( Exception e ) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }


    /**
     * GET  /racingSessions/newSchedule/:id -> create new schedule for this racingSession.
     */
    @RequestMapping(value = "/racingSessions/erroneousLifeSigns",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ErroneousLifeSign> findErroneousLifeSigns () {
        log.info("REST request to retrieve erroneous life Signs");
        return scheduleService.findErroneousLifeSigns();
    }

}
