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

/**
 *
 * @author comtel
 *
 */
public enum BlockType {

    /** Organization Block */
    OB(0x38),

    /** User Data Block */
    DB(0x41),

    /** System Data Block */
    SDB(0x42),

    /** User Function */
    FC(0x43),

    /** System Function */
    SFC(0x44),

    /** User Function Block */
    FB(0x45),

    /** System Function Block */
    SFB(0x46);

    private final byte value;

    BlockType(int value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }
}
