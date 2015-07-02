package com.zuehlke.carrera.comp.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.zuehlke.carrera.comp.domain.Competition;
import com.zuehlke.carrera.comp.repository.CompetitionRepository;
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
@RequestMapping("/api")
public class CompetitionResource {

    private final Logger log = LoggerFactory.getLogger(CompetitionResource.class);

    @Inject
    private CompetitionRepository competitionRepository;

    /**
     * POST  /competitions -> Create a new competition.
     */
    @RequestMapping(value = "/competitions",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody Competition competition) throws URISyntaxException {
        log.debug("REST request to save Competition : {}", competition);
        if (competition.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new competition cannot already have an ID").build();
        }
        competitionRepository.save(competition);
        return ResponseEntity.created(new URI("/api/competitions/" + competition.getId())).build();
    }

    /**
     * PUT  /competitions -> Updates an existing competition.
     */
    @RequestMapping(value = "/competitions",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody Competition competition) throws URISyntaxException {
        log.debug("REST request to update Competition : {}", competition);
        if (competition.getId() == null) {
            return create(competition);
        }
        competitionRepository.save(competition);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /competitions -> get all the competitions.
     */
    @RequestMapping(value = "/competitions",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Competition> getAll() {
        log.debug("REST request to get all Competitions");
        return competitionRepository.findAll();
    }

    /**
     * GET  /competitions/:id -> get the "id" competition.
     */
    @RequestMapping(value = "/competitions/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Competition> get(@PathVariable Long id) {
        log.debug("REST request to get Competition : {}", id);
        return Optional.ofNullable(competitionRepository.findOne(id))
            .map(competition -> new ResponseEntity<>(
                competition,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /competitions/:id -> delete the "id" competition.
     */
    @RequestMapping(value = "/competitions/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) {
        log.debug("REST request to delete Competition : {}", id);
        competitionRepository.delete(id);
    }
}
