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

import java.util.EnumMap;
import java.util.Map;

import org.comtel2000.mokka7.util.S7;

public class S7BlockList {

    private final Map<BlockSubType, Integer> map = new EnumMap<>(BlockSubType.class);

    public static S7BlockList of(byte[] src, int pos) {
        S7BlockList list = new S7BlockList();
        list.decode(src, pos);
        return list;
    }

    protected void decode(byte[] src, int pos) {
        for (int i = 0; i < 28; i += 4) {
            add(S7.getStringAt(src, pos + i, 2), S7.getWordAt(src, pos + i + 2));
        }
    }

    private void add(String blk, int count) {
        BlockSubType type = BlockSubType.of((byte) Integer.parseInt(blk, 16));
        map.put(type, count);
    }

    public int getSize(BlockType type) {
        return map.getOrDefault(type, 0);
    }

    @Override
    public String toString() {
        return "S7BlockList [map=" + map + "]";
    }

}
