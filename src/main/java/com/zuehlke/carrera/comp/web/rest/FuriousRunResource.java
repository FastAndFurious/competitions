package com.zuehlke.carrera.comp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.zuehlke.carrera.comp.domain.FuriousRun;
import com.zuehlke.carrera.comp.domain.FuriousRunDto;
import com.zuehlke.carrera.comp.domain.RunStatus;
import com.zuehlke.carrera.comp.domain.RacingSession;
import com.zuehlke.carrera.comp.nolog.StompCompetitionStatePublisher;
import com.zuehlke.carrera.comp.repository.FuriousRunRepository;
import com.zuehlke.carrera.comp.repository.RacingSessionRepository;
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
 * REST controller for managing Furiousruns.
 */
@RestController
@RequestMapping("/api")
public class FuriousRunResource {

    private final Logger log = LoggerFactory.getLogger(FuriousRunResource.class);

    @Inject
    private FuriousRunRepository repository;

    @Inject
    private ScheduleService scheduleService;

    @Inject
    private StompCompetitionStatePublisher publisher;

    @Inject
    private RacingSessionRepository sessionRepository;

    /**
     * POST  /furiousruns -> Create a new Run.
     */
    @RequestMapping(value = "/furiousruns",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody FuriousRun furiousRun) throws URISyntaxException {
        log.debug("REST request to save Run : {}", furiousRun);
        if (furiousRun.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new run cannot already have an ID").build();
        }
        repository.save(furiousRun);
        return ResponseEntity.created(new URI("/api/furiousruns/" + furiousRun.getId())).build();
    }

    /**
     * PUT  /run -> Updates an existing run.
     */
    @RequestMapping(value = "/furiousruns",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody FuriousRun furiousRun) throws URISyntaxException {
        log.debug("REST request to update run : {}", furiousRun);
        if (furiousRun.getId() == null) {
            return create(furiousRun);
        }
        repository.save(furiousRun);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /furiousruns -> get all the furiousruns.
     */
    @RequestMapping(value = "/furiousruns",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<FuriousRun> getAll() {
        log.debug("REST request to get all Runs");
        return repository.findAll();
    }

    /**
     * GET  /furiousruns/:sessionId -> get the scheduled runs for a particular session
     * will create a default schedule for the session if none exists yet.
     */
    @RequestMapping(value = "/furiousruns/schedule/{sessionId}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<FuriousRunDto> getSchedule( @PathVariable Long sessionId) {
        log.debug("REST request to get all Runs for session ");

        RacingSession session = sessionRepository.findOne(sessionId);
        String comp = session.getCompetition();
        List<FuriousRun> runs = repository.findByStatus(RunStatus.ONGOING);
        if ( runs.size() > 0 ) {
            runs.stream().filter(run -> run.getSessionId().equals(sessionId)).forEach(run -> {
                publishForRunId(run.getId());
            });
        } else {
            publisher.publishStatus(comp, sessionId, null);
        }
        return scheduleService.findOrCreateForSession(sessionId);
    }

    /**
     * PUT /furiousRuns/start/:id -> start the race with id :id
     */
    @RequestMapping(value = "/furiousruns/start/{id}",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    @Timed
    public ResponseEntity<String> startRun( @PathVariable Long id) {
        log.info("REST request to start run {}", id);
        ServiceResult result = scheduleService.startRun(id);
        if ( result.getStatus() == ServiceResult.Status.OK ) {
            publishForRunId ( id );
            return ResponseEntity.ok("Success");
        } else {
            return ResponseEntity.badRequest().body(result.getMessage());
        }

    }

    private void publishForRunId(Long id) {
        FuriousRun run = repository.findOne(id);
        if ( run == null ) return;
        RacingSession session = sessionRepository.findOne(run.getSessionId());
        publisher.publishStatus(session.getCompetition(), run.getSessionId(), run.getTeam());
    }

    /**
     * GET /furiousRuns/start/:id -> stop the race with id :id
     */
    @RequestMapping(value = "/furiousruns/stop/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> stopRun( @PathVariable Long id) {
        log.info("REST request to stop run {}", id);
        scheduleService.stopRun(id);
        return ResponseEntity.ok().build();
    }

    /**
     * POST /runFinished -> stop the race with id :id
     */
    @RequestMapping(value = "/runFinished",
            method = RequestMethod.POST)
    @Timed
    public ResponseEntity<Void> stopRun( @RequestBody String trackId) {
        log.info("REST request to stop run {}", trackId);
        scheduleService.stopRunOnTrack(trackId);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /furiousruns/:id -> get the "id" furiousrun.
     */
    @RequestMapping(value = "/furiousruns/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<FuriousRun> get(@PathVariable Long id) {
        log.debug("REST request to get Run : {}", id);
        return Optional.ofNullable(repository.findOne(id))
            .map(furiousRun -> new ResponseEntity<>(
                    furiousRun,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /furiousruns/:id -> delete the "id" furiousrun.
     */
    @RequestMapping(value = "/furiousruns/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Run : {}", id);
        repository.delete(id);
    }


}
