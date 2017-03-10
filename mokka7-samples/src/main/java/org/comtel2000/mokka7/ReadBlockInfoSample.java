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

import org.comtel2000.mokka7.block.BlockType;
import org.comtel2000.mokka7.block.S7BlockInfo;
import org.comtel2000.mokka7.block.S7BlockList;

public class ReadBlockInfoSample extends ClientRunner {

    public ReadBlockInfoSample() {
        super();
    }

    @Override
    public void call(S7Client client) throws Exception {

        S7BlockInfo info = client.getAgBlockInfo(BlockType.DB, 1);
        System.out.println(info);
        info = client.getAgBlockInfo(BlockType.DB, 2);
        System.out.println(info);
        info = client.getAgBlockInfo(BlockType.DB, 3);
        System.out.println(info);

        S7BlockInfo sfb = client.getAgBlockInfo(BlockType.SFB, 0);
        System.out.println(sfb);

        S7BlockList bl = client.getS7BlockList();
        System.out.println(bl);



    }

    public static void main(String[] args) {
        new ReadBlockInfoSample();
    }
}
