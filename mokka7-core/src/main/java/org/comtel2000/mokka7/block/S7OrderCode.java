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
package org.comtel2000.mokka7.block;

import org.comtel2000.mokka7.util.S7;

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

    public String getCode() {
        return code;
    }

    public String getFirmware() {
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
