package com.wasoko.promotion.service.exception;

import lombok.Getter;

import java.io.Serial;

/**
 * The type Soko runtime exception.
 */
@Getter
public class SokoRuntimeException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -4293967659288606499L;

    private String error = "Error";

    /**
     * Instantiates a new Soko runtime exception.
     *
     * @param throwable the throwable
     */
    public SokoRuntimeException(Throwable throwable) {
        super(throwable);
    }

    /**
     * Instantiates a new Soko runtime exception.
     *
     * @param message   the message
     * @param throwable the throwable
     */
    public SokoRuntimeException(String message, Throwable throwable) {
        super(message, throwable);
    }

    /**
     * Instantiates a new Soko runtime exception.
     *
     * @param error   the error
     * @param message the message
     */
    public SokoRuntimeException(String error, String message) {
        super(message);
        this.error = error;
    }

    /**
     * Instantiates a new Soko runtime exception.
     *
     * @param message the message
     */
    public SokoRuntimeException(String message) {
        super(message);
    }
}
