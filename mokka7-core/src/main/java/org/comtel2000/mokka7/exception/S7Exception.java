/*
 * PROJECT Mokka7 (fork of Moka7)
 *
 * Copyright (C) 2017 J.Zimmermann All rights reserved.
 *
 * Mokka7 is free software: you can redistribute it and/or modify it under the terms of the Lesser
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or under EPL Eclipse Public License 1.0.
 *
 * This means that you have to chose in advance which take before you import the library into your
 * project.
 *
 * SNAP7 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE whatever license you
 * decide to adopt.
 */
package org.comtel2000.mokka7.exception;

import java.io.IOException;

/**
 * Default S7 protocol exception
 *
 * @author comtel
 *
 */
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
