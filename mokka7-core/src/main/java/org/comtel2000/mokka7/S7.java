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
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * Step 7 Constants and Conversion helper class
 *
 * @author Davide
 * @author comtel
 */
public class S7 {

    // Block type
    public static final int Block_OB = 0x38;
    public static final int Block_DB = 0x41;
    public static final int Block_SDB = 0x42;
    public static final int Block_FC = 0x43;
    public static final int Block_SFC = 0x44;
    public static final int Block_FB = 0x45;
    public static final int Block_SFB = 0x46;

    // Block languages
    public static final int BlockLangAWL = 0x01;
    public static final int BlockLangKOP = 0x02;
    public static final int BlockLangFUP = 0x03;
    public static final int BlockLangSCL = 0x04;
    public static final int BlockLangDB = 0x05;
    public static final int BlockLangGRAPH = 0x06;

    // Type Var
    public static final int S7TypeBool = 1;
    public static final int S7TypeInt = 1;

    // Sub Block Type
    public static final int SubBlk_OB = 0x08;
    public static final int SubBlk_DB = 0x0A;
    public static final int SubBlk_SDB = 0x0B;
    public static final int SubBlk_FC = 0x0C;
    public static final int SubBlk_SFC = 0x0D;
    public static final int SubBlk_FB = 0x0E;
    public static final int SubBlk_SFB = 0x0F;


    private static final byte[] BIT_MASK = { (byte) 0x01, (byte) 0x02, (byte) 0x04, (byte) 0x08, (byte) 0x10, (byte) 0x20, (byte) 0x40, (byte) 0x80 };

    public static int bcdToByte(byte b) {
        return ((b >> 4) * 10) + (b & 0x0F);
    }

    public static byte byteToBCD(int value) {
        return (byte) (((value / 10) << 4) | (value % 10));
    }


    public static boolean getBitAt(byte b, int bitpos) {
        int value = b & 0x0FF;
        return (value & BIT_MASK[bitpos]) != 0;
    }

    public static boolean getBitAt(byte[] buffer, int pos, int bitpos) {
        int value = buffer[pos] & 0x0FF;
        return (value & BIT_MASK[bitpos]) != 0;
    }

    public static byte[] getBytesAt(byte[] buffer, int pos, int maxLen) {
        return Arrays.copyOfRange(buffer, pos, pos + maxLen);
    }

    public static Date getDateAt(byte[] buffer, int pos) {
        int year, month, day, hour, min, sec;
        Calendar cal = Calendar.getInstance();

        year = bcdToByte(buffer[pos]);
        if (year < 90) {
            year += 2000;
        } else {
            year += 1900;
        }

        month = bcdToByte(buffer[pos + 1]) - 1;
        day = bcdToByte(buffer[pos + 2]);
        hour = bcdToByte(buffer[pos + 3]);
        min = bcdToByte(buffer[pos + 4]);
        sec = bcdToByte(buffer[pos + 5]);

        cal.set(year, month, day, hour, min, sec);

        return cal.getTime();
    }

    public static LocalDateTime getlDateTimeAt(byte[] buffer, int pos) {
        int year, month, day, hour, min, sec;
        year = bcdToByte(buffer[pos]);
        year += year < 90 ? 2000 : 1900;
        month = bcdToByte(buffer[pos + 1]) - 1;
        day = bcdToByte(buffer[pos + 2]);
        hour = bcdToByte(buffer[pos + 3]);
        min = bcdToByte(buffer[pos + 4]);
        sec = bcdToByte(buffer[pos + 5]);
        return LocalDateTime.of(year, month, day, hour, min, sec);
    }

    // Returns a 32 bit signed value : from 0 to 4294967295 (2^32-1)
    public static int getDIntAt(byte[] buffer, int pos) {
        return ((buffer[pos]) << 24 | (buffer[pos + 1] & 0xFF) << 16 | (buffer[pos + 2] & 0xFF) << 8 | (buffer[pos + 3] & 0xFF));
    }

    // Returns a 32 bit unsigned value : from 0 to 4294967295 (2^32-1)
    public static long getDWordAt(byte[] buffer, int pos) {
        return ((buffer[pos]) << 24 | (buffer[pos + 1] & 0xFF) << 16 | (buffer[pos + 2] & 0xFF) << 8 | (buffer[pos + 3] & 0xFF));
    }

