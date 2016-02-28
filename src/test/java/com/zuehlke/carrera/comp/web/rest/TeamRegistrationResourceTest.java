package com.zuehlke.carrera.comp.web.rest;

import com.zuehlke.carrera.comp.CompetitionManagerApp;
import com.zuehlke.carrera.comp.domain.TeamRegistration;
import com.zuehlke.carrera.comp.repository.TeamRegistrationRepository;

import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
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
import org.joda.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the TeamRegistrationResource REST controller.
 *
 * @see TeamRegistrationResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CompetitionManagerApp.class)
@WebAppConfiguration
@IntegrationTest
public class TeamRegistrationResourceTest {

    private static final String DEFAULT_COMPETITION = "SAMPLE_TEXT";
    private static final String UPDATED_COMPETITION = "UPDATED_TEXT";
    private static final String DEFAULT_TEAM = "SAMPLE_TEXT";
    private static final String UPDATED_TEAM = "UPDATED_TEXT";
    private static final String A_TWITTER_NAME = "@wgiersche";

    private static final LocalDateTime DEFAULT_REGISTRATION_TIME = new LocalDateTime(0L);
    private static final LocalDateTime UPDATED_REGISTRATION_TIME = new LocalDateTime()
            .withSecondOfMinute(0).withMillisOfSecond(0);
    @Inject
    private TeamRegistrationRepository teamRegistrationRepository;

    private MockMvc restTeamRegistrationMockMvc;

    private TeamRegistration teamRegistration;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        TeamRegistrationResource teamRegistrationResource = new TeamRegistrationResource();
        ReflectionTestUtils.setField(teamRegistrationResource, "teamRegistrationRepository", teamRegistrationRepository);
        this.restTeamRegistrationMockMvc = MockMvcBuilders.standaloneSetup(teamRegistrationResource).build();
    }

    @Before
    public void initTest() {
        teamRegistration = new TeamRegistration();
        teamRegistration.setCompetition(DEFAULT_COMPETITION);
        teamRegistration.setTeam(DEFAULT_TEAM);
        teamRegistration.setRegistrationTime(DEFAULT_REGISTRATION_TIME);
        teamRegistration.setAccessCode("SAMPLE_TEXT");
        teamRegistration.setTwitterNames("@wgiersche,@hgiersche,@bgiersche");
    }

    @Test
    @Transactional
    public void createTeamRegistration() throws Exception {
        int databaseSizeBeforeCreate = teamRegistrationRepository.findAll().size();

        // Create the TeamRegistration
        restTeamRegistrationMockMvc.perform(post("/api/teamRegistrations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(teamRegistration)))
                .andExpect(status().isCreated());

        // Validate the TeamRegistration in the database
        List<TeamRegistration> teamRegistrations = teamRegistrationRepository.findAll();
        assertThat(teamRegistrations).hasSize(databaseSizeBeforeCreate + 1);
        TeamRegistration testTeamRegistration = teamRegistrations.get(teamRegistrations.size() - 1);
        assertThat(testTeamRegistration.getCompetition()).isEqualTo(DEFAULT_COMPETITION);
        assertThat(testTeamRegistration.getTeam()).isEqualTo(DEFAULT_TEAM);
        assertThat(testTeamRegistration.getTwitterNames()).contains(A_TWITTER_NAME);
    }

    @Test
    @Transactional
    public void checkCompetitionIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(teamRegistrationRepository.findAll()).hasSize(0);
        // set the field null
        teamRegistration.setCompetition(null);

        // Create the TeamRegistration, which fails.
        restTeamRegistrationMockMvc.perform(post("/api/teamRegistrations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(teamRegistration)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<TeamRegistration> teamRegistrations = teamRegistrationRepository.findAll();
        assertThat(teamRegistrations).hasSize(0);
    }

    @Test
    @Transactional
    public void checkTeamIsRequired() throws Exception {
        // Validate the database is empty
        assertThat(teamRegistrationRepository.findAll()).hasSize(0);
        // set the field null
        teamRegistration.setTeam(null);

        // Create the TeamRegistration, which fails.
        restTeamRegistrationMockMvc.perform(post("/api/teamRegistrations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(teamRegistration)))
                .andExpect(status().isBadRequest());

        // Validate the database is still empty
        List<TeamRegistration> teamRegistrations = teamRegistrationRepository.findAll();
        assertThat(teamRegistrations).hasSize(0);
    }

    @Test
    @Transactional
    public void getAllTeamRegistrations() throws Exception {
        // Initialize the database
        teamRegistrationRepository.saveAndFlush(teamRegistration);

        // Get all the teamRegistrations
        restTeamRegistrationMockMvc.perform(get("/api/teamRegistrations"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(teamRegistration.getId().intValue())))
                .andExpect(jsonPath("$.[*].competition").value(hasItem(DEFAULT_COMPETITION)))
                .andExpect(jsonPath("$.[*].team").value(hasItem(DEFAULT_TEAM)));
    }

    @Test
    @Transactional
    public void getTeamRegistration() throws Exception {
        // Initialize the database
        teamRegistrationRepository.saveAndFlush(teamRegistration);

        // Get the teamRegistration
        restTeamRegistrationMockMvc.perform(get("/api/teamRegistrations/{id}", teamRegistration.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(teamRegistration.getId().intValue()))
            .andExpect(jsonPath("$.competition").value(DEFAULT_COMPETITION))
            .andExpect(jsonPath("$.team").value(DEFAULT_TEAM));
    }

    @Test
    @Transactional
    public void getNonExistingTeamRegistration() throws Exception {
        // Get the teamRegistration
        restTeamRegistrationMockMvc.perform(get("/api/teamRegistrations/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTeamRegistration() throws Exception {
        // Initialize the database
        teamRegistrationRepository.saveAndFlush(teamRegistration);

		int databaseSizeBeforeUpdate = teamRegistrationRepository.findAll().size();

        // Update the teamRegistration
        teamRegistration.setCompetition(UPDATED_COMPETITION);
        teamRegistration.setTeam(UPDATED_TEAM);
        teamRegistration.setRegistrationTime(UPDATED_REGISTRATION_TIME);
        restTeamRegistrationMockMvc.perform(put("/api/teamRegistrations")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(teamRegistration)))
                .andExpect(status().isOk());

        // Validate the TeamRegistration in the database
        List<TeamRegistration> teamRegistrations = teamRegistrationRepository.findAll();
        assertThat(teamRegistrations).hasSize(databaseSizeBeforeUpdate);
        TeamRegistration testTeamRegistration = teamRegistrations.get(teamRegistrations.size() - 1);
        assertThat(testTeamRegistration.getCompetition()).isEqualTo(UPDATED_COMPETITION);
        assertThat(testTeamRegistration.getTeam()).isEqualTo(UPDATED_TEAM);
        assertThat(testTeamRegistration.getRegistrationTime()).isEqualTo(UPDATED_REGISTRATION_TIME);
        assertThat(testTeamRegistration.getListOfTwitterNames()).contains(A_TWITTER_NAME);
    }

    @Test
    @Transactional
    public void deleteTeamRegistration() throws Exception {
        // Initialize the database
        teamRegistrationRepository.saveAndFlush(teamRegistration);

		int databaseSizeBeforeDelete = teamRegistrationRepository.findAll().size();

        // Get the teamRegistration
        restTeamRegistrationMockMvc.perform(delete("/api/teamRegistrations/{id}", teamRegistration.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<TeamRegistration> teamRegistrations = teamRegistrationRepository.findAll();
        assertThat(teamRegistrations).hasSize(databaseSizeBeforeDelete - 1);
    }
}
