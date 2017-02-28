/*
 * PROJECT Mokka7 (fork of Moka7)
 *
 * Copyright (C) 2013, 2016 Davide Nardella All rights reserved. Copyright (C) 2017 J.Zimmermann All
 * rights reserved.
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
 * S7 Data types
 *
 * @author comtel
 *
 */
public enum DataType {

    S7WLBit(0x01),

    S7WLByte(0x02),

    S7WLChar(0x03),

    S7WLWord(0x04),

    S7WLInt(0x05),

    S7WLDWord(0x06),

    S7WLDInt(0x07),

    S7WLReal(0x08),

    S7WLCounter(0x1C),

    S7WLTimer(0x1D);

    private final byte value;

    DataType(int value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }

    /**
     * Type to byte length
     *
     * @param type DataType
     * @return encoded byte[] length
     */
    public static int getByteLength(DataType type) {
        switch (type) {
            case S7WLBit:// S7 sends 1 byte per bit
            case S7WLByte:
            case S7WLChar:
                return 1;
            case S7WLWord:
            case S7WLInt:
            case S7WLCounter:
            case S7WLTimer:
                return 2;
            case S7WLDWord:
            case S7WLDInt:
            case S7WLReal:
                return 4;
            default:
                return 0;
        }
    }
}
