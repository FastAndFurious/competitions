package com.zuehlke.carrera.comp.domain;

/**
 *  represents the different phases that a competition undergoes.
 */
public enum Phase {

    // the first phase - no activity - just a schedule
    SCHEDULED,

    // the second phase
    PREPARATION,

    TRAINING,

    QUALIFICATION,

    COMPETITION,

    COMPLETED
}
