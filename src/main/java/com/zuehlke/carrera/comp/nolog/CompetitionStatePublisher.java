package com.zuehlke.carrera.comp.nolog;

public interface CompetitionStatePublisher {
    void publish(String competition, Long sessionId, String team);
}
