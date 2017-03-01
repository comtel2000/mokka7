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
 * @author comtel
 */
public class S7OrderCode {

    public String code;
    public int v1;
    public int v2;
    public int v3;

    public static S7OrderCode of(byte[] src, int pos, int size) {
        S7OrderCode oc = new S7OrderCode();
        oc.update(src, pos, size);
        return oc;
    }

    public String getCode(){
        return code;
    }

    public String getFirmware(){
        return String.format("%s.%s.%s", v1, v2, v3);
    }

    protected void update(byte[] src, int pos, int size) {
        code = S7.getStringAt(src, pos + 2, 20);
        v1 = src[pos + size - 3];
        v2 = src[pos + size - 2];
        v3 = src[pos + size - 1];
    }

    @Override
    public String toString() {
        return "S7OrderCode [code=" + code + ", v1=" + v1 + ", v2=" + v2 + ", v3=" + v3 + "]";
    }
}
