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

import com.google.common.primitives.Ints;

public class WriteMultiVarsSample extends ClientRunner {

    final int db = 201;

    public WriteMultiVarsSample() {
        super();
    }

    @Override
    public void call(S7Client client) throws Exception {

        try (S7MultiVar mv = new S7MultiVar(client)) {
            mv.add(AreaType.DB, db, 0, 1, DataType.BYTE, new byte[] { 0x09 });
            mv.add(AreaType.DB, db, 1, 1, DataType.BYTE, new byte[] { 0x08 });
            mv.add(AreaType.DB, db, 2, 1, DataType.BYTE, new byte[] { 0x07 });
            mv.add(AreaType.DB, db, 3, 1, DataType.BYTE, new byte[] { 0x06 });

            mv.add(AreaType.DB, db, 4, 1, DataType.BYTE, new byte[] { 0x09 });
            mv.add(AreaType.DB, db, 5, 1, DataType.BYTE, new byte[] { 0x08 });
            mv.add(AreaType.DB, db, 6, 1, DataType.BYTE, new byte[] { 0x07 });
            mv.add(AreaType.DB, db, 7, 1, DataType.BYTE, new byte[] { 0x06 });

            mv.write();
        }

        byte b = client.readByte(AreaType.DB, db, 0);
        bitSet(b);
        b = client.readByte(AreaType.DB, db, 1);
        bitSet(b);
        b = client.readByte(AreaType.DB, db, 2);
        bitSet(b);
        b = client.readByte(AreaType.DB, db, 3);
        bitSet(b);
        Long sum = client.readLong(AreaType.DB, db, 0);
        System.err.println(Ints.fromByteArray(new byte[] { 0x09, 0x08, 0x07, 0x06 }));
        System.err.println(sum);

        // try (S7MultiVar mv = new S7MultiVar(client)) {
        //
        // mv.add(AreaType.DB, db, 4, 1, DataType.S7WLDWord, Ints.toByteArray(151521030));
        // mv.add(AreaType.DB, db, 8, 1, DataType.S7WLDWord, Ints.toByteArray(151521030));
        //
        // mv.write();
        //
        // }
        //
        // Long l = client.readLong(AreaType.DB, db, 4);
        // System.err.println(l);
        // l = client.readLong(AreaType.DB, db, 8);
        // System.err.println(l);
    }

    public static void main(String[] args) {
        new WriteMultiVarsSample();
    }
}
