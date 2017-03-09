/*
 * PROJECT Mokka7 (fork of Snap7/Moka7)
 *
 * Copyright (c) 2013,2016 Davide Nardella Copyright (c) 2017 J.Zimmermann (comtel2000)
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Mokka7 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE whatever license you
 * decide to adopt.
 *
 * Contributors: Davide Nardella - initial API and implementation J.Zimmermann - Mokka7 fork
 *
 */
package org.comtel2000.mokka7.exception;

/**
 * Default S7 protocol exception
 *
 * @author comtel
 *
 */
public class S7Exception extends Exception {

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
