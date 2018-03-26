package com.shibadog.lutrinae.domain.exception;

public class ApplicationException extends Exception {

    private static final long serialVersionUID = 1L;

    public ApplicationException(String msg) {
        super(msg);
    }

    public ApplicationException(String msg, Throwable t) {
        super(msg, t);
    }
}