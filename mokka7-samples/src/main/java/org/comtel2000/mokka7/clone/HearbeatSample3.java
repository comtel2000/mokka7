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

import org.comtel2000.mokka7.AreaType;
import org.comtel2000.mokka7.ClientRunner;
import org.comtel2000.mokka7.DataType;
import org.comtel2000.mokka7.S7;
import org.comtel2000.mokka7.S7Client;

/**
 * Clone bit of DB200.DBX34.0 to DB200.DBX34.1
 *
 * @author comtel
 *
 */
public class HearbeatSample3 extends ClientRunner {

    public HearbeatSample3() {
        super();
    }

    @Override
    public void call(S7Client client) throws Exception {
        boolean plcBit, clientBit;
        for (int i = 0; i < 1000; i++) {
            client.readArea(AreaType.S7AreaDB, 200, 34, 1, DataType.S7WLByte, buffer);
            plcBit = S7.getBitAt(buffer, 0, 0);
            clientBit = S7.getBitAt(buffer, 0, 1);
            if (plcBit != clientBit) {
                System.err.println("update: " + plcBit + "/" + clientBit);
                S7.setBitAt(buffer, 0, 1, plcBit);
                client.writeArea(AreaType.S7AreaDB, 200, 34, 1, DataType.S7WLByte, buffer);
            }
            Thread.sleep(500);
        }

        // client.WriteArea(S7.S7AreaDB, 200, 34, 1, new
        // byte[]{0x00});
    }

    public static void main(String[] args) {
        new HearbeatSample3();
    }
}
