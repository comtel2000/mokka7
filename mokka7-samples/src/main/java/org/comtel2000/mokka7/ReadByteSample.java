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
import org.comtel2000.mokka7.type.DataType;
import org.comtel2000.mokka7.util.S7;

public class ReadByteSample extends ClientRunner {

    final int db = 201;

    public ReadByteSample() {
        super();
    }

    @Override
    public void call(S7Client client) throws Exception {

        client.readArea(AreaType.DB, db, 0, 1, DataType.BYTE, buffer);
        bitSet(buffer[0]);
        S7.hexDump(buffer, 2, System.out::println);

        byte b = client.readByte(AreaType.DB, db, 0);
        bitSet(b);
        S7.hexDump(buffer, 2, System.out::println);

        client.readArea(AreaType.DB, db, 1, 1, DataType.BYTE, buffer);
        bitSet(buffer[0]);
        S7.hexDump(buffer, 2, System.out::println);

        b = client.readByte(AreaType.DB, db, 1);
        bitSet(b);
        S7.hexDump(buffer, 2, System.out::println);

        byte[] bytes = client.readBytes(AreaType.DB, 200, 0, 64);
        S7.hexDump(bytes, System.out::println);
    }

    public static void main(String[] args) {
        new ReadByteSample();
    }
}
