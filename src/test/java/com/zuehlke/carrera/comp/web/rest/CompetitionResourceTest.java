package com.zuehlke.carrera.comp.web.rest;

import com.zuehlke.carrera.comp.CompetitionManagerApp;
import com.zuehlke.carrera.comp.domain.Competition;
import com.zuehlke.carrera.comp.repository.CompetitionRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.joda.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the CompetitionResource REST controller.
 *
 * @see CompetitionResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CompetitionManagerApp.class)
@WebAppConfiguration
@IntegrationTest
@Transactional
public class CompetitionResourceTest {

    private static final String DEFAULT_NAME = "SAMPLE_TEXT";
    private static final String UPDATED_NAME = "UPDATED_TEXT";
    private static final String DEFAULT_TRACK_ID = "SAMPLE_TEXT";
    private static final String UPDATED_TRACK_ID = "UPDATED_TEXT";

    private static final LocalDate DEFAULT_START_DATE = new LocalDate(0L);
    private static final LocalDate UPDATED_START_DATE = new LocalDate();

    private static final Integer DEFAULT_BEST_SEQUENCE = 0;
    private static final Integer UPDATED_BEST_SEQUENCE = 1;

    private static final Integer DEFAULT_BEST_SET = 0;
    private static final Integer UPDATED_BEST_SET = 1;
    private static final String DEFAULT_FIRST_PRIORITY = "SAMPLE_TEXT";
    private static final String UPDATED_FIRST_PRIORITY = "UPDATED_TEXT";
    private static final String DEFAULT_SECOND_PRIORITY = "SAMPLE_TEXT";
    private static final String UPDATED_SECOND_PRIORITY = "UPDATED_TEXT";

    private static final Integer DEFAULT_RUN_DURATION = 0;
    private static final Integer UPDATED_RUN_DURATION = 1;

    private static final Integer DEFAULT_NUMBER_OF_SESSIONS = 0;
    private static final Integer UPDATED_NUMBER_OF_SESSIONS = 1;

    @Inject
    private CompetitionRepository competitionRepository;

    private MockMvc restCompetitionMockMvc;

