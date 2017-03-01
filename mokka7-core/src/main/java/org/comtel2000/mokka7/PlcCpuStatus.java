/*
 * PROJECT Mokka7 (fork of Moka7)
 *
 * Copyright (C) 2013, 2016 Davide Nardella All rights reserved.
 * Copyright (C) 2017 J.Zimmermann All rights reserved.
 *
 * SNAP7 is free software: you can redistribute it and/or modify it under the terms of the Lesser
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
package org.comtel2000.mokka7;

/**
 *
 * @author comtel
 *
 */
public enum PlcCpuStatus {

    UNKNOWN(0x00), STOP(0x04), RUN(0x08);

    private final byte value;

    PlcCpuStatus(int value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }

    public static PlcCpuStatus valueOf(byte b) {
        // Since RUN status is always 0x08 for all CPUs and CPs, STOP status
        // sometime can be coded as 0x03 (especially for old cpu...)
        for (PlcCpuStatus s : values()) {
            if (s.value == b) {
                return s;
            }
        }
        return RUN;
    }
}
