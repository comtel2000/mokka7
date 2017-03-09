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
package org.comtel2000.mokka7;

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.comtel2000.mokka7.util.S7;
import org.junit.Assert;
import org.junit.Test;

public class S7Test {

    @Test
    public void testBitAt() {

        byte[] buffer = new byte[2];
        Arrays.fill(buffer, (byte) 0x00);
        for (int i = 0; i < 7; i++) {
            assertEquals(Boolean.FALSE, S7.getBitAt(buffer, 0, i));
            S7.setBitAt(buffer, 0, i, true);
            assertEquals(Boolean.TRUE, S7.getBitAt(buffer, 0, i));
            S7.setBitAt(buffer, 0, i, false);
            assertEquals(Boolean.FALSE, S7.getBitAt(buffer, 0, i));
        }
        assertEquals((byte) 0, buffer[0]);

        S7.setBitAt(buffer, 0, 8, true);
        assertEquals(Boolean.TRUE, S7.getBitAt(buffer, 0, 7));

        S7.setBitAt(buffer, 0, -1, true);
        assertEquals(Boolean.TRUE, S7.getBitAt(buffer, 0, 0));
    }

    @Test
    public void testBytesAt() {

        byte[] buffer = new byte[32];
        Arrays.fill(buffer, (byte) 0x00);
        Assert.assertArrayEquals(buffer, S7.getBytesAt(buffer, 0, buffer.length));
        S7.setBytesAt(buffer, 0, new byte[] { 0, 1, 2, 3, 4 });
        Assert.assertArrayEquals(new byte[] { 0, 1, 2, 3, 4 }, S7.getBytesAt(buffer, 0, 5));
        S7.setBytesAt(buffer, 5, new byte[] { 0, 1, 2, 3, 4 });
        Assert.assertArrayEquals(new byte[] { 0, 1, 2, 3, 4 }, S7.getBytesAt(buffer, 5, 5));

    }

    @Test
    public void testWordAt() {

        byte[] buffer = new byte[4];
        Arrays.fill(buffer, (byte) 0x00);
        assertEquals(0, S7.getWordAt(buffer, 0));
        S7.setWordAt(buffer, 0, Short.MAX_VALUE);
        assertEquals(Short.MAX_VALUE, S7.getWordAt(buffer, 0));
        S7.setWordAt(buffer, 0, 0);
        assertEquals(0, S7.getWordAt(buffer, 0));
        S7.setWordAt(buffer, 0, 128);
        assertEquals(128, S7.getWordAt(buffer, 0));
        S7.setWordAt(buffer, 0, 0);
        assertEquals(0, S7.getWordAt(buffer, 0));
    }

    @Test
    public void testDIntAt() {

        byte[] buffer = new byte[4];
        Arrays.fill(buffer, (byte) 0x00);
        assertEquals(0, S7.getDIntAt(buffer, 0));
        S7.setDIntAt(buffer, 0, Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, S7.getDIntAt(buffer, 0));
        S7.setDIntAt(buffer, 0, 0);
        assertEquals(0, S7.getDIntAt(buffer, 0));
        S7.setDIntAt(buffer, 0, 128);
        assertEquals(128, S7.getDIntAt(buffer, 0));
        S7.setDIntAt(buffer, 0, Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, S7.getDIntAt(buffer, 0));
    }

    @Test
    public void testDWordAt() {

        byte[] buffer = new byte[8];
        Arrays.fill(buffer, (byte) 0x00);
        assertEquals(0, S7.getDWordAt(buffer, 0));
        S7.setDWordAt(buffer, 0, Integer.MAX_VALUE);
        assertEquals(Integer.MAX_VALUE, S7.getDWordAt(buffer, 0));
        S7.setDWordAt(buffer, 0, 0);
        assertEquals(0, S7.getDWordAt(buffer, 0));
        S7.setDWordAt(buffer, 0, 128);
        assertEquals(128, S7.getDWordAt(buffer, 0));
        S7.setDWordAt(buffer, 4, Integer.MIN_VALUE);
        assertEquals(Integer.MIN_VALUE, S7.getDWordAt(buffer, 4));
    }

