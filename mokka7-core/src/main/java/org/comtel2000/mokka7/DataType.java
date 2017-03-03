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
 * S7 Data types
 *
 * @author comtel
 *
 */
public enum DataType {

    /** Bit, Boolean */
    S7WLBit(0x01),

    /** Unsigned Byte */
    S7WLByte(0x02),

    /** Unsigned Byte,Char */
    S7WLChar(0x03),

    /** Unsigned Word, Short, BCD */
    S7WLWord(0x04),

    /** Signed Word, Short, BCD */
    S7WLInt(0x05),

    /** Unsigned Double Word, Long, LBCD, Float */
    S7WLDWord(0x06),

    /** Signed Double Word, Long, LBCD, Float */
    S7WLDInt(0x07),

    /** Float */
    S7WLReal(0x08),

    /** Counter type */
    S7WLCounter(0x1C),

    /** Tyimer type */
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
