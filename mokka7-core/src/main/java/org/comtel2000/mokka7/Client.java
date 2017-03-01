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

import org.comtel2000.mokka7.exception.S7Exception;

/**
 *
 * @author comtel
 *
 */
public interface Client {

    final byte[] buffer = new byte[1024];

    default Boolean readBit(AreaType area, int db, int start, int bitPos) throws S7Exception  {
        return readBit(area, db, start, bitPos, buffer);
    }

    default Boolean readBit(AreaType area, int db, int start, int bitPos, byte[] buffer) throws S7Exception  {
        if (readArea(area, db, start * 8 + bitPos, 1, DataType.S7WLBit, buffer)) {
            return S7.getBitAt(buffer, 0, 0);
        }
        return null;
    }

    default Byte readByte(AreaType area, int db, int start) throws S7Exception  {
        return readByte(area, db, start, buffer);
    }

    default Byte readByte(AreaType area, int db, int start, byte[] buffer) throws S7Exception  {
        if (readArea(area, db, start, 1, DataType.S7WLByte, buffer)) {
            return S7.getByteAt(buffer, 0);
        }
        return null;
    }

    default byte[] readBytes(AreaType area, int db, int start, int amount) throws S7Exception  {
        return readBytes(area, db, start, amount, buffer);
    }

    default byte[] readBytes(AreaType area, int db, int start, int amount, byte[] buffer) throws S7Exception  {
        if (readArea(area, db, start, amount, DataType.S7WLByte, buffer)) {
            return Arrays.copyOf(buffer, amount);
        }
        return null;
    }

    default Integer readInt(AreaType area, int db, int start) throws S7Exception  {
        return readInt(area, db, start, buffer);
    }

    default Integer readInt(AreaType area, int db, int start, byte[] buffer) throws S7Exception  {
        if (readArea(area, db, start, 1, DataType.S7WLDInt, buffer)) {
            return S7.getDIntAt(buffer, 0);
        }
        return null;
    }

    default Long readLong(AreaType area, int db, int start) throws S7Exception  {
        return readLong(area, db, start, buffer);
    }

    default Long readLong(AreaType area, int db, int start, byte[] buffer) throws S7Exception  {
        if (readArea(area, db, start, 1, DataType.S7WLDWord, buffer)) {
            return S7.getDWordAt(buffer, 0);
        }
        return null;
    }

    default String readString(AreaType area, int db, int start, int length) throws S7Exception  {
        return readString(area, db, start, length, StandardCharsets.UTF_8, buffer);
    }

    default String readString(AreaType area, int db, int start, int length, byte[] buffer) throws S7Exception  {
        return readString(area, db, start, length, StandardCharsets.UTF_8, buffer);
    }

    default String readString(AreaType area, int db, int start, int length, Charset charset) throws S7Exception  {
        return readString(area, db, start, length, charset, buffer);
    }

    default String readString(AreaType area, int db, int start, int length, Charset charset, byte[] buffer) throws S7Exception  {
        if (readArea(area, db, start, length, DataType.S7WLByte, buffer)) {
            return S7.getStringAt(buffer, 0, length);
        }
        return null;
    }

    default boolean writeBit(AreaType area, int db, int start, int bitPos, boolean value) throws S7Exception  {
        //reset first byte?
        S7.setByteAt(buffer, 0, (byte) 0x00);
        S7.setBitAt(buffer, 0, bitPos, value);
        return writeArea(area, db, (start * 8) + bitPos, 1, DataType.S7WLBit, buffer);
    }

    default boolean writeByte(AreaType area, int db, int start, byte value) throws S7Exception  {
        S7.setByteAt(buffer, 0, value);
        return writeArea(area, db, start, 1, DataType.S7WLByte, buffer);
    }


    default boolean writeBytes(AreaType area, int db, int start, byte[] values) throws S7Exception  {
        System.arraycopy(values, 0, buffer, 0, values.length);
        return writeArea(area, db, start, values.length, DataType.S7WLByte, buffer);
    }

    default boolean writeInt(AreaType area, int db, int start, int value) throws S7Exception  {
        S7.setDIntAt(buffer, 0, value);
        return writeArea(area, db, start, 1, DataType.S7WLDInt, buffer);
    }

    default boolean writeLong(AreaType area, int db, int start, long value) throws S7Exception  {
        S7.setDWordAt(buffer, 0, value);
        return writeArea(area, db, start, 1, DataType.S7WLDWord, buffer);
    }

    default boolean writeString(AreaType area, int db, int start, String value) throws S7Exception  {
        return writeString(area, db, start, value, StandardCharsets.UTF_8);
    }

    default boolean writeString(AreaType area, int db, int start, String value, Charset charset) throws S7Exception  {
        byte[] values = Objects.requireNonNull(value).getBytes(charset);
        return writeBytes(area, db, start, values);
    }

    public boolean readArea(AreaType area, int db, int start, int amount, DataType wordLen, byte[] buffer) throws S7Exception;

    public boolean writeArea(AreaType area, int db, int start, int amount, DataType type, byte[] buffer) throws S7Exception;
}
