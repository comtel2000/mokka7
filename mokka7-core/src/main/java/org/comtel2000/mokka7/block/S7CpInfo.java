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
public class S7CpInfo {

    public int maxBusRate;
    public int maxConnections;
    public int maxMpiRate;
    public int maxPduLength;

    public static S7CpInfo of(byte[] src, int pos) {
        S7CpInfo info = new S7CpInfo();
        info.decode(src, pos);
        return info;
    }

    protected void decode(byte[] src, int pos) {
        maxPduLength = S7.getShortAt(src, 2);
        maxConnections = S7.getShortAt(src, 4);
        maxMpiRate = S7.getDIntAt(src, 6);
        maxBusRate = S7.getDIntAt(src, 10);
    }

    @Override
    public String toString() {
        return "S7CpInfo [maxBusRate=" + maxBusRate + ", maxConnections=" + maxConnections + ", maxMpiRate=" + maxMpiRate + ", maxPduLength=" + maxPduLength
                + "]";
    }
}
