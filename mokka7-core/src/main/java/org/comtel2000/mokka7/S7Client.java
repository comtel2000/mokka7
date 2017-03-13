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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.comtel2000.mokka7.block.BlockType;
import org.comtel2000.mokka7.block.PlcCpuStatus;
import org.comtel2000.mokka7.block.S7BlockInfo;
import org.comtel2000.mokka7.block.S7BlockList;
import org.comtel2000.mokka7.block.S7CpInfo;
import org.comtel2000.mokka7.block.S7CpuInfo;
import org.comtel2000.mokka7.block.S7DataItem;
import org.comtel2000.mokka7.block.S7OrderCode;
import org.comtel2000.mokka7.block.S7Protection;
import org.comtel2000.mokka7.block.S7Szl;
import org.comtel2000.mokka7.exception.S7Exception;
import org.comtel2000.mokka7.type.AreaType;
import org.comtel2000.mokka7.type.DataType;
import org.comtel2000.mokka7.util.ReturnCode;
import org.comtel2000.mokka7.util.S7;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Dave Nardella
 * @author comtel
 */
public class S7Client implements Client, ReturnCode {

    private static final Logger logger = LoggerFactory.getLogger(S7Client.class);

    private int pduLength = 0;

    private boolean connected = false;

    private S7Config config;

    private Socket tcpSocket;
    private BufferedInputStream inStream;
    private OutputStream outStream;

    private byte lastPDUType;

    private final byte[] pdu = new byte[2048];

    private int recvTimeout = RECV_TIMEOUT;

    private final int MAX_INC_COUNT = 65535;

    private final AtomicInteger counter = new AtomicInteger(-1);

    public S7Client() {
        this(new S7Config());
    }

    public S7Client(S7Config config) {
        Arrays.fill(buffer, (byte) 0);
        this.setConfig(config);
    }

    public S7Config getConfig() {
        return config;
    }

    public void setConfig(S7Config config) {
        this.config = Objects.requireNonNull(config);
    }

    private int incrementAndGet() {
        if (counter.compareAndSet(MAX_INC_COUNT, -1)) {
            return 0;
        }
        return counter.incrementAndGet();
    }

