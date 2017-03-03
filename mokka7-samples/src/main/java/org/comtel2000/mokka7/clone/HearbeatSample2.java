/*
 * PROJECT Mokka7 (fork of Snap7/Moka7)
 * 
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
 *    J.Zimmermann    - Mokka7 fork
 * 
 */
package org.comtel2000.mokka7.clone;

import java.util.Arrays;

import org.comtel2000.mokka7.ClientRunner;
import org.comtel2000.mokka7.S7Client;
import org.comtel2000.mokka7.type.AreaType;
import org.comtel2000.mokka7.type.DataType;
import org.comtel2000.mokka7.util.S7;

/**
 * Clone bit of DB200.DBX34.0 to DB200.DBX34.1
 *
 * @author comtel
 *
 */
public class HearbeatSample2 extends ClientRunner {

    public HearbeatSample2() {
        super();
    }

    @Override
    public void call(S7Client client) throws Exception {
        boolean snycFailed = false;
        Arrays.fill(buffer, (byte) 0);
        for (int i = 0; i < 50; i++) {
            client.readArea(AreaType.DB, 200, 34 * 8 + 0, 1, DataType.BIT, buffer);
            boolean plcBit = S7.getBitAt(buffer, 0, 0);
            System.out.println("heartbeat: " + plcBit);
            if (snycFailed) {
                System.out.println("write: " + plcBit);
                S7.setBitAt(buffer, 0, 1, plcBit);
                client.writeArea(AreaType.DB, 200, 34 * 8 + 1, 1, DataType.BIT, buffer);
            }
            client.readArea(AreaType.DB, 200, 34, 1, DataType.BYTE, buffer);
            boolean plmBit = S7.getBitAt(buffer, 0, 1);
            snycFailed = plcBit != plmBit;
            if (snycFailed) {
                System.out.println("sync failed: " + plcBit + "/" + plmBit);
            }
            Thread.sleep(500);
        }
    }

    public static void main(String[] args) {
        new HearbeatSample2();
    }
}