    private Competition competition;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CompetitionResource competitionResource = new CompetitionResource();
        ReflectionTestUtils.setField(competitionResource, "competitionRepository", competitionRepository);
        this.restCompetitionMockMvc = MockMvcBuilders.standaloneSetup(competitionResource).build();
    }

    @Before
    public void initTest() {
        competition = new Competition();
        competition.setName(DEFAULT_NAME);
        competition.setTrackId(DEFAULT_TRACK_ID);
        competition.setStartDate(DEFAULT_START_DATE);
        competition.setBestSequence(DEFAULT_BEST_SEQUENCE);
        competition.setBestSet(DEFAULT_BEST_SET);
        competition.setFirstPriority(DEFAULT_FIRST_PRIORITY);
        competition.setSecondPriority(DEFAULT_SECOND_PRIORITY);
    }

    @Test
    @Transactional
    public void createCompetition() throws Exception {
        int databaseSizeBeforeCreate = competitionRepository.findAll().size();

        // Create the Competition
        restCompetitionMockMvc.perform(post("/api/competitions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(competition)))
                .andExpect(status().isCreated());

        // Validate the Competition in the database
        List<Competition> competitions = competitionRepository.findAll();
        assertThat(competitions).hasSize(databaseSizeBeforeCreate + 1);
        Competition testCompetition = competitions.get(competitions.size() - 1);
        assertThat(testCompetition.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCompetition.getTrackId()).isEqualTo(DEFAULT_TRACK_ID);
        assertThat(testCompetition.getStartDate()).isEqualTo(DEFAULT_START_DATE);
        assertThat(testCompetition.getBestSequence()).isEqualTo(DEFAULT_BEST_SEQUENCE);
        assertThat(testCompetition.getBestSet()).isEqualTo(DEFAULT_BEST_SET);
        assertThat(testCompetition.getFirstPriority()).isEqualTo(DEFAULT_FIRST_PRIORITY);
        assertThat(testCompetition.getSecondPriority()).isEqualTo(DEFAULT_SECOND_PRIORITY);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(competitionRepository.findAll()).hasSize(0);
        // set the field null
        competition.setName(null);

        // Create the Competition, which fails.
        restCompetitionMockMvc.perform(post("/api/competitions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(competition)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<Competition> competitions = competitionRepository.findAll();
        assertThat(competitions).hasSize(0);
    }

    @Test
    @Transactional
    public void checkTrackIdIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(competitionRepository.findAll()).hasSize(0);
        // set the field null
        competition.setTrackId(null);

        // Create the Competition, which fails.
        restCompetitionMockMvc.perform(post("/api/competitions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(competition)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<Competition> competitions = competitionRepository.findAll();
        assertThat(competitions).hasSize(0);
    }

    @Test
    @Transactional
    public void getAllCompetitions() throws Exception {
        // Initialize the database
        competitionRepository.saveAndFlush(competition);

        // Get all the competitions
        restCompetitionMockMvc.perform(get("/api/competitions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(competition.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].trackId").value(hasItem(DEFAULT_TRACK_ID.toString())))
                .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
                .andExpect(jsonPath("$.[*].bestSequence").value(hasItem(DEFAULT_BEST_SEQUENCE)))
                .andExpect(jsonPath("$.[*].bestSet").value(hasItem(DEFAULT_BEST_SET)))
                .andExpect(jsonPath("$.[*].firstPriority").value(hasItem(DEFAULT_FIRST_PRIORITY.toString())))
                .andExpect(jsonPath("$.[*].secondPriority").value(hasItem(DEFAULT_SECOND_PRIORITY.toString())));
    }

    @Test
    @Transactional
    public void getCompetition() throws Exception {
        // Initialize the database
        competitionRepository.saveAndFlush(competition);

        // Get the competition
        restCompetitionMockMvc.perform(get("/api/competitions/{id}", competition.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(competition.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.trackId").value(DEFAULT_TRACK_ID.toString()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.bestSequence").value(DEFAULT_BEST_SEQUENCE))
            .andExpect(jsonPath("$.bestSet").value(DEFAULT_BEST_SET))
            .andExpect(jsonPath("$.firstPriority").value(DEFAULT_FIRST_PRIORITY.toString()))
            .andExpect(jsonPath("$.secondPriority").value(DEFAULT_SECOND_PRIORITY.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingCompetition() throws Exception {
        // Get the competition
        restCompetitionMockMvc.perform(get("/api/competitions/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCompetition() throws Exception {
        // Initialize the database
        competitionRepository.saveAndFlush(competition);

		int databaseSizeBeforeUpdate = competitionRepository.findAll().size();

        // Update the competition
        competition.setName(UPDATED_NAME);
        competition.setTrackId(UPDATED_TRACK_ID);
        competition.setStartDate(UPDATED_START_DATE);
        competition.setBestSequence(UPDATED_BEST_SEQUENCE);
        competition.setBestSet(UPDATED_BEST_SET);
        competition.setFirstPriority(UPDATED_FIRST_PRIORITY);
        competition.setSecondPriority(UPDATED_SECOND_PRIORITY);
        restCompetitionMockMvc.perform(put("/api/competitions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(competition)))
                .andExpect(status().isOk());

        // Validate the Competition in the database
        List<Competition> competitions = competitionRepository.findAll();
        assertThat(competitions).hasSize(databaseSizeBeforeUpdate);
        Competition testCompetition = competitions.get(competitions.size() - 1);
        assertThat(testCompetition.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCompetition.getTrackId()).isEqualTo(UPDATED_TRACK_ID);
        assertThat(testCompetition.getStartDate()).isEqualTo(UPDATED_START_DATE);
        assertThat(testCompetition.getBestSequence()).isEqualTo(UPDATED_BEST_SEQUENCE);
        assertThat(testCompetition.getBestSet()).isEqualTo(UPDATED_BEST_SET);
        assertThat(testCompetition.getFirstPriority()).isEqualTo(UPDATED_FIRST_PRIORITY);
        assertThat(testCompetition.getSecondPriority()).isEqualTo(UPDATED_SECOND_PRIORITY);
    }

    @Test
    @Transactional
    public void deleteCompetition() throws Exception {
        // Initialize the database
        competitionRepository.saveAndFlush(competition);

		int databaseSizeBeforeDelete = competitionRepository.findAll().size();

        // Get the competition
        restCompetitionMockMvc.perform(delete("/api/competitions/{id}", competition.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Competition> competitions = competitionRepository.findAll();
        assertThat(competitions).hasSize(databaseSizeBeforeDelete - 1);
    }
}
