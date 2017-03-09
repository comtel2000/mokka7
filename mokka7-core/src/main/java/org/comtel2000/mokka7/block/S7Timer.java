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
package org.comtel2000.mokka7.block;

import java.time.Duration;

import org.comtel2000.mokka7.util.S7;

public class S7Timer {

    public Duration pt;
    public Duration et;

    public boolean input;
    public boolean q;

    public S7Timer() {}

    public static S7Timer of(byte[] buffer, int pos) {
        S7Timer timer = new S7Timer();
        timer.decode(buffer, pos);
        return timer;
    }

    protected void decode(byte[] buffer, int pos) {
        if (buffer.length < pos + 12) {
            return;
        }
        this.pt = Duration.ofMillis(S7.getDIntAt(buffer, pos));
        this.et = Duration.ofMillis(S7.getDIntAt(buffer, pos + 4));

        this.input = S7.getBitAt(buffer[pos + 8], 0x01);
        this.q = S7.getBitAt(buffer[pos + 8], 0x02);
    }

    @Override
    public String toString() {
        return "S7Timer [pt=" + pt + ", et=" + et + ", input=" + input + ", q=" + q + "]";
    }

}
