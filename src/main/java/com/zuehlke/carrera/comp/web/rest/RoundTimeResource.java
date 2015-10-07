package com.zuehlke.carrera.comp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.zuehlke.carrera.comp.domain.RoundTime;
import com.zuehlke.carrera.comp.nolog.RoundTimeService;
import com.zuehlke.carrera.relayapi.messages.RoundTimeMessage;
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
 * REST controller for managing Competition.
 */
@RestController
@RequestMapping(value = "api")
public class RoundTimeResource {

    private final Logger log = LoggerFactory.getLogger(RoundTimeResource.class);

    @Inject
    private RoundTimeService roundTimeService;

    /**
     */
    @RequestMapping(value = "/roundTimes",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @ResponseBody
    public ResponseEntity<Void> register(@Valid @RequestBody RoundTimeMessage message ) throws URISyntaxException {
        log.info("REST request to register Round Time: {}", message);
        if (message.getTrack() == null) {
            return ResponseEntity.badRequest().header("Failure", "A RoundTimeMessage must name the track").build();
        }

        Long id = roundTimeService.register ( message );
        return ResponseEntity.created(new URI("/api/roundTimes/" + id)).build();
    }


    /**
     * GET  /competitions -> get all the competitions.
     */
    @RequestMapping(value = "/roundTimes",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<RoundTime> getAll() {
        log.info("REST request to get all Competitions");
        return roundTimeService.findAll();
    }

    /**
     * GET  /roundTimes/:id -> get the "id" competition.
     */
    @RequestMapping(value = "/roundTimes/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<RoundTime> get(@PathVariable Long id) {
        log.info("REST request to get Competition : {}", id);
        return Optional.ofNullable(roundTimeService.findOne(id))
            .map(roundTime -> new ResponseEntity<>(
                roundTime,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /roundTime/:id -> delete the "id" competition.
     */
    @RequestMapping(value = "/roundTimes/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.info("REST request to delete Competition : {}", id);
        roundTimeService.delete(id);
    }
}
