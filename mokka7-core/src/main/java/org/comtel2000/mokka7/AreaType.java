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
package org.comtel2000.mokka7;

/**
 *
 * @author comtel
 *
 */
public enum AreaType {

    /** */
    S7AreaPE(0x81),

    /** */
    S7AreaPA(0x82),

    /** */
    S7AreaMK(0x83),

    /** */
    S7AreaDB(0x84),

    /** */
    S7AreaCT(0x1C),

    /** */
    S7AreaTM(0x1D);

    private final byte value;

    AreaType(int value) {
        this.value = (byte) value;
    }

    public byte getValue() {
        return value;
    }
}
