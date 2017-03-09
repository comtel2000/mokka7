/*
 * PROJECT Mokka7 (fork of Snap7/Moka7)
 *
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
 *    J.Zimmermann    - Mokka7 fork
 *
 */
package org.comtel2000.mokka7;

import java.util.BitSet;
import java.util.concurrent.TimeUnit;

import org.comtel2000.mokka7.block.S7CpInfo;
import org.comtel2000.mokka7.block.S7OrderCode;
import org.comtel2000.mokka7.metrics.MonitoredS7Client;
import org.comtel2000.mokka7.util.ReturnCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ClientRunner {

    protected static byte[] buffer = new byte[1024];

    protected static final Logger logger = LoggerFactory.getLogger(ClientRunner.class);

    private static final String host = "127.0.0.1";
    private static final int rack = 0;
    private static final int slot = 0;

    public ClientRunner() {
        this(host, rack, slot);
    }

    public ClientRunner(String host, int rack, int slot) {
        MonitoredS7Client client = new MonitoredS7Client();
        client.start(10, TimeUnit.SECONDS);
        long time = System.currentTimeMillis();
        try {
            if (client.connect(host, rack, slot)) {
                logger.info("connected in {}ms", System.currentTimeMillis() - time);
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
                time = System.currentTimeMillis();
                call(client);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            logger.info("finished after {}ms", (System.currentTimeMillis() - time));
            client.report();
            client.disconnect();
            client.close();
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
