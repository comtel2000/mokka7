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

import java.util.Arrays;

import org.comtel2000.mokka7.block.S7DataItem;
import org.comtel2000.mokka7.exception.S7Exception;
import org.comtel2000.mokka7.type.AreaType;
import org.comtel2000.mokka7.type.DataType;
import org.comtel2000.mokka7.util.ReturnCode;

/**
 *
 * @author comtel
 *
 */
public class S7MultiVar implements ReturnCode, AutoCloseable {

    private S7Client client;
    private int count = 0;

    private final S7DataItem[] items = new S7DataItem[S7Client.MAX_VARS];

    public S7MultiVar(S7Client client) {
        this.client = client;
    }

    private S7DataItem adjustWordLength(AreaType area, int db, int start, int amount, DataType type) {
        int wordSize = DataType.getByteLength(type);
        if (wordSize == 0) {
            return null;
        }
        if (area == AreaType.CT) {
            type = DataType.COUNTER;
        } else if (area == AreaType.TM) {
            type = DataType.TIMER;
        }

        if (type == DataType.BIT) {
            amount = 1; // Only 1 bit can be transferred at time
        } else if ((type != DataType.COUNTER) && (type != DataType.TIMER)) {
            amount = amount * wordSize;
            start = start * 8;
        }
        S7DataItem item = new S7DataItem();
        item.area = area;
        item.db = db;
        item.start = start;
        item.amount = amount;
        item.type = type;
        item.result = ERR_ITEM_NOT_AVAILABLE;
        return item;
    }

    public boolean add(AreaType area, int db, int start, int amount, DataType type) {
        if (count >= S7Client.MAX_VARS) {
            return false;
        }
        S7DataItem item = adjustWordLength(area, db, start, amount, type);
        if (item == null) {
            return false;
        }
        item.data = new byte[item.amount];
        return add(item);
    }

    public boolean add(AreaType area, int db, int start, int amount, DataType type, byte[] data) {
        if (count >= S7Client.MAX_VARS) {
            return false;
        }
        S7DataItem item = adjustWordLength(area, db, start, amount, type);
        if (item == null) {
            return false;
        }
        if (data == null || data.length < item.amount) {
            throw new IllegalArgumentException("data length must be equal or greater than: " + item.amount);
        }
        item.data = data;
        return add(item);
    }

    public boolean add(S7DataItem item) {
        if (count >= S7Client.MAX_VARS) {
            return false;
        }
        items[count] = item;
        count++;
        return true;
    }

    public boolean add(S7DataItem[] data) {
        if (count >= S7Client.MAX_VARS || count + data.length >= S7Client.MAX_VARS) {
            return false;
        }
        for (S7DataItem item : data) {
            items[count] = item;
            count++;
        }
        return true;
    }

    public boolean read() throws S7Exception {
        if (count < 1) {
            throw new S7Exception(ERR_FUNCTION_REFUSED, ReturnCode.getErrorText(ERR_FUNCTION_REFUSED));
        }
        return client.readMultiVars(items, count);
    }

    public boolean write() throws S7Exception {
        if (count < 1) {
            throw new S7Exception(ERR_FUNCTION_REFUSED, ReturnCode.getErrorText(ERR_FUNCTION_REFUSED));
        }
        return client.writeMultiVars(items, count);
    }

    public S7DataItem getResult(int pos) {
        if (pos < 0 || pos >= count) {
            return null;
        }
        return items[pos];
    }


    @Override
    public void close() {
        count = 0;
        Arrays.fill(items, null);
    }
}
