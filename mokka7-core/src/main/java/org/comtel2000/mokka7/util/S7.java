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
package org.comtel2000.mokka7.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Consumer;

import org.comtel2000.mokka7.block.S7Timer;

/**
 * Step 7 Constants and Conversion helper class
 *
 * @author Davide
 * @author comtel
 */
public class S7 {

    private static final byte[] BIT_MASK = { (byte) 0x01, (byte) 0x02, (byte) 0x04, (byte) 0x08, (byte) 0x10, (byte) 0x20, (byte) 0x40, (byte) 0x80 };

    private final static char EMP_CHAR = '\u0020', DOT_CHAR = '.';
    private final static char HEX_DIGIT[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

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
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static LocalDateTime getDateTimeAt(byte[] buffer, int pos) {
        int year, month, day, hour, min, sec;
        year = bcdToByte(buffer[pos]);
        year += year < 90 ? 2000 : 1900;
        month = bcdToByte(buffer[pos + 1]);
        day = bcdToByte(buffer[pos + 2]);
        hour = bcdToByte(buffer[pos + 3]);
        min = bcdToByte(buffer[pos + 4]);
        sec = bcdToByte(buffer[pos + 5]);

        // First two digits of miliseconds
        // int msecH = bcdToByte(buffer[pos + 6]) * 10;
        // Last digit of miliseconds
        // int msecL = bcdToByte(buffer[pos + 7]) / 10;

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

    public static String getPrintableStringAt(byte[] buffer, int pos, int maxLen) {
        StringBuilder sb = new StringBuilder();
        for (int i = pos; i < pos + maxLen; i++) {
            char ch = (char) (buffer[i] & 0xFF);
            if (Character.isISOControl(ch) || Character.isWhitespace(ch)) {
                sb.append(DOT_CHAR);
                continue;
            }
            sb.append(ch);
        }
        return sb.toString();
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

    public static String getS7StringAt(byte[] buffer, int pos) {
        int length = buffer[pos + 1];
        return new String(buffer, pos + 2, length, StandardCharsets.UTF_8);
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

    public static S7Timer getS7TimerAt(byte[] buffer, int pos) {
        return S7Timer.of(buffer, pos);
    }

    public static void setBitAt(byte[] buffer, int pos, int bitPos, boolean value) {
        if (bitPos < 0) {
            bitPos = 0;
        } else if (bitPos > 7) {
            bitPos = 7;
        }
        if (value) {
            buffer[pos] = (byte) (buffer[pos] | BIT_MASK[bitPos]);
        } else {
            buffer[pos] = (byte) (buffer[pos] & ~BIT_MASK[bitPos]);
        }
    }

    public static void setDateAt(byte[] buffer, int pos, Date date) {
        int year, month, day, hour, min, sec;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        year = cal.get(Calendar.YEAR);
        month = cal.get(Calendar.MONTH) + 1;
        day = cal.get(Calendar.DAY_OF_MONTH);
        hour = cal.get(Calendar.HOUR_OF_DAY);
        min = cal.get(Calendar.MINUTE);
        sec = cal.get(Calendar.SECOND);
        // milli = cal.get(Calendar.MILLISECOND);
        // // First two digits of miliseconds
        // int msecH = milli / 10;
        // // Last digit of miliseconds
        // int msecL = milli % 10;

        if (year > 1999) {
            year -= 2000;
        }
        buffer[pos] = byteToBCD(year);
        buffer[pos + 1] = byteToBCD(month);
        buffer[pos + 2] = byteToBCD(day);
        buffer[pos + 3] = byteToBCD(hour);
        buffer[pos + 4] = byteToBCD(min);
        buffer[pos + 5] = byteToBCD(sec);
        buffer[pos + 6] = byteToBCD(0);
        buffer[pos + 7] = byteToBCD(0);

    }

    public static void setDateTimeAt(byte[] buffer, int pos, LocalDateTime dateTime) {
        int year, month, day, hour, min, sec;
        year = dateTime.get(ChronoField.YEAR);
        month = dateTime.get(ChronoField.MONTH_OF_YEAR);
        day = dateTime.get(ChronoField.DAY_OF_MONTH);
        hour = dateTime.get(ChronoField.HOUR_OF_DAY);
        min = dateTime.get(ChronoField.MINUTE_OF_HOUR);
        sec = dateTime.get(ChronoField.SECOND_OF_MINUTE);
        // milli = dateTime.get(ChronoField.MILLI_OF_SECOND);
        // // First two digits of miliseconds
        // int msecH = milli / 10;
        // // Last digit of miliseconds
        // int msecL = milli % 10;

        if (year > 1999) {
            year -= 2000;
        }
        buffer[pos] = byteToBCD(year);
        buffer[pos + 1] = byteToBCD(month);
        buffer[pos + 2] = byteToBCD(day);
        buffer[pos + 3] = byteToBCD(hour);
        buffer[pos + 4] = byteToBCD(min);
        buffer[pos + 5] = byteToBCD(sec);
        buffer[pos + 6] = byteToBCD(0);
        buffer[pos + 7] = byteToBCD(0);
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

    public static void setS7StringAt(byte[] buffer, int pos, int maxLen, String value) {
        byte[] data = value.getBytes(StandardCharsets.UTF_8);
        buffer[pos] = (byte) (maxLen & 0xFF);
        buffer[pos + 1] = (byte) (data.length & 0xFF);
        System.arraycopy(data, 0, buffer, pos + 2, Math.min(data.length, maxLen));
    }

    public static void hexDump(byte[] buffer, Consumer<String> c) {
        hexDump(buffer, 128, c);
    }

    public static void hexDump(byte[] buffer, int maxlines, Consumer<String> c) {
        hexDump(buffer, maxlines, 0, c);
    }
    public static void hexDump(byte[] buffer, int maxlines, int offset, Consumer<String> c) {
        if (buffer == null || buffer.length == 0 || maxlines < 1 || offset < 0) {
            return;
        }
        int length, pos;
        int line = 0;
        StringBuilder sb = new StringBuilder();
        while (line < maxlines && (pos = (line * 16) + offset) < buffer.length) {
            length = Math.min(16, buffer.length - pos);
            if (length < 1) {
                return;
            }
            sb.setLength(0);
            for (int i = 28; i >= 0; i -= 4) {
                sb.append(HEX_DIGIT[0x0F & line >>> i]);
            }
            sb.append('0').append(':').append(EMP_CHAR).append(EMP_CHAR);
            line++;
            for (int i = 0; i < 16; i++) {
                if (i < length) {
                    sb.append(HEX_DIGIT[0x0F & buffer[pos + i] >> 4]);
                    sb.append(HEX_DIGIT[0x0F & buffer[pos + i]]);
                } else {
                    sb.append(EMP_CHAR).append(EMP_CHAR);
                }
                sb.append(EMP_CHAR);
                if (i == 7) {
                    sb.append(EMP_CHAR);
                }
            }
            sb.append(EMP_CHAR);
            for (int i = 0; i < 16; i++) {
                if (i >= length) {
                    sb.append(EMP_CHAR);
                    continue;
                }
                char ch = (char) (buffer[pos + i] & 0xFF);
                if (Character.isISOControl(ch) || Character.isWhitespace(ch)) {
                    sb.append(DOT_CHAR);
                    continue;
                }
                sb.append(ch);
            }
            c.accept(sb.toString());
        }
    }

    protected static void binaryDump(byte b) {
        System.out.println(Integer.toBinaryString(b & 255 | 256).substring(1));
    }

    protected static void binaryDump(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        if (bytes != null) {
            for (byte b : bytes) {
                sb.append(Integer.toBinaryString(b & 255 | 256).substring(1));
            }
        }
        System.out.println(sb.toString());
    }
}