    // Returns a 32 bit floating point
    public static float getFloatAt(byte[] buffer, int pos) {
        int IntFloat = getDIntAt(buffer, pos);
        return Float.intBitsToFloat(IntFloat);
    }

    /**
     * The buffer as UTF-8 String
     *
     * @param buffer
     * @param pos
     * @param maxLen
     * @return
     */
    public static String getPrintableStringAt(byte[] buffer, int pos, int maxLen) {
        return getPrintableStringAt(buffer, pos, maxLen, StandardCharsets.UTF_8);
    }

    public static String getPrintableStringAt(byte[] buffer, int pos, int maxLen, Charset cs) {
        byte[] temp = new byte[maxLen];
        System.arraycopy(buffer, pos, temp, 0, maxLen);
        for (int i = 0; i < temp.length; i++) {
            // escape whitespace to '.'
            if ((temp[i] < 31) || (temp[i] > 126)) {
                temp[i] = 46;
            }
        }
        return new String(temp, cs);
    }

    // Returns a 16 bit signed value : from -32768 to 32767
    public static int getShortAt(byte[] buffer, int pos) {
        int hi = (buffer[pos]);
        int lo = (buffer[pos + 1] & 0x00FF);
        return ((hi << 8) + lo);
    }

    public static String getStringAt(byte[] buffer, int pos, int maxLen) {
        return getStringAt(buffer, pos, maxLen, StandardCharsets.UTF_8);
    }

    public static String getStringAt(byte[] buffer, int pos, int maxLen, Charset charset) {
        return new String(buffer, pos, maxLen, charset);
    }

    public static byte getByteAt(byte[] buffer, int pos) {
        return buffer[pos];
    }

    /**
     * Returns a 16 bit unsigned value : from 0 to 65535 (2^16-1)
     *
     * @param buffer
     * @param pos start position
     * @return
     */
    public static int getWordAt(byte[] buffer, int pos) {
        int hi = (buffer[pos] & 0x00FF);
        int lo = (buffer[pos + 1] & 0x00FF);
        return (hi << 8) + lo;
    }

    public static void setBitAt(byte[] buffer, int pos, int bitPos, boolean value) {
        if (bitPos < 0) {
            bitPos = 0;
        }else if (bitPos > 7) {
            bitPos = 7;
        }
        if (value) {
            buffer[pos] = (byte) (buffer[pos] | BIT_MASK[bitPos]);
        } else {
            buffer[pos] = (byte) (buffer[pos] & ~BIT_MASK[bitPos]);
        }
    }

    public static void setDateAt(byte[] buffer, int pos, Date dateTime) {
        int year, month, day, hour, min, sec, dow;
        Calendar cal = Calendar.getInstance();
        cal.setTime(dateTime);

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;
        day = cal.get(Calendar.DAY_OF_MONTH);
        hour = cal.get(Calendar.HOUR_OF_DAY);
        min = cal.get(Calendar.MINUTE);
        sec = cal.get(Calendar.SECOND);
        dow = cal.get(Calendar.DAY_OF_WEEK);

        if (year > 1999) {
            year -= 2000;
        }

        buffer[pos] = byteToBCD(year);
        buffer[pos + 1] = byteToBCD(month);
        buffer[pos + 2] = byteToBCD(day);
        buffer[pos + 3] = byteToBCD(hour);
        buffer[pos + 4] = byteToBCD(min);
        buffer[pos + 5] = byteToBCD(sec);
        buffer[pos + 6] = 0;
        buffer[pos + 7] = byteToBCD(dow);
    }

