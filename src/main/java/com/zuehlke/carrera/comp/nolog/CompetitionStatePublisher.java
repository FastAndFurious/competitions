package com.zuehlke.carrera.comp.nolog;

public interface CompetitionStatePublisher {
    void publishStatus(String competition, Long sessionId, String team);
    void publishSchedule(Long sessionId );
}
