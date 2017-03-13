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

    private final S7Client client;
    private int count = 0;

    private final S7DataItem[] items = new S7DataItem[S7Client.MAX_VARS];

    public S7MultiVar(S7Client client) {
        this.client = client;
    }

    public boolean add(AreaType area,DataType type, int db, int start, int amount) {
        if (count >= S7Client.MAX_VARS) {
            return false;
        }
        S7DataItem item = new S7DataItem(area,type, db, start, amount);
        item.data = new byte[item.amount];
        return add(item);
    }

    public boolean add(AreaType area, DataType type, int db, int start, int amount, byte[] data) {
        if (count >= items.length) {
            return false;
        }
        S7DataItem item = new S7DataItem(area,type, db, start, amount);
        if (data == null || data.length < item.amount) {
            throw new IllegalArgumentException("data length must be equal or greater than: " + item.amount);
        }
        item.data = data;
        return add(item);
    }

    public boolean add(S7DataItem item) {
        if (count >= items.length) {
            return false;
        }
        items[count] = item;
        count++;
        return true;
    }

    public boolean add(S7DataItem[] data) {
        if (count >= items.length || count + data.length >= items.length) {
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
