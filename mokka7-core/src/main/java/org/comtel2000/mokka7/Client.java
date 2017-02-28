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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;

public interface Client {

    final byte[] buffer = new byte[1024];

    default Boolean readBit(AreaType area, int db, int start, int bitPos) {
        return readBit(area, db, start, bitPos, buffer);
    }

    default Boolean readBit(AreaType area, int db, int start, int bitPos, byte[] buffer) {
        if (readArea(area, db, start * 8 + bitPos, 1, DataType.S7WLBit, buffer) == 0) {
            return S7.getBitAt(buffer, 0, bitPos);
        }
        return null;
    }

    default Byte readByte(AreaType area, int db, int start) {
        return readByte(area, db, start, buffer);
    }

    default Byte readByte(AreaType area, int db, int start, byte[] buffer) {
        if (readArea(area, db, start, 1, DataType.S7WLByte, buffer) == 0) {
            return S7.getByteAt(buffer, 0);
        }
        return null;
    }

    default byte[] readBytes(AreaType area, int db, int start, int amount) {
        return readBytes(area, db, start, amount, buffer);
    }

    default byte[] readBytes(AreaType area, int db, int start, int amount, byte[] buffer) {
        if (readArea(area, db, start, amount, DataType.S7WLByte, buffer) == 0) {
            return Arrays.copyOf(buffer, amount);
        }
        return null;
    }

    default Integer readInt(AreaType area, int db, int start) {
        return readInt(area, db, start, buffer);
    }

    default Integer readInt(AreaType area, int db, int start, byte[] buffer) {
        if (readArea(area, db, start, 1, DataType.S7WLDInt, buffer) == 0) {
            return S7.getDIntAt(buffer, 0);
        }
        return null;
    }

    default Long readLong(AreaType area, int db, int start) {
        return readLong(area, db, start, buffer);
    }

    default Long readLong(AreaType area, int db, int start, byte[] buffer) {
        if (readArea(area, db, start, 1, DataType.S7WLDWord, buffer) == 0) {
            return S7.getDWordAt(buffer, 0);
        }
        return null;
    }

    default String readString(AreaType area, int db, int start, int length) {
        return readString(area, db, start, length, StandardCharsets.UTF_8, buffer);
    }

    default String readString(AreaType area, int db, int start, int length, byte[] buffer) {
        return readString(area, db, start, length, StandardCharsets.UTF_8, buffer);
    }

    default String readString(AreaType area, int db, int start, int length, Charset charset) {
        return readString(area, db, start, length, charset, buffer);
    }

    default String readString(AreaType area, int db, int start, int length, Charset charset, byte[] buffer) {
        if (readArea(area, db, start, length, DataType.S7WLByte, buffer) == 0) {
            return S7.getStringAt(buffer, 0, length);
        }
        return null;
    }

    default int writeBit(AreaType area, int db, int start, int bitPos, boolean value) {
        //reset first byte?
        S7.setByteAt(buffer, 0, (byte) 0x00);
        S7.setBitAt(buffer, 0, bitPos, value);
        return writeArea(area, db, (start * 8) + bitPos, 1, DataType.S7WLBit, buffer);
    }

    default int writeByte(AreaType area, int db, int start, byte value) {
        S7.setByteAt(buffer, 0, value);
        return writeArea(area, db, start, 1, DataType.S7WLByte, buffer);
    }


    default int writeBytes(AreaType area, int db, int start, byte[] values) {
        System.arraycopy(values, 0, buffer, 0, values.length);
        return writeArea(area, db, start, values.length, DataType.S7WLByte, buffer);
    }

    default int writeInt(AreaType area, int db, int start, int value) {
        S7.setDIntAt(buffer, 0, value);
        return writeArea(area, db, start, 1, DataType.S7WLDInt, buffer);
    }

    default int writeLong(AreaType area, int db, int start, long value) {
        S7.setDWordAt(buffer, 0, value);
        return writeArea(area, db, start, 1, DataType.S7WLDWord, buffer);
    }

    default int writeString(AreaType area, int db, int start, String value) {
        return writeString(area, db, start, value, StandardCharsets.UTF_8);
    }

    default int writeString(AreaType area, int db, int start, String value, Charset charset) {
        byte[] values = Objects.requireNonNull(value).getBytes(charset);
        return writeBytes(area, db, start, values);
    }

    public int readArea(AreaType area, int db, int start, int amount, DataType wordLen, byte[] buffer);

    public int writeArea(AreaType area, int db, int start, int amount, DataType type, byte[] buffer);
}
