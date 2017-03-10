/*
 * PROJECT Mokka7 (fork of Snap7/Moka7)
 *
 * Copyright (c) 2013,2016 Davide Nardella Copyright (c) 2017 J.Zimmermann (comtel2000)
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Mokka7 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE whatever license you
 * decide to adopt.
 *
 * Contributors: Davide Nardella - initial API and implementation J.Zimmermann - Mokka7 fork
 *
 */
package org.comtel2000.mokka7.block;

/**
 * Block languages
 *
 * @author comtel
 *
 */
public enum BlockLang {

    AWL(0x01),

    KOP(0x02),

    FUP(0x03),

    SCL(0x04),

    DB(0x05),

    GRAPH(0x06);

    private final byte value;

    BlockLang(int value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }

    public static BlockLang of(byte v){
        for (BlockLang type : values()){
            if (type.value == v){
                return type;
            }
        }
        return null;
    }
}
