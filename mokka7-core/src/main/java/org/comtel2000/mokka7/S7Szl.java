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
 * @author Davide
 */
public class S7Szl {

    public byte[] data;
    public int dataSize;
    public int lenthdr;
    public int n_dr;

    public S7Szl(int bufferSize) {
        data = new byte[bufferSize];
    }

    protected void copy(byte[] src, int srcPos, int destPos, int size) {
        System.arraycopy(src, srcPos, data, destPos, size);
    }
}
