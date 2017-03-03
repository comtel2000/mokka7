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

import static org.junit.Assert.assertEquals;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

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
}
