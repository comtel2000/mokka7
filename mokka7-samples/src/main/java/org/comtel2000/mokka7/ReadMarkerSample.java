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
package org.comtel2000.mokka7;

import org.comtel2000.mokka7.type.AreaType;
import org.comtel2000.mokka7.type.DataType;
import org.junit.Assert;

public class ReadMarkerSample extends ClientRunner {


    public ReadMarkerSample() {
        super();
    }

    @Override
    public void call(S7Client client) throws Exception {

        client.readArea(AreaType.MK, 0, 0, 1, DataType.BYTE, buffer);
        bitSet(buffer[0]);

        byte b = client.readByte(AreaType.MK, 0, 0);
        bitSet(b);

        Assert.assertEquals(buffer[0], b);

        client.readArea(AreaType.MK, 0, 1, 1, DataType.BYTE, buffer);
        bitSet(buffer[0]);

        Assert.assertEquals(buffer[0], b);

        b = client.readByte(AreaType.MK, 0, 1);
        bitSet(b);

        boolean flag = client.readBit(AreaType.MK, 0, 12, 0);
        System.out.println("M12.0=" + flag);

        flag = client.readBit(AreaType.MK, 0, 12, 1);
        System.out.println("M12.1=" + flag);

        flag = client.readBit(AreaType.MK, 0, 12, 2);
        System.out.println("M12.2=" + flag);
    }

    public static void main(String[] args) {
        new ReadMarkerSample();
    }
}
