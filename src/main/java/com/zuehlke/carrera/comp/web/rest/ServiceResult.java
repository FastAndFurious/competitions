package com.zuehlke.carrera.comp.web.rest;

public class ServiceResult {

    public enum Status {
        OK,
        NOK
    }

    private Status status;

    private String message;

    public ServiceResult(Status status, String message) {
        this.status = status;
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
