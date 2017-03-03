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

import org.comtel2000.mokka7.ClientRunner;
import org.comtel2000.mokka7.S7Client;
import org.comtel2000.mokka7.type.AreaType;
import org.comtel2000.mokka7.util.S7;

/**
 * Clone bit of DB200.DBX34.0 to DB200.DBX34.1
 *
 * @author comtel
 *
 */
public class HearbeatSample4 extends ClientRunner {

    public HearbeatSample4() {
        super();
    }

    @Override
    public void call(S7Client client) throws Exception {
        boolean plcBit, clientBit;
        for (int i = 0; i < 1000; i++) {
            byte b = client.readByte(AreaType.DB, 200, 34);
            plcBit = S7.getBitAt(b, 0);
            clientBit = S7.getBitAt(b, 1);
            if (plcBit != clientBit) {
                System.out.println("update: " + plcBit + "/" + clientBit);
                boolean updateBit = plcBit;
                client.writeBit(AreaType.DB, 200, 34, 1, updateBit);
            } else {
                System.out.println("ok: " + plcBit + "/" + clientBit);
            }

            Thread.sleep(500);
        }
    }

    public static void main(String[] args) {
        new HearbeatSample4();
    }
}