    public static void setDateTimeAt(byte[] buffer, int pos, LocalDateTime dateTime) {
        int year, month, day, hour, min, sec, dow;
        year = dateTime.get(ChronoField.YEAR);
        month = dateTime.get(ChronoField.MONTH_OF_YEAR);
        day = dateTime.get(ChronoField.DAY_OF_MONTH);
        hour = dateTime.get(ChronoField.HOUR_OF_DAY);
        min = dateTime.get(ChronoField.MINUTE_OF_HOUR);
        sec = dateTime.get(ChronoField.SECOND_OF_MINUTE);
        dow = dateTime.get(ChronoField.DAY_OF_WEEK);

        if (year > 1999) {
            year -= 2000;
        }

        buffer[pos] = byteToBCD(year);
        buffer[pos + 1] = byteToBCD(month);
        buffer[pos + 2] = byteToBCD(day);
        buffer[pos + 3] = byteToBCD(hour);
        buffer[pos + 4] = byteToBCD(min);
        buffer[pos + 5] = byteToBCD(sec);
        buffer[pos + 6] = 0;
        buffer[pos + 7] = byteToBCD(dow);
    }

    public static void setDIntAt(byte[] buffer, int pos, int value) {
        buffer[pos + 3] = (byte) (value & 0xFF);
        buffer[pos + 2] = (byte) ((value >> 8) & 0xFF);
        buffer[pos + 1] = (byte) ((value >> 16) & 0xFF);
        buffer[pos] = (byte) ((value >> 24) & 0xFF);
    }

    public static void setDWordAt(byte[] buffer, int pos, long value) {
        buffer[pos + 3] = (byte) (value & 0xFF);
        buffer[pos + 2] = (byte) ((value >> 8) & 0xFF);
        buffer[pos + 1] = (byte) ((value >> 16) & 0xFF);
        buffer[pos] = (byte) ((value >> 24) & 0xFF);
    }

    public static void setFloatAt(byte[] buffer, int pos, float value) {
        int DInt = Float.floatToIntBits(value);
        setDIntAt(buffer, pos, DInt);
    }

    public static void setShortAt(byte[] buffer, int pos, short value) {
        buffer[pos] = (byte) (value >> 8);
        buffer[pos + 1] = (byte) (value & 0x00FF);
    }

    public static void setWordAt(byte[] buffer, int pos, int value) {
        setShortAt(buffer, pos, (short) value);
    }

    public static void setByteAt(byte[] buffer, int pos, byte b) {
        buffer[pos] = b;
    }

    public static void setBytesAt(byte[] buffer, int pos, byte[] bytes) {
        System.arraycopy(bytes, 0, buffer, pos, bytes.length);
    }

    public static void setStringAt(byte[] buffer, int pos, String value) {
        setStringAt(buffer, pos, value, StandardCharsets.UTF_8);
    }

    public static void setStringAt(byte[] buffer, int pos, String value, Charset charset) {
        setBytesAt(buffer, pos, value.getBytes(charset));
    }

    protected static void hexDump(byte[] buffer) {
        if (buffer == null || buffer.length == 0){
            throw new IllegalArgumentException("invalid buffer/size length");
        }
        hexDump(buffer, buffer.length);
    }

    protected static void hexDump(byte[] buffer, int size) {
        if (buffer == null || buffer.length == 0 || size < 1){
            throw new IllegalArgumentException("invalid buffer/size length");
        }
        int maxLength = Math.min(buffer.length, size);
        final StringBuilder sb = new StringBuilder();
        int count = 0;
        for (int i = 0; i < maxLength; i++) {
            String hv = Integer.toHexString(buffer[i] & 0x0FF);
            if (hv.length() == 1){
                sb.append("0");
            }
            sb.append(hv.toUpperCase()).append(" ");
            if (++count == 16) {
                sb.append(" ").append(S7.getPrintableStringAt(buffer, i - 15, 16));
                System.out.println(sb.toString());
                sb.setLength(0);
                count = 0;
            }
        }
        if (count > 0) {
            while (sb.length() < 49) {
                sb.append(" ");
            }
            sb.append(S7.getPrintableStringAt(buffer, maxLength - count, count));
            System.out.println(sb.toString());
        }
    }

    protected static void binaryDump(byte b){
        System.out.println(Integer.toBinaryString(b & 255 | 256).substring(1));
    }

    protected static void binaryDump(byte[] bytes){
        StringBuilder sb  = new StringBuilder();
        if(bytes != null){
            for (byte b : bytes) {
                sb.append(Integer.toBinaryString(b & 255 | 256).substring(1));
            }
        }
        System.out.println(sb.toString());
    }
}
