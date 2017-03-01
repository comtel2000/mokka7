package org.comtel2000.mokka7.exception;

import java.io.IOException;

public class S7Exception extends IOException {

    private static final long serialVersionUID = -3135373121524472268L;

    private final int errorCode;

    public S7Exception(int error, String message) {
        super(message);
        errorCode = error;
    }

    public S7Exception(int error, String message, Throwable th) {
        super(message, th);
        errorCode = error;
    }

    public int getErrorCode() {
        return errorCode;
    }
}
