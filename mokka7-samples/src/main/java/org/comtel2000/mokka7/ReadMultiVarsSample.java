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

import org.junit.Assert;


public class ReadMultiVarsSample extends ClientRunner {

    final int db = 3;

    public ReadMultiVarsSample() {
        super();
    }

    @Override
    public void call(S7Client client) throws Exception {

        byte b0 = client.readByte(AreaType.S7AreaDB, db, 0);
        bitSet(b0);
        byte b1 = client.readByte(AreaType.S7AreaDB, db, 1);
        bitSet(b1);
        byte b2 = client.readByte(AreaType.S7AreaDB, db, 2);
        bitSet(b2);
        byte b3 = client.readByte(AreaType.S7AreaDB, db, 3);
        bitSet(b3);

        try (S7MultiVar mv = new S7MultiVar(client)) {
            mv.add(AreaType.S7AreaDB, db, 0, 1, DataType.S7WLByte);
            mv.add(AreaType.S7AreaDB, db, 1, 1, DataType.S7WLByte);
            mv.add(AreaType.S7AreaDB, db, 2, 1, DataType.S7WLByte);
            mv.add(AreaType.S7AreaDB, db, 3, 1, DataType.S7WLByte);

            mv.read();

            checkResult(mv.getResult(0).result);
            checkResult(mv.getResult(1).result);
            checkResult(mv.getResult(2).result);
            checkResult(mv.getResult(3).result);

            Assert.assertEquals(b0, mv.getResult(0).data[0]);
            Assert.assertEquals(b1, mv.getResult(1).data[0]);
            Assert.assertEquals(b2, mv.getResult(2).data[0]);
            Assert.assertEquals(b3, mv.getResult(3).data[0]);
        }

        try (S7MultiVar mv = new S7MultiVar(client)) {
            mv.add(AreaType.S7AreaDB, db, 0, 1, DataType.S7WLWord);
            mv.add(AreaType.S7AreaDB, db, 2, 1, DataType.S7WLWord);
            mv.read();

            checkResult(mv.getResult(0).result);
            checkResult(mv.getResult(1).result);

            Assert.assertEquals(b0, mv.getResult(0).data[0]);
            Assert.assertEquals(b1, mv.getResult(0).data[1]);
            Assert.assertEquals(b2, mv.getResult(1).data[0]);
            Assert.assertEquals(b3, mv.getResult(1).data[1]);

        }

        try (S7MultiVar mv = new S7MultiVar(client)) {
            mv.add(AreaType.S7AreaDB, db, 0, 2, DataType.S7WLWord);
            mv.read();

            checkResult(mv.getResult(0).result);

            Assert.assertEquals(b0, mv.getResult(0).data[0]);
            Assert.assertEquals(b1, mv.getResult(0).data[1]);
            Assert.assertEquals(b2, mv.getResult(0).data[2]);
            Assert.assertEquals(b3, mv.getResult(0).data[3]);
        }

        try (S7MultiVar mv = new S7MultiVar(client)) {
            mv.add(AreaType.S7AreaDB, db, 0, 1, DataType.S7WLDWord);
            mv.add(AreaType.S7AreaDB, db, 4, 1, DataType.S7WLDWord);
            mv.read();

            checkResult(mv.getResult(0).result);
            checkResult(mv.getResult(1).result);

            Assert.assertEquals(b0, mv.getResult(0).data[0]);
            Assert.assertEquals(b1, mv.getResult(0).data[1]);
            Assert.assertEquals(b2, mv.getResult(0).data[2]);
            Assert.assertEquals(b3, mv.getResult(0).data[3]);

            S7.hexDump(mv.getResult(0).data, System.out::println);
            S7.hexDump(mv.getResult(1).data, System.out::println);
        }

    }

    public static void main(String[] args) {
        new ReadMultiVarsSample();
    }
}