    @Test
    public void testFloatAt() {

        byte[] buffer = new byte[4];
        Arrays.fill(buffer, (byte) 0x00);
        assertEquals(0.0, S7.getFloatAt(buffer, 0), 0.01);
        S7.setFloatAt(buffer, 0, Float.MAX_VALUE);
        assertEquals(Float.MAX_VALUE, S7.getFloatAt(buffer, 0), 0.01);
        S7.setFloatAt(buffer, 0, 0);
        assertEquals(0, S7.getFloatAt(buffer, 0), 0.01);
        S7.setFloatAt(buffer, 0, 128);
        assertEquals(128, S7.getFloatAt(buffer, 0), 0.01);
        S7.setFloatAt(buffer, 0, Float.MIN_VALUE);
        assertEquals(Float.MIN_VALUE, S7.getFloatAt(buffer, 0), 0.01);
    }

    @Test
    public void testStringAt() {

        byte[] buffer = new byte[32];
        Arrays.fill(buffer, (byte) 0x00);
        S7.setStringAt(buffer, 0, " ");
        assertEquals(" ", S7.getStringAt(buffer, 0, 1));
        S7.setStringAt(buffer, 0, "DEMO");
        assertEquals("DEMO", S7.getStringAt(buffer, 0, 4));
        S7.setStringAt(buffer, 0, "DEMO_\u20ac", StandardCharsets.UTF_8);
        assertEquals("DEMO_\u20ac", S7.getStringAt(buffer, 0, 8, StandardCharsets.UTF_8));
        S7.setStringAt(buffer, 0, "");
        assertEquals("", S7.getStringAt(buffer, 0, 0));
    }

    @Test
    public void testSetDateAt() {
        // 1987-04-15
        long enc = 20 * 60;
        LocalDateTime date = LocalDateTime.of(1984, 1, 1, 0, 0).plusSeconds(enc * 86400);

        long S7_TIME_OFFSET = 441763200000L;
        long millis = enc * 86400000L + S7_TIME_OFFSET;
        Date date2 = new Date(millis);
        assertEquals(date.toInstant(ZoneOffset.UTC), date2.toInstant());

        LocalDateTime date3 = LocalDateTime.ofEpochSecond(millis / 1000, 0, ZoneOffset.UTC);
        assertEquals(date, date3);

        byte[] buffer = new byte[32];
        Arrays.fill(buffer, (byte) 0);
        LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(ZoneOffset.UTC), ZoneOffset.systemDefault());
        S7.setDateTimeAt(buffer, 0, ldt);

        byte[] buffer1 = new byte[32];
        Arrays.fill(buffer1, (byte) 0);
        S7.setDateAt(buffer1, 0, date2);
        Assert.assertArrayEquals(buffer, buffer1);
    }

    @Test
    public void testSetGetDateTimeAt() {
        LocalDateTime date = LocalDateTime.now();
        byte[] buffer = new byte[32];
        Arrays.fill(buffer, (byte) 0);
        S7.setDateTimeAt(buffer, 0, date);
        LocalDateTime dateRet = S7.getDateTimeAt(buffer, 0);
        assertEquals(date.minusNanos(date.getLong(ChronoField.NANO_OF_SECOND)), dateRet);
    }

    @Test
    public void testSetGetDateAt() {
        Calendar cal = new GregorianCalendar();
        cal.set(Calendar.MILLISECOND, 0);
        Date date = cal.getTime();
        byte[] buffer = new byte[32];
        Arrays.fill(buffer, (byte) 0);
        S7.setDateAt(buffer, 0, date);
        Date dateRet = S7.getDateAt(buffer, 0);
        assertEquals(date, dateRet);
    }

    @Test
    public void testSetGetS7StringAt() {
        String value = "\u001eDEMO";
        byte[] buffer = new byte[32];
        Arrays.fill(buffer, (byte) 0);
        S7.setS7StringAt(buffer, 0, 10, value);
        assertEquals(value, S7.getS7StringAt(buffer, 0));
    }

}
