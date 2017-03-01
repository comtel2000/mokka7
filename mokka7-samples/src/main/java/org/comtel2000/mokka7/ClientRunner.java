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

import java.util.BitSet;

import org.comtel2000.mokka7.ReturnCode;
import org.comtel2000.mokka7.S7Client;
import org.comtel2000.mokka7.S7CpInfo;
import org.comtel2000.mokka7.S7OrderCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ClientRunner {

    protected static byte[] buffer = new byte[1024];

    private static final Logger logger = LoggerFactory.getLogger(ClientRunner.class);

    private static final String host = "192.168.100.250";
    private static final int rack = 0;
    private static final int slot = 0;

    public ClientRunner() {
        this(host, rack, slot);
    }

    public ClientRunner(String host, int rack, int slot) {
        S7Client client = new S7Client();
        try {
            if (client.connect(host, rack, slot)) {
                S7OrderCode orderCode = client.getOrderCode();
                if (orderCode != null) {
                    logger.debug("Order Code\t: {}", orderCode.getCode());
                    logger.debug("Firmware\t: {}", orderCode.getFirmware());
                }

                S7CpInfo cpInfo = client.getCpInfo();
                if (cpInfo != null) {
                    logger.debug("Max PDU Length\t: {}", cpInfo.maxPduLength);
                    logger.debug("Max connections\t: {}", cpInfo.maxConnections);
                    logger.debug("Max MPI (bps)\t: {}", cpInfo.maxMpiRate);
                    logger.debug("Max Bus (bps)\t: {}", cpInfo.maxBusRate);
                }
                call(client);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            client.disconnect();
        }
    }

    public abstract void call(S7Client client) throws Exception;

    protected void checkResult(int result) {
        if (result != 0) {
            logger.error("(0x{}) {}", Integer.toHexString(result), ReturnCode.getErrorText(result));
        }
    }

    protected static void bitSet(byte b) {
        String hex = Integer.toHexString(b & 0x0FF).toUpperCase();
        System.out.println((hex.length() == 1 ? "0" + hex : hex) + ":\t" + BitSet.valueOf(new byte[] { b }));
    }

}
