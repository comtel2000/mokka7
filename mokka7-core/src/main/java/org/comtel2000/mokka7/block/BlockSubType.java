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
 * Sub Block types
 *
 * @author comtel
 *
 */
public enum BlockSubType {

    /** Sub Block type OB */
    OB(0x08),

    /** Sub Block type DB */
    DB(0x0A),

    /** Sub Block type SDB */
    SDB(0x0B),

    /** Sub Block type FC */
    FC(0x0C),

    /** Sub Block type SFC */
    SFC(0x0D),

    /** Sub Block type FB */
    FB(0x0E),

    /** Sub Block type SFB */
    SFB(0x0F);

    private final byte value;

    BlockSubType(int value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }
}
