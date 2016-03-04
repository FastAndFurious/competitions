package com.zuehlke.carrera.comp.web.outbound;

public class OutboundServiceException extends Exception {

    public OutboundServiceException() {
    }

    public OutboundServiceException(String message) {
        super(message);
    }

    public OutboundServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public OutboundServiceException(Throwable cause) {
        super(cause);
    }

    public OutboundServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
