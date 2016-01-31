package com.zuehlke.carrera.comp.web.rest;

import com.zuehlke.carrera.comp.CompetitionManagerApp;
import com.zuehlke.carrera.comp.domain.RacingSession;
import com.zuehlke.carrera.comp.repository.RacingSessionRepository;

import org.joda.time.LocalDateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the RacingSessionResource REST controller.
 *
 * @see RacingSessionResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CompetitionManagerApp.class)
@WebAppConfiguration
@IntegrationTest
@Transactional
public class RacingSessionResourceTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss");

    private static final String DEFAULT_COMPETITION = "SAMPLE_TEXT";
    private static final String UPDATED_COMPETITION = "UPDATED_TEXT";
    private static final RacingSession.SessionType DEFAULT_TYPE = RacingSession.SessionType.Training;
    private static final RacingSession.SessionType UPDATED_TYPE = RacingSession.SessionType.Competition;

    private static final Integer DEFAULT_SEQ_NO = 0;
    private static final Integer UPDATED_SEQ_NO = 1;

    private static final LocalDateTime DEFAULT_PLANNED_START_TIME = new LocalDateTime(0L);
    private static final LocalDateTime UPDATED_PLANNED_START_TIME = new LocalDateTime()
            .withSecondOfMinute(0).withMillisOfSecond(0);
    private static final String DEFAULT_PLANNED_START_TIME_STR = dateTimeFormatter.print(DEFAULT_PLANNED_START_TIME);
    private static final String DEFAULT_TRACK_LAYOUT = "SAMPLE_TEXT";
    private static final String UPDATED_TRACK_LAYOUT = "UPDATED_TEXT";

    @Inject
    private RacingSessionRepository racingSessionRepository;

    private MockMvc restRacingSessionMockMvc;

    private RacingSession racingSession;

    @PostConstruct
    public void setup() {

        MockitoAnnotations.initMocks(this);
        RacingSessionResource racingSessionResource = new RacingSessionResource();
        ReflectionTestUtils.setField(racingSessionResource, "racingSessionRepository", racingSessionRepository);
        this.restRacingSessionMockMvc = MockMvcBuilders.standaloneSetup(racingSessionResource).build();
    }

    @Before
    public void initTest() {
        racingSession = new RacingSession();
        racingSession.setCompetition(DEFAULT_COMPETITION);
        racingSession.setType(DEFAULT_TYPE);
        racingSession.setSeqNo(DEFAULT_SEQ_NO);
        racingSession.setPlannedStartTime(DEFAULT_PLANNED_START_TIME);
        racingSession.setTrackLayout(DEFAULT_TRACK_LAYOUT);
        racingSession.setTrackId("SAMPLE_TEXT");
    }

    @Test
    @Transactional
    public void createRacingSession() throws Exception {
        int databaseSizeBeforeCreate = racingSessionRepository.findAll().size();

        String json = new String(TestUtil.convertObjectToJsonBytes(racingSession));

        // Create the RacingSession
        restRacingSessionMockMvc.perform(post("/api/racingSessions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(racingSession)))
                .andExpect(status().isCreated());

        // Validate the RacingSession in the database
        List<RacingSession> racingSessions = racingSessionRepository.findAll();
        assertThat(racingSessions).hasSize(databaseSizeBeforeCreate + 1);
        RacingSession testRacingSession = racingSessions.get(racingSessions.size() - 1);
        assertThat(testRacingSession.getCompetition()).isEqualTo(DEFAULT_COMPETITION);
        assertThat(testRacingSession.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testRacingSession.getSeqNo()).isEqualTo(DEFAULT_SEQ_NO);
        assertThat(testRacingSession.getPlannedStartTime()).isEqualTo(DEFAULT_PLANNED_START_TIME);
        assertThat(testRacingSession.getTrackLayout()).isEqualTo(DEFAULT_TRACK_LAYOUT);
    }

    @Test
    @Transactional
    public void checkCompetitionIsRequired() throws Exception {
        // Validate the database is empty
        String json = new String (TestUtil.convertObjectToJsonBytes(racingSession));

        assertThat(racingSessionRepository.findAll()).hasSize(0);
        // set the field null
        racingSession.setCompetition(null);


        // Create the RacingSession, which fails.
        restRacingSessionMockMvc.perform(post("/api/racingSessions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(racingSession)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<RacingSession> racingSessions = racingSessionRepository.findAll();
        assertThat(racingSessions).hasSize(0);
    }

    @Test
    @Transactional
    public void checkTrackLayoutIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(racingSessionRepository.findAll()).hasSize(0);
        // set the field null
        racingSession.setTrackLayout(null);

        // Create the RacingSession, which fails.
        restRacingSessionMockMvc.perform(post("/api/racingSessions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(racingSession)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<RacingSession> racingSessions = racingSessionRepository.findAll();
        assertThat(racingSessions).hasSize(0);
    }

    @Test
    @Transactional
    public void getAllRacingSessions() throws Exception {
        // Initialize the database
        racingSessionRepository.saveAndFlush(racingSession);

        // Get all the racingSessions
        restRacingSessionMockMvc.perform(get("/api/racingSessions"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(racingSession.getId().intValue())))
                .andExpect(jsonPath("$.[*].competition").value(hasItem(DEFAULT_COMPETITION.toString())))
                .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
                .andExpect(jsonPath("$.[*].seqNo").value(hasItem(DEFAULT_SEQ_NO)))
                .andExpect(jsonPath("$.[*].plannedStartTime").value(hasItem(DEFAULT_PLANNED_START_TIME_STR)))
                .andExpect(jsonPath("$.[*].trackLayout").value(hasItem(DEFAULT_TRACK_LAYOUT.toString())));
    }

    @Test
    @Transactional
    public void getRacingSession() throws Exception {
        // Initialize the database
        racingSessionRepository.saveAndFlush(racingSession);

        // Get the racingSession
        restRacingSessionMockMvc.perform(get("/api/racingSessions/{id}", racingSession.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(racingSession.getId().intValue()))
            .andExpect(jsonPath("$.competition").value(DEFAULT_COMPETITION.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.seqNo").value(DEFAULT_SEQ_NO))
            .andExpect(jsonPath("$.plannedStartTime").value(DEFAULT_PLANNED_START_TIME_STR))
            .andExpect(jsonPath("$.trackLayout").value(DEFAULT_TRACK_LAYOUT.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingRacingSession() throws Exception {
        // Get the racingSession
        restRacingSessionMockMvc.perform(get("/api/racingSessions/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRacingSession() throws Exception {
        // Initialize the database
        racingSessionRepository.saveAndFlush(racingSession);

		int databaseSizeBeforeUpdate = racingSessionRepository.findAll().size();

        String json = new String (TestUtil.convertObjectToJsonBytes(racingSession));

        // Update the racingSession
        racingSession.setCompetition(UPDATED_COMPETITION);
        racingSession.setType(UPDATED_TYPE);
        racingSession.setSeqNo(UPDATED_SEQ_NO);
        racingSession.setPlannedStartTime(UPDATED_PLANNED_START_TIME);
        racingSession.setTrackLayout(UPDATED_TRACK_LAYOUT);
        restRacingSessionMockMvc.perform(put("/api/racingSessions")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(racingSession)))
                .andExpect(status().isOk());

        // Validate the RacingSession in the database
        List<RacingSession> racingSessions = racingSessionRepository.findAll();
        assertThat(racingSessions).hasSize(databaseSizeBeforeUpdate);
        RacingSession testRacingSession = racingSessions.get(racingSessions.size() - 1);
        assertThat(testRacingSession.getCompetition()).isEqualTo(UPDATED_COMPETITION);
        assertThat(testRacingSession.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testRacingSession.getSeqNo()).isEqualTo(UPDATED_SEQ_NO);
        assertThat(testRacingSession.getPlannedStartTime()).isEqualTo(UPDATED_PLANNED_START_TIME);
        assertThat(testRacingSession.getTrackLayout()).isEqualTo(UPDATED_TRACK_LAYOUT);
    }

    @Test
    @Transactional
    public void deleteRacingSession() throws Exception {
        // Initialize the database
        racingSessionRepository.saveAndFlush(racingSession);

		int databaseSizeBeforeDelete = racingSessionRepository.findAll().size();

        // Get the racingSession
        restRacingSessionMockMvc.perform(delete("/api/racingSessions/{id}", racingSession.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<RacingSession> racingSessions = racingSessionRepository.findAll();
        assertThat(racingSessions).hasSize(databaseSizeBeforeDelete - 1);
    }
}
