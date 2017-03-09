/*
 * PROJECT Mokka7 (fork of Snap7/Moka7)
 * 
 * Copyright (c) 2017 J.Zimmermann (comtel2000)
 * 
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Mokka7 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE whatever license you
 * decide to adopt.
 * 
 * Contributors: J.Zimmermann - Mokka7 fork
 * 
 */
package org.comtel2000.mokka7;

import org.comtel2000.mokka7.type.AreaType;

/**
 * Simulate a running light (bit pointer)
 *
 * @author comtel
 *
 */
public class WriteBitSample extends ClientRunner {

    final int db = 201;
    final int start = 0;

    public WriteBitSample() {
        super();
    }

    @Override
    public void call(S7Client client) throws Exception {
        for (int i = 0; i < 8; i++) {
            int c = i;
            client.writeBit(AreaType.DB, db, start, c, true);
            Thread.sleep(200);
        }
        for (int i = 0; i < 8; i++) {
            int c = i;
            client.writeBit(AreaType.DB, db, start, c, false);
            Thread.sleep(200);
        }
        int count = 0;
        boolean inc = true;
        for (int i = 0; i < 85; i++) {
            if (inc) {
                int temp = count;
                if (count > 0) {
                    client.writeBit(AreaType.DB, db, start, temp - 1, false);
                }
                client.writeBit(AreaType.DB, db, start, temp, true);
                count++;
            } else {
                int temp = count;
                if (count < 7) {
                    client.writeBit(AreaType.DB, db, start, temp + 1, false);
                }
                client.writeBit(AreaType.DB, db, start, temp, true);
                count--;
            }
            if (inc && count > 7) {
                inc = false;
                count = 6;
            } else if (!inc && count == -1) {
                inc = true;
                count = 1;
            }
            Thread.sleep(100);
        }
    }

    public static void main(String[] args) {
        new WriteBitSample();
    }
}