    @Override
    public boolean clearSessionPassword() throws S7Exception {
        if (sendPacket(S7_CLR_PWD)) {
            int length = recvIsoPacket();
            if (length < 31) {
                throw buildException(ISO_INVALID_PDU);
            }
            if (S7.getWordAt(pdu, 27) != 0) {
                throw buildException(S7_FUNCTION_ERROR);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean connect() throws S7Exception {
        logger.debug("try to connect to {}:{}", config.getHost(), config.getPort());
        if (connected) {
            disconnect();
        }
        try {
            openTcpConnect(5000);
            openIsoConnect();
            updateNegotiatePduLength();
        } catch (S7Exception e) {
            disconnect();
            throw e;
        }
        return connected = true;
    }

    @Override
    public boolean connect(String address, int rack, int slot) throws S7Exception {
        config.setHost(address);
        config.setRack(rack);
        config.setSlot(slot);
        config.setRemoteTSAP((config.getType().getValue() << 8) + (rack * 0x20) + slot);
        return connect();
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    public int getRecvTimeout() {
        return recvTimeout;
    }

    public void setRecvTimeout(int recvTimeout) {
        this.recvTimeout = recvTimeout;
    }

    @Override
    public int dbGet(int db, byte[] buffer) throws S7Exception {
        S7BlockInfo block = getAgBlockInfo(BlockType.DB, db);
        if (block != null) {
            int sizeToRead = block.mc7Size;
            // Checks the room
            if (sizeToRead > buffer.length) {
                logger.error("buffer size to small ({}/{})", sizeToRead, buffer.length);
                throw buildException(S7_BUFFER_TOO_SMALL);
            }
            if (readArea(AreaType.DB, db, 0, sizeToRead, DataType.BYTE, buffer) > 0) {
                return sizeToRead;
            }
        }
        throw buildException(S7_FUNCTION_ERROR);
    }

    @Override
    public int dbFill(int db, byte fill) throws S7Exception {
        S7BlockInfo block = getAgBlockInfo(BlockType.DB, db);
        if (block != null) {
            byte[] buffer = new byte[block.mc7Size];
            Arrays.fill(buffer, fill);
            if (writeArea(AreaType.DB, db, 0, buffer.length, DataType.BYTE, buffer)) {
                return buffer.length;
            }
        }
        throw buildException(S7_FUNCTION_ERROR);
    }

    @Override
    public void disconnect() {
        logger.debug("disconnecting..");
        closeSilently(inStream);
        closeSilently(outStream);
        closeSilently(tcpSocket);
        connected = false;
        pduLength = 0;
    }

    @Override
    public S7BlockInfo getAgBlockInfo(BlockType blockType, int blockNumber) throws S7Exception {
        // Block Type
        S7_BI[30] = blockType.getValue();
        // Block Number
        S7_BI[31] = (byte) ((blockNumber / 10000) + 0x30);
        blockNumber = blockNumber % 10000;
        S7_BI[32] = (byte) ((blockNumber / 1000) + 0x30);
        blockNumber = blockNumber % 1000;
        S7_BI[33] = (byte) ((blockNumber / 100) + 0x30);
        blockNumber = blockNumber % 100;
        S7_BI[34] = (byte) ((blockNumber / 10) + 0x30);
        blockNumber = blockNumber % 10;
        S7_BI[35] = (byte) ((blockNumber) + 0x30);

        if (sendPacket(S7_BI)) {
            int length = recvIsoPacket();
            if (length < 28) {
                throw buildException(ISO_INVALID_PDU);
            }
            if (S7.getWordAt(pdu, 27) != 0) {
                throw buildException(S7.getWordAt(pdu, 27));
            }
            return S7BlockInfo.of(pdu, 42);
        }
        throw buildException(S7_FUNCTION_ERROR);
    }

    @Override
    public S7BlockList getS7BlockList() throws S7Exception {

        S7.setSwapWordAt(S7_BL, 11, incrementAndGet());
        if (sendPacket(S7_BL)) {
            int length = recvIsoPacket();
            if (length < 28) {
                throw buildException(ISO_INVALID_PDU);
            }
            if (S7.getWordAt(pdu, 27) != 0) {
                throw buildException(S7.getWordAt(pdu, 27));
            }
            return S7BlockList.of(pdu, 33);
        }
        throw buildException(S7_FUNCTION_ERROR);
    }

    @Override
    public S7CpInfo getCpInfo() throws S7Exception {
        S7Szl szl = getSzl(0x0131, 0x0001, 1024);
        return S7CpInfo.of(szl.data, 0);
    }

    @Override
    public S7CpuInfo getCpuInfo() throws S7Exception {
        S7Szl szl = getSzl(0x001C, 0x0000, 1024);
        return S7CpuInfo.of(szl.data, 0);
    }

    @Override
    public S7OrderCode getOrderCode() throws S7Exception {
        S7Szl szl = getSzl(0x0011, 0x0000, 1024);
        return S7OrderCode.of(szl.data, 0, szl.dataSize);
    }

    @Override
    public LocalDateTime getPlcDateTime() throws S7Exception {
        if (sendPacket(S7_GET_DT)) {
            int length = recvIsoPacket();
            if (length < 31) {
                throw buildException(ISO_INVALID_PDU);
            }

            if (S7.getWordAt(pdu, 27) != 0) {
                throw buildException(S7.getWordAt(pdu, 27));
            }

            if (pdu[29] == (byte) 0xff) {
                return S7.getDateTimeAt(pdu, 35);
            }
        }
        throw buildException(S7_FUNCTION_ERROR);
    }

    @Override
    public PlcCpuStatus getPlcStatus() throws S7Exception {
        if (sendPacket(S7_GET_STAT)) {
            int length = recvIsoPacket();
            if (length < 31) {
                throw buildException(ISO_INVALID_PDU);
            }
            if (S7.getWordAt(pdu, 27) != 0) {
                throw buildException(S7.getWordAt(pdu, 27));
            }
            return PlcCpuStatus.valueOf(pdu[44]);
        }
        throw buildException(S7_FUNCTION_ERROR);
    }

    @Override
    public S7Protection getProtection() throws S7Exception {
        S7Szl szl = getSzl(0x0232, 0x0004, 256);
        return S7Protection.of(szl.data);
    }

    private void openTcpConnect(int timeout) throws S7Exception {
        try {
            tcpSocket = new Socket();
            tcpSocket.connect(new InetSocketAddress(config.getHost(), config.getPort()), timeout);
            tcpSocket.setTcpNoDelay(true);
            tcpSocket.setSoTimeout(recvTimeout);
            inStream = new BufferedInputStream(tcpSocket.getInputStream());
            outStream = new BufferedOutputStream(tcpSocket.getOutputStream());
        } catch (IOException e) {
            throw buildException(TCP_CONNECTION_FAILED, e);
        }
    }

    private void openIsoConnect() throws S7Exception {

        int locTSAP = config.getLocalTSAP() & 0x0000FFFF;
        int remTSAP = config.getRemoteTSAP() & 0x0000FFFF;

        ISO_CR[16] = (byte) (locTSAP >> 8);
        ISO_CR[17] = (byte) (locTSAP & 0x00FF);
        ISO_CR[20] = (byte) (remTSAP >> 8);
        ISO_CR[21] = (byte) (remTSAP & 0x00FF);

        if (sendPacket(ISO_CR)) {
            int length = recvIsoPacket();
            if (length != 22) {
                logger.warn("invalid PDU length: {} ({})", length, 22);
                if (length == 33) {
                    return;
                }
                throw buildException(ISO_INVALID_PDU);
            }
            if (lastPDUType == (byte) 0xd0) {
                return;
            }
        }
        throw buildException(ISO_CONNECTION_FAILED);
    }

    private boolean updateNegotiatePduLength() throws S7Exception {
        // Set PDU Size Requested
        S7.setWordAt(S7_PN, 23, DEFAULT_PDU_SIZE_REQUESTED);
        if (sendPacket(S7_PN)) {
            int length = recvIsoPacket();
            if (length != 27) {
                throw buildException(ISO_NEGOTIATING_PDU);
            }
            // check S7 Error: 20 = size of Negotiate Answer
            if (pdu[17] != (byte) 0x00 || pdu[18] != (byte) 0x00) {
                throw buildException(ISO_NEGOTIATING_PDU);
            }
            pduLength = S7.getWordAt(pdu, 25);
            logger.debug("PDU negotiated length: {} bytes", pduLength);
            if (pduLength < 1) {
                throw buildException(ISO_NEGOTIATING_PDU);
            }
            return true;
        }
        return false;
    }

    @Override
    public int getPduLength() {
        return pduLength;
    }

    @Override
    public boolean setPlcColdStart() throws S7Exception {
        if (sendPacket(S7_COLD_START)) {
            int length = recvIsoPacket();
            if (length < 19) {
                throw buildException(ISO_INVALID_PDU);
            }
            if (S7.getWordAt(pdu, 17) != 0) {
                throw buildException(S7_FUNCTION_ERROR);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean setPlcHotStart() throws S7Exception {
        if (sendPacket(S7_HOT_START)) {
            int length = recvIsoPacket();
            if (length < 19) {
                throw buildException(ISO_INVALID_PDU);
            }
            if (S7.getWordAt(pdu, 17) != 0) {
                throw buildException(S7_FUNCTION_ERROR);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean setPlcStop() throws S7Exception {
        if (sendPacket(S7_STOP)) {
            int length = recvIsoPacket();
            if (length < 19) {
                throw buildException(ISO_INVALID_PDU);
            }
            if (S7.getWordAt(pdu, 17) != 0) {
                throw buildException(S7_FUNCTION_ERROR);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean readMultiVars(S7DataItem[] items, int itemsCount) throws S7Exception {
        int offset;
        int length;
        int itemSize;
        byte[] s7Item = new byte[12];
        byte[] s7ItemRead = new byte[1024];

        // Checks items
        if (itemsCount > MAX_VARS) {
            throw buildException(ERR_TOO_MANY_ITEMS);
        }

        // Fills Header
        System.arraycopy(S7_MRD_HEADER, 0, pdu, 0, S7_MRD_HEADER.length);
        S7.setWordAt(pdu, 13, (itemsCount * s7Item.length + 2));
        pdu[18] = (byte) itemsCount;
        offset = 19;
        for (int c = 0; c < itemsCount; c++) {
            System.arraycopy(S7_MRD_ITEM, 0, s7Item, 0, s7Item.length);
            s7Item[3] = items[c].type.getValue();
            S7.setWordAt(s7Item, 4, items[c].amount);
            if (items[c].area == AreaType.DB) {
                S7.setWordAt(s7Item, 6, items[c].db);
            }
            s7Item[8] = items[c].area.getValue();

            // address into the PLC
            int address = items[c].start;
            s7Item[11] = (byte) (address & 0xff);
            s7Item[10] = (byte) (address >> 8 & 0xff);
            s7Item[9] = (byte) (address >> 16 & 0xff);

            System.arraycopy(s7Item, 0, pdu, offset, s7Item.length);
            offset += s7Item.length;
        }

        if (offset > pduLength) {
            logger.error("PDU length < offset ({}/{})", pduLength, offset);
            throw buildException(ERR_SIZE_OVER_PDU);
        }

        S7.setWordAt(pdu, 2, offset); // Whole size

        if (!sendPacket(pdu, offset)) {
            throw buildException(ERR_ISO_INVALID_PDU);
        }
        // get Answer
        length = recvIsoPacket();

        // Check ISO length
        if (length < 22) {
            throw buildException(ERR_ISO_INVALID_PDU);
        }
        // Check Global Operation Result
        int globalResult = S7.getWordAt(pdu, 17);
        if (globalResult != 0) {
            throw buildException(ReturnCode.getCpuError(globalResult));
        }

        // get true itemsCount
        int itemsRead = S7.getByteAt(pdu, 20);
        if ((itemsRead != itemsCount) || (itemsRead > MAX_VARS)) {
            throw buildException(ERR_INVALID_PLC_ANSWER);
        }
        // get Data
        offset = 21;
        for (int c = 0; c < itemsCount; c++) {
            // get the Item
            System.arraycopy(pdu, offset, s7ItemRead, 0, length - offset);
            if (s7ItemRead[0] == (byte) 0xff) {
                itemSize = S7.getWordAt(s7ItemRead, 2);
                if ((s7ItemRead[1] != TS_RESOCTET) && (s7ItemRead[1] != TS_RESREAL) && (s7ItemRead[1] != TS_RESBIT)) {
                    itemSize = itemSize >> 3;
                }
                System.arraycopy(s7ItemRead, 4, items[c].data, 0, Math.min(items[c].data.length, itemSize));
                // Marshal.Copy(s7ItemRead, 4, items[c].pData, itemSize);
                items[c].result = 0;
                if (itemSize % 2 != 0) {
                    itemSize++; // Odd size are rounded
                }
                offset = offset + 4 + itemSize;
            } else {
                items[c].result = ReturnCode.getCpuError(s7ItemRead[0]);
                offset += 4; // Skip the Item header
            }
        }

        return true;
    }

    @Override
    public boolean writeMultiVars(S7DataItem[] items, int itemsCount) throws S7Exception {
        int offset;
        int parLength;
        int dataLength;
        int itemDataSize;
        byte[] s7ParItem = new byte[S7_MWR_PARAM.length];
        byte[] dataItem = new byte[1024];

        // Checks items
        if (itemsCount > MAX_VARS) {
            throw buildException(ERR_TOO_MANY_ITEMS);
        }
        // Fills Header
        System.arraycopy(S7_MWR_HEADER, 0, pdu, 0, S7_MWR_HEADER.length);
        parLength = itemsCount * S7_MWR_PARAM.length + 2;
        S7.setWordAt(pdu, 13, parLength);
        pdu[18] = (byte) itemsCount;
        // Fills Params
        offset = S7_MWR_HEADER.length;
        for (int c = 0; c < itemsCount; c++) {
            System.arraycopy(S7_MWR_PARAM, 0, s7ParItem, 0, S7_MWR_PARAM.length);
            s7ParItem[3] = items[c].type.getValue();
            s7ParItem[8] = items[c].area.getValue();
            S7.setWordAt(s7ParItem, 4, items[c].amount);
            S7.setWordAt(s7ParItem, 6, items[c].db);
            // address into the PLC
            int address = items[c].start;
            s7ParItem[11] = (byte) (address & 0xff);
            s7ParItem[10] = (byte) (address >> 8 & 0xff);
            s7ParItem[9] = (byte) (address >> 16 & 0xff);

            System.arraycopy(s7ParItem, 0, pdu, offset, s7ParItem.length);
            offset += S7_MWR_PARAM.length;
        }
        // Fills Data
        dataLength = 0;
        for (int c = 0; c < itemsCount; c++) {
            dataItem[0] = 0x00;
            switch (items[c].type) {
                case BIT:
                    dataItem[1] = TS_RESBIT;
                    break;
                case COUNTER:
                case TIMER:
                    dataItem[1] = TS_RESOCTET;
                    break;
                default:
                    dataItem[1] = TS_RESBYTE; // byte/word/dword etc.
                    break;
            }
            if ((items[c].type == DataType.TIMER) || (items[c].type == DataType.COUNTER)) {
                itemDataSize = items[c].amount * 2;
            } else {
                itemDataSize = items[c].amount;
            }

            if ((dataItem[1] != TS_RESOCTET) && (dataItem[1] != TS_RESBIT)) {
                S7.setWordAt(dataItem, 2, (itemDataSize * 8));
            } else {
                S7.setWordAt(dataItem, 2, itemDataSize);
            }

            System.arraycopy(items[c].data, 0, dataItem, 4, itemDataSize);
            if (itemDataSize % 2 != 0) {
                dataItem[itemDataSize + 4] = (byte) 0x00;
                itemDataSize++;
            }
            System.arraycopy(dataItem, 0, pdu, offset, itemDataSize + 4);
            offset = offset + itemDataSize + 4;
            dataLength = dataLength + itemDataSize + 4;
        }

        // Checks the size
        if (offset > pduLength) {
            throw buildException(ERR_SIZE_OVER_PDU);
        }

        S7.setWordAt(pdu, 2, offset); // Whole size
        S7.setWordAt(pdu, 15, dataLength); // Whole size
        sendPacket(pdu, offset);

        recvIsoPacket();
        // Check Global Operation Result
        int globalResult = S7.getWordAt(pdu, 17);
        if (globalResult != 0) {
            throw buildException(ReturnCode.getCpuError(globalResult));
        }
        // get true itemsCount
        int itemsWritten = S7.getByteAt(pdu, 20);
        if ((itemsWritten != itemsCount) || (itemsWritten > MAX_VARS)) {
            throw buildException(ERR_INVALID_PLC_ANSWER);
        }

        for (int c = 0; c < itemsCount; c++) {
            if (pdu[c + 21] == (byte) 0xff) {
                items[c].result = 0;
            } else {
                items[c].result = ReturnCode.getCpuError(pdu[c + 21]);
            }
        }
        return true;
    }

    @Override
    public int readArea(final AreaType area, final int db, final int start, final int amount, final DataType type, final byte[] buffer) throws S7Exception {
        int _amount = amount;
        DataType _type;
        switch (area) {
            case CT:
                _type = DataType.COUNTER;
                break;
            case TM:
                _type = DataType.TIMER;
                break;
            default:
                _type = type;
                break;
        }
        int wordSize = DataType.getByteLength(_type);
        if (wordSize == 0) {
            throw buildException(ERR_INVALID_WORD_LEN);
        }
        switch (_type) {
            case BIT:
                _amount = 1;
                break;
            case COUNTER:
            case TIMER:
                break;
            default:
                _amount = _amount * wordSize;
                wordSize = 1;
                _type = DataType.BYTE;
                break;
        }
        return readInternalArea(area, db, start, _amount, _type, wordSize, buffer);
    }

    private int readInternalArea(final AreaType area, final int db, final int start, final int amount, final DataType type, final int wordSize, final byte[] buffer) throws S7Exception {
        int sizeRequested;
        int offset = 0;
        // 18 = Reply telegram header
        int maxElements = (pduLength - 18) / wordSize;
        int totElements = amount;
        int position = start;

        while (totElements > 0) {
            int numElements = totElements;
            if (numElements > maxElements) {
                numElements = maxElements;
            }

            sizeRequested = numElements * wordSize;

            // Setup the telegram
            System.arraycopy(S7_RW, 0, pdu, 0, SIZE_RD);
            // Set DB Number
            pdu[27] = area.getValue();
            // Set area
            if (area == AreaType.DB) {
                S7.setWordAt(pdu, 25, db);
            }

            int address;
            switch (type) {
                case BIT:
                case COUNTER:
                case TIMER:
                    address = position;
                    pdu[22] = type.getValue();
                    break;
                default:
                    address = position << 3;
                    break;
            }

            // Num elements
            S7.setWordAt(pdu, 23, numElements);

            // address into the PLC (only 3 bytes)
            pdu[30] = (byte) (address & 0xff);
            pdu[29] = (byte) (address >> 8 & 0xff);
            pdu[28] = (byte) (address >> 16 & 0xff);

            if (sendPacket(pdu, SIZE_RD)) {
                int length = recvIsoPacket();
                if (length < 25) {
                    throw buildException(ERR_ISO_INVALID_DATA_SIZE);
                }
                if (pdu[21] != (byte) 0xff) {
                    throw buildException(ReturnCode.getCpuError(pdu[21]));
                }
                System.arraycopy(pdu, 25, buffer, offset, sizeRequested);
                offset += sizeRequested;
            }
            totElements -= numElements;
            position += numElements * wordSize;
        }
        return offset;
    }

    @Override
    public S7Szl getSzl(int id, int index, int bufferSize) throws S7Exception {
        final S7Szl szl = new S7Szl(bufferSize);
        int length;
        int dataSZL;
        int offset = 0;
        boolean done = false;
        boolean first = true;
        byte seq_in = 0x00;
        int seq_out = 0x0000;

        szl.dataSize = 0;
        do {
            if (first) {
                S7.setWordAt(S7_SZL_FIRST, 11, ++seq_out);
                S7.setWordAt(S7_SZL_FIRST, 29, id);
                S7.setWordAt(S7_SZL_FIRST, 31, index);
                sendPacket(S7_SZL_FIRST);
            } else {
                S7.setWordAt(S7_SZL_NEXT, 11, ++seq_out);
                pdu[24] = seq_in;
                sendPacket(S7_SZL_NEXT);
            }

            length = recvIsoPacket();
            if (length < 33) {
                throw buildException(ISO_INVALID_PDU);
            }
            if (S7.getWordAt(pdu, 27) != 0) {
                throw buildException(S7.getWordAt(pdu, 27));
            }
            if (pdu[29] != (byte) 0xff) {
                throw buildException(S7_FUNCTION_ERROR);
            }
            if (first) {
                // gets amount of this slice
                // Skips extra params (ID, Index ...)
                dataSZL = S7.getWordAt(pdu, 31) - 8;
                done = pdu[26] == (byte) 0x00;
                seq_in = pdu[24]; // Slice sequence

                szl.lenthdr = S7.getWordAt(pdu, 37);
                szl.n_dr = S7.getWordAt(pdu, 39);
                szl.copy(pdu, 41, offset, dataSZL);
                offset += dataSZL;
                szl.dataSize += dataSZL;
            } else {
                // gets amount of this slice
                dataSZL = S7.getWordAt(pdu, 31);
                done = pdu[26] == (byte) 0x00;
                seq_in = pdu[24]; // Slice sequence
                szl.copy(pdu, 37, offset, dataSZL);
                offset += dataSZL;
                szl.dataSize += dataSZL;
            }
            first = false;
        } while (!done);

        return szl;
    }

    private int recvIsoPacket() throws S7Exception {
        boolean done = false;
        int size = 0;
        while (!done) {
            // get TPKT (4 bytes)
            recvPacket(pdu, 0, 4);
            size = S7.getWordAt(pdu, 2);
            // Check 0 bytes Data Packet (only TPKT+COTP = 7 bytes)
            if (size == ISO_HEADER_SIZE) {
                recvPacket(pdu, 4, 3); // Skip remaining 3 bytes and Done is still false
            } else {
                if (size > MAX_PDU_SIZE || size < MIN_PDU_SIZE) {
                    throw buildException(ISO_INVALID_PDU);
                }
                done = true; // a valid length !=7 && >16 && <247
            }
        }

        // Skip remaining 3 COTP bytes
        recvPacket(pdu, 4, 3);
        // Stores PDU Type, we need it
        lastPDUType = pdu[5];
        // Receives the S7 Payload
        recvPacket(pdu, 7, size - ISO_HEADER_SIZE);

        return size;
    }

    protected int recvPacket(byte[] buffer, int start, int size) throws S7Exception {
        int bytesRead = 0;
        try {
            int offset = start, timeout = 0;
            while (timeout < recvTimeout) {
                bytesRead += inStream.read(buffer, offset, size);
                if (bytesRead == size) {
                    return bytesRead;
                }
                offset += bytesRead;
                Thread.sleep(1);
                timeout++;
            }
            if (bytesRead == 0) {
                throw buildException(TCP_CONNECTION_RESET);
            }
            logger.error("cleanup the buffer: {}", inStream.available());
            inStream.read(pdu);

        } catch (InterruptedException e) {
            logger.debug("recv packet interrupted");
        } catch (IOException e) {
            throw buildException(TCP_DATA_RECV, e);
        }
        return bytesRead;
    }

    private boolean sendPacket(byte[] buffer) throws S7Exception {
        return sendPacket(buffer, buffer.length);
    }

    protected boolean sendPacket(byte[] buffer, int len) throws S7Exception {
        try {
            outStream.write(buffer, 0, len);
            outStream.flush();
            return true;
        } catch (Exception e) {
            throw buildException(TCP_DATA_SEND, e);
        }
    }

    @Override
    public boolean setPlcDateTime(LocalDateTime dateTime) throws S7Exception {
        S7.setDateTimeAt(S7_SET_DT, 31, dateTime);
        if (sendPacket(S7_SET_DT)) {
            int length = recvIsoPacket();
            if (length < 31) {
                throw buildException(ISO_INVALID_PDU);
            }

            if (S7.getWordAt(pdu, 27) != 0) {
                throw buildException(S7.getWordAt(pdu, 27));
            }
            return true;
        }
        throw buildException(S7_FUNCTION_ERROR);
    }

    @Override
    public boolean setPlcDateTime() throws S7Exception {
        return setPlcDateTime(LocalDateTime.now());
    }

    @Override
    public boolean setSessionPassword(String password) throws S7Exception {
        // Adjusts the Password length to 8
        if (password.length() > 8) {
            password = password.substring(0, 8);
        } else {
            while (password.length() < 8) {
                password = password + " ";
            }
        }
        byte[] pwd = password.getBytes(StandardCharsets.UTF_8);

        // Encodes the password
        pwd[0] = (byte) (pwd[0] ^ 0x55);
        pwd[1] = (byte) (pwd[1] ^ 0x55);
        for (int c = 2; c < 8; c++) {
            pwd[c] = (byte) (pwd[c] ^ 0x55 ^ pwd[c - 2]);
        }
        System.arraycopy(pwd, 0, S7_SET_PWD, 29, 8);
        if (sendPacket(S7_SET_PWD)) {
            int length = recvIsoPacket();
            if (length < 33) {
                throw buildException(ISO_INVALID_PDU);
            }

            if (S7.getWordAt(pdu, 27) != 0) {
                throw buildException(S7.getWordAt(pdu, 27));
            }
            return true;
        }
        throw buildException(S7_FUNCTION_ERROR);
    }

    @Override
    public boolean writeArea(final AreaType area, final int db, final int start, final int amount, final DataType type, final byte[] buffer)
            throws S7Exception {
        int _amount = amount;
        DataType _type;
        switch (area) {
            case CT:
                _type = DataType.COUNTER;
                break;
            case TM:
                _type = DataType.TIMER;
                break;
            default:
                _type = type;
                break;
        }
        int wordSize = DataType.getByteLength(_type);
        if (wordSize == 0) {
            throw buildException(ERR_INVALID_WORD_LEN);
        }
        switch (_type) {
            case BIT:
                _amount = 1;
                break;
            case COUNTER:
            case TIMER:
                break;
            default:
                _amount = _amount * wordSize;
                wordSize = 1;
                _type = DataType.BYTE;
                break;
        }
        return writeInternalArea(area, db, start, _amount, _type, wordSize, buffer);
    }

    private boolean writeInternalArea(final AreaType area, final int db, final int start, final int amount, final DataType type, final int wordSize,
            final byte[] buffer) throws S7Exception {
        int maxElements = (pduLength - 35) / wordSize; // 35 = Reply telegram header
        int totElements = amount;
        int offset = 0;
        int position = start;
        while (totElements > 0) {
            int numElements = totElements;
            if (numElements > maxElements) {
                numElements = maxElements;
            }

            int dataSize = numElements * wordSize;
            int isoSize = SIZE_WR + dataSize;

            // setup the telegram
            System.arraycopy(S7_RW, 0, pdu, 0, SIZE_WR);
            // Whole telegram Size
            S7.setWordAt(pdu, 2, isoSize);
            // Data length
            int length = dataSize + 4;
            S7.setWordAt(pdu, 15, length);
            // Function
            pdu[17] = (byte) 0x05;
            // set DB Number
            pdu[27] = area.getValue();
            if (area == AreaType.DB) {
                S7.setWordAt(pdu, 25, db);
            }

            // Adjusts start and word length
            int address;
            switch (type) {
                case BIT:
                case COUNTER:
                case TIMER:
                    address = position;
                    length = dataSize;
                    pdu[22] = type.getValue();
                    break;
                default:
                    address = position << 3;
                    length = dataSize << 3;
                    break;
            }

            // Num elements
            S7.setWordAt(pdu, 23, numElements);
            // address into the PLC
            pdu[30] = (byte) (address & 0xff);
            pdu[29] = (byte) (address >> 8 & 0xff);
            pdu[28] = (byte) (address >> 16 & 0xff);

            // Transport Size
            switch (type) {
                case BIT:
                    pdu[32] = TS_RESBIT;
                    break;
                case COUNTER:
                case TIMER:
                    pdu[32] = TS_RESOCTET;
                    break;
                default:
                    // byte/word/dword/real/char converted to byte[] etc.
                    pdu[32] = TS_RESBYTE;
                    break;
            }
            // length
            S7.setWordAt(pdu, 33, length);

            // Copies the Data
            System.arraycopy(buffer, offset, pdu, 35, dataSize);

            if (sendPacket(pdu, isoSize)) {
                length = recvIsoPacket();
                if (length != 22) {
                    throw buildException(ERR_ISO_INVALID_PDU);
                }

                if (pdu[21] != (byte) 0xff) {
                    throw buildException(ReturnCode.getCpuError(pdu[21]));
                }

            }
            offset += dataSize;
            totElements -= numElements;
            position += numElements * wordSize;
        }
        return true;
    }

    @Override
    public int getIsoExchangeBuffer(byte[] buffer) throws S7Exception {
        int size = 0;
        System.arraycopy(TPKT_ISO, 0, pdu, 0, TPKT_ISO.length);
        S7.setWordAt(pdu, 2, (size + TPKT_ISO.length));
        try {
            System.arraycopy(buffer, 0, pdu, TPKT_ISO.length, size);
        } catch (Exception e) {
            throw buildException(ERR_ISO_INVALID_PDU, e);
        }
        if (sendPacket(pdu, TPKT_ISO.length + size)) {
            int length = recvIsoPacket();
            if (length < 1) {
                throw buildException(ISO_INVALID_PDU);
            }

            System.arraycopy(pdu, TPKT_ISO.length, buffer, 0, length - TPKT_ISO.length);
            size = length - TPKT_ISO.length;
        }
        return size;
    }

    private S7Exception buildException(int code) {
        return buildException(code, null);
    }

    protected S7Exception buildException(int code, Throwable e) {
        return e == null ? new S7Exception(code, ReturnCode.getErrorText(code)) : new S7Exception(code, ReturnCode.getErrorText(code), e);
    }

    private static void closeSilently(Closeable c) {
        try {
            if (c != null) {
                c.close();
            }
        } catch (IOException e) {
        }
    }
}
