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
package org.comtel2000.mokka7.type;

import java.util.Arrays;
import java.util.List;

/**
 * PLC area types
 *
 * @author comtel
 *
 */
public enum AreaType {
    /** System Information (caller) */
    SYS_INFO(0x03),

    /** S7 counters */
    SYSTEM_FLAGS(0x05),

    /** Analog Inputs - System info (200 family) */
    ANALOG_INPUTS(0x06),

    /** Analog Outputs - System flags (200 family) */
    ANALOG_OUTPUTS(0x07),

    /** Analog Counter Input area (200 family) */
    CT_INPUTS(0x1C),

    /** S7 Timer area */
    TM(0x1D),

    /** Analog Counter Output area (200 family) */
    CT_OUTPUTS(0x1E),

    /** IEC counters (200 family) */
    TM_IEC(0x1F),

    /** PE (inputs) instance area */
    PE(0x81),

    /** PA (outputs) instance area */
    PA(0x82),

    /** Marker (flags) area */
    MK(0x83),

    /** DataBlock (Peripheral I/O) area */
    DB(0x84),

    /** DataBlock area */
    DI(0x85),

    /** DataBlock (local) area */
    DB_LOCAL(0x86),

    /** IEC timers (200 family) */
    TM_V(0x87);

    private final byte value;

    AreaType(int value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }

    public final static List<AreaType> SUPPORTED = Arrays.asList(MK, DB, TM);

}
