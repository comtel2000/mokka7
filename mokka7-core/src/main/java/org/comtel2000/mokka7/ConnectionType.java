/*
 * PROJECT Mokka7 (fork of Snap7/Moka7)
 * 
 * Copyright (c) 2013,2016 Davide Nardella
 * Copyright (c) 2017 J.Zimmermann (comtel2000)
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Mokka7 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE whatever license you
 * decide to adopt.
 * 
 * Contributors:
 *    Davide Nardella - initial API and implementation
 *    J.Zimmermann    - Mokka7 fork
 * 
 */
package org.comtel2000.mokka7;

/**
 * Siemens S7 Communication Services
 *
 * @author comtel
 *
 */
public enum ConnectionType {

    /** Used for program loading, diagnostics */
    PG(0x01),

    /** Used for operator control and monitoring */
    OP(0x02),

    /** S7 Standard Communication */
    S7_BASIC(0x03);

    private final int value;

    ConnectionType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
