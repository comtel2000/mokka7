/*
 * PROJECT Mokka7 (fork of Moka7)
 *
 * Copyright (C) 2013, 2016 Davide Nardella All rights reserved.
 * Copyright (C) 2017 J.Zimmermann All rights reserved.
 *
 * SNAP7 is free software: you can redistribute it and/or modify it under the terms of the Lesser
 * GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or under EPL Eclipse Public License 1.0.
 *
 * This means that you have to chose in advance which take before you import the library into your
 * project.
 *
 * SNAP7 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE whatever license you
 * decide to adopt.
 */
package org.comtel2000.mokka7;

import java.util.Arrays;

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
        if (area == AreaType.S7AreaCT) {
            type = DataType.S7WLCounter;
        } else if (area == AreaType.S7AreaTM) {
            type = DataType.S7WLTimer;
        }

        if (type == DataType.S7WLBit) {
            amount = 1; // Only 1 bit can be transferred at time
        } else if ((type != DataType.S7WLCounter) && (type != DataType.S7WLTimer)) {
            amount = amount * wordSize;
            start = start * 8;
        }
        S7DataItem item = new S7DataItem();
        item.area = area;
        item.db = db;
        item.start = start;
        item.amount = amount;
        item.type = type;
        item.result = ERR_CLI_ITEM_NOT_AVAILABLE;
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

    public boolean add(S7DataItem item){
        if (count >= S7Client.MAX_VARS) {
            return false;
        }
        items[count] = item;
        count++;
        return true;
    }

    public boolean add(S7DataItem[] data){
        if (count >= S7Client.MAX_VARS || count + data.length >= S7Client.MAX_VARS) {
            return false;
        }
        for (S7DataItem item : data) {
            items[count] = item;
            count++;
        }
        return true;
    }

    public int read() {
        if (count < 1) {
            return ERR_CLI_FUNCTION_REFUSED;
        }
        return client.readMultiVars(items, count);
    }

    public int write() {
        if (count < 1) {
            return ERR_CLI_FUNCTION_REFUSED;
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
