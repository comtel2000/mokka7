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

import java.util.Objects;

import org.comtel2000.mokka7.type.AreaType;
import org.comtel2000.mokka7.type.DataType;
import org.comtel2000.mokka7.util.ReturnCode;

/**
 *
 * @author comtel
 *
 */
public class S7DataItem {

    public AreaType area;
    public DataType type;
    public int result;
    public int db;
    public int start;
    public int amount;
    public byte[] data;

    public S7DataItem(AreaType area, DataType type, int db, int start, int amount) {
        this(area, type, db, start, amount, null);
    }

    public S7DataItem(AreaType area, DataType type, int db, int start, int amount, byte[] data) {
        int wordSize = DataType.getByteLength(Objects.requireNonNull(type));
        switch (Objects.requireNonNull(area)) {
            case CT:
                this.type = DataType.COUNTER;
                break;
            case TM:
                this.type = DataType.TIMER;
                break;
            default:
                this.type = type;
                break;
        }

        switch (this.type) {
            case BIT:
                // Only 1 bit can be transferred at time
                this.amount = 1;
                this.start = start;
            case COUNTER:
            case TIMER:
                this.amount = amount;
                this.start = start;
                break;
            default:
                // convert to Bit request
                this.amount = amount * wordSize;
                this.start = start * 8;
                break;
        }

        this.area = area;
        this.db = db;
        this.result = ReturnCode.ERR_ITEM_NOT_AVAILABLE;
        this.data = data != null ? data : new byte[this.amount];
    }

}
