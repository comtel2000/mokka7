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

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

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
import org.comtel2000.mokka7.util.S7;

/**
 *
 * @author comtel
 *
 */
public interface Client {

    final byte[] buffer = new byte[1024];

    /** Max number of vars (multiread/write) -> max PDU size */
    public static final int MAX_VARS = 20;

    /** Result transport size */
    static final byte TS_RESBIT = 0x03;
    static final byte TS_RESBYTE = 0x04;
    static final byte TS_RESINT = 0x05;
    static final byte TS_RESREAL = 0x07;
    static final byte TS_RESOCTET = 0x09;

    static final int DEFAULT_PDU_SIZE_REQUESTED = 480;

    static final int SIZE_RD = 31;
    static final int SIZE_WR = 35;

    /** default TCP receive timeout in ms */
    static final int RECV_TIMEOUT = 2000;

    /** default ISO tcp port */
    static final int ISO_TCP = 102;

    /** TPKT+COTP Header Size */
    static final int ISO_HEADER_SIZE = 7;

    static final int MAX_PDU_SIZE = DEFAULT_PDU_SIZE_REQUESTED + ISO_HEADER_SIZE;

    static final int MIN_PDU_SIZE = 16;

    /** TPKT + ISO COTP Header (Connection Oriented Transport Protocol) */
    static final byte[] TPKT_ISO = { // 7 bytes
            0x03, 0x00, 0x00, 0x1f, // Telegram Length (Data Size + 31 or 35)
            0x02, (byte) 0xf0, (byte) 0x80 // COTP (see above for info)
    };

    /** Telegrams ISO Connection Request telegram (contains also ISO Header and COTP Header) */
    static final byte ISO_CR[] = {
            // TPKT (RFC1006 Header)
            (byte) 0x03, // RFC 1006 ID (3)
            (byte) 0x00, // Reserved, always 0
            (byte) 0x00, // High part of packet lenght (entire frame, payload and TPDU included)
            (byte) 0x16, // Low part of packet lenght (entire frame, payload and TPDU included)
            // COTP (ISO 8073 Header)
            (byte) 0x11, // PDU Size length
            (byte) 0xE0, // CR - Connection Request ID
            (byte) 0x00, // Dst Reference HI
            (byte) 0x00, // Dst Reference LO
            (byte) 0x00, // Src Reference HI
            (byte) 0x01, // Src Reference LO
            (byte) 0x00, // Class + Options Flags
            (byte) 0xC0, // PDU Max length ID
            (byte) 0x01, // PDU Max length HI
            (byte) 0x0A, // PDU Max length LO
            (byte) 0xC1, // Src TSAP Identifier
            (byte) 0x02, // Src TSAP length (2 bytes)
            (byte) 0x01, // Src TSAP HI (will be overwritten)
            (byte) 0x00, // Src TSAP LO (will be overwritten)
            (byte) 0xC2, // Dst TSAP Identifier
            (byte) 0x02, // Dst TSAP length (2 bytes)
            (byte) 0x01, // Dst TSAP HI (will be overwritten)
            (byte) 0x02 // Dst TSAP LO (will be overwritten)
    };

    /** S7 get Block Info Request Header (contains also ISO Header and COTP Header) */
    static final byte S7_BI[] = { (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x25, (byte) 0x02, (byte) 0xf0, (byte) 0x80, (byte) 0x32, (byte) 0x07,
            (byte) 0x00, (byte) 0x00,
            (byte) 0x05, (byte) 0x00,
            (byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x0c, (byte) 0x00, (byte) 0x01, (byte) 0x12,
            (byte) 0x04, (byte) 0x11, (byte) 0x43, (byte) 0x03, (byte) 0x00, (byte) 0xff, (byte) 0x09, (byte) 0x00, (byte) 0x08, (byte) 0x30, (byte) 0x41,
            // Block Type
            (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x30, (byte) 0x30,
            // ASCII Block Number
            (byte) 0x41 };

    /** S7 Clear Session Password */
    static final byte S7_CLR_PWD[] = { (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x1d, (byte) 0x02, (byte) 0xf0, (byte) 0x80, (byte) 0x32, (byte) 0x07,
            (byte) 0x00, (byte) 0x00, (byte) 0x29, (byte) 0x00, (byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x01, (byte) 0x12,
            (byte) 0x04, (byte) 0x11, (byte) 0x45, (byte) 0x02, (byte) 0x00, (byte) 0x0a, (byte) 0x00, (byte) 0x00, (byte) 0x00 };

    /** S7 COLD start request */
    static final byte S7_COLD_START[] = { (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x27, (byte) 0x02, (byte) 0xf0, (byte) 0x80, (byte) 0x32, (byte) 0x01,
            (byte) 0x00, (byte) 0x00, (byte) 0x0f, (byte) 0x00, (byte) 0x00, (byte) 0x16, (byte) 0x00, (byte) 0x00, (byte) 0x28, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xfd, (byte) 0x00, (byte) 0x02, (byte) 0x43, (byte) 0x20, (byte) 0x09, (byte) 0x50,
            (byte) 0x5f, (byte) 0x50, (byte) 0x52, (byte) 0x4f, (byte) 0x47, (byte) 0x52, (byte) 0x41, (byte) 0x4d };

    /** get Date/Time request */
    static final byte S7_GET_DT[] = { (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x1d, (byte) 0x02, (byte) 0xf0, (byte) 0x80, (byte) 0x32, (byte) 0x07,
            (byte) 0x00, (byte) 0x00, (byte) 0x38, (byte) 0x00, (byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x01, (byte) 0x12,
            (byte) 0x04, (byte) 0x11, (byte) 0x47, (byte) 0x01, (byte) 0x00, (byte) 0x0a, (byte) 0x00, (byte) 0x00, (byte) 0x00 };

    /** S7 get PLC Status */
    static final byte S7_GET_STAT[] = { (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x21, (byte) 0x02, (byte) 0xf0, (byte) 0x80, (byte) 0x32, (byte) 0x07,
            (byte) 0x00, (byte) 0x00, (byte) 0x2c, (byte) 0x00, (byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x01, (byte) 0x12,
            (byte) 0x04, (byte) 0x11, (byte) 0x44, (byte) 0x01, (byte) 0x00, (byte) 0xff, (byte) 0x09, (byte) 0x00, (byte) 0x04, (byte) 0x04, (byte) 0x24,
            (byte) 0x00, (byte) 0x00 };

    /** S7 HOT start request */
    static final byte S7_HOT_START[] = { (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x25, (byte) 0x02, (byte) 0xf0, (byte) 0x80, (byte) 0x32, (byte) 0x01,
            (byte) 0x00, (byte) 0x00, (byte) 0x0c, (byte) 0x00, (byte) 0x00, (byte) 0x14, (byte) 0x00, (byte) 0x00, (byte) 0x28, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0xfd, (byte) 0x00, (byte) 0x00, (byte) 0x09, (byte) 0x50, (byte) 0x5f, (byte) 0x50,
            (byte) 0x52, (byte) 0x4f, (byte) 0x47, (byte) 0x52, (byte) 0x41, (byte) 0x4d };

    /** S7 PDU Negotiation Telegram (contains also ISO Header and COTP Header) */
    static final byte S7_PN[] = { (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x19, (byte) 0x02, (byte) 0xf0,
            (byte) 0x80, // TPKT + COTP (see above for info)
            (byte) 0x32, (byte) 0x01,
            (byte) 0x00, (byte) 0x00, // Redundancy identification
            (byte) 0xff, (byte) 0xff, // Protocol Data Unit Reference (init)
            (byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x00, (byte) 0xf0,
            (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x01, (byte) 0x00,

            (byte) 0x1e // PDU length Requested = HI-LO 480 bytes
    };

    /** S7 Read/Write Request Header (contains also ISO Header and COTP Header) */
    static final byte S7_RW[] = { // 31-35 bytes
            (byte) 0x03, (byte) 0x00, (byte) 0x00,

            (byte) 0x1f, // Telegram length (Data Size + 31 or 35)
            (byte) 0x02, (byte) 0xf0, (byte) 0x80, // COTP (see above for info)
            (byte) 0x32, // S7 Protocol ID
            (byte) 0x01, // Job Type
            (byte) 0x00, (byte) 0x00, // Redundancy identification
            (byte) 0x00, (byte) 0x00, // Protocol Data Unit Reference
            (byte) 0x00, (byte) 0x0e, // Parameters length
            (byte) 0x00, (byte) 0x00, // Data length = Size(bytes) + 4
            (byte) 0x04, // Function 4 Read Var, 5 Write Var
            (byte) 0x01, // Items count
            (byte) 0x12, // Var spec.
            (byte) 0x0a, // length of remaining bytes
            (byte) 0x10, // Syntax ID
            DataType.BYTE.getValue(), // Transport Size
            (byte) 0x00, (byte) 0x00, // Num Elements
            (byte) 0x00, (byte) 0x00, // DB Number (if any, else 0)
            (byte) 0x84, // area Type
            (byte) 0x00, (byte) 0x00, (byte) 0x00, // area offset
            // WR area
            (byte) 0x00, // Reserved
            (byte) 0x04, // Transport size
            (byte) 0x00, (byte) 0x00, // Data length * 8 (if not timer or counter)
    };

    /** get Date/Time command */
    static final byte S7_SET_DT[] = { (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x27, (byte) 0x02, (byte) 0xf0, (byte) 0x80, (byte) 0x32, (byte) 0x07,
            (byte) 0x00, (byte) 0x00, (byte) 0x89, (byte) 0x03, (byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x0e, (byte) 0x00, (byte) 0x01, (byte) 0x12,
            (byte) 0x04, (byte) 0x11, (byte) 0x47, (byte) 0x02, (byte) 0x00, (byte) 0xff, (byte) 0x09, (byte) 0x00, (byte) 0x0a, (byte) 0x00,

            (byte) 0x19, // Hi part of Year
            (byte) 0x13, // Lo part of Year
            (byte) 0x12, // Month
            (byte) 0x06, // Day
            (byte) 0x17, // Hour
            (byte) 0x37, // Min
            (byte) 0x13, // Sec
            (byte) 0x00, (byte) 0x01 // ms + Day of week
    };

    /** S7 Set Session Password */
    static final byte S7_SET_PWD[] = { (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x25, (byte) 0x02, (byte) 0xf0, (byte) 0x80, (byte) 0x32, (byte) 0x07,
            (byte) 0x00, (byte) 0x00, // Redundancy identification
            (byte) 0x27, (byte) 0x00, // Protocol Data Unit Reference
            (byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x0c, (byte) 0x00, (byte) 0x01, (byte) 0x12,
            (byte) 0x04, (byte) 0x11, (byte) 0x45, (byte) 0x01, (byte) 0x00, (byte) 0xff, (byte) 0x09, (byte) 0x00, (byte) 0x08,
            // 8 Char Encoded Password
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00 };

    /** S7 STOP request */
    static final byte S7_STOP[] = { (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x21, (byte) 0x02, (byte) 0xf0, (byte) 0x80, (byte) 0x32, (byte) 0x01,
            (byte) 0x00, (byte) 0x00,  // Redundancy identification
            (byte) 0x0e, (byte) 0x00,  // Protocol Data Unit Reference
            (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x00, (byte) 0x29, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x09, (byte) 0x50, (byte) 0x5f, (byte) 0x50, (byte) 0x52, (byte) 0x4f, (byte) 0x47, (byte) 0x52,
            (byte) 0x41, (byte) 0x4d };

    /** SZL First telegram request */
    static final byte S7_SZL_FIRST[] = { (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x21, (byte) 0x02, (byte) 0xf0, (byte) 0x80, (byte) 0x32, (byte) 0x07,
            (byte) 0x00, (byte) 0x00,  // Redundancy identification
            (byte) 0x00, (byte) 0x00,  // Protocol Data Unit Reference
            (byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x01, (byte) 0x12, (byte) 0x04, (byte) 0x11, (byte) 0x44, (byte) 0x01,
            (byte) 0x00, (byte) 0xff, (byte) 0x09, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x00, // ID
                                                                                                       // (29)
            (byte) 0x00, (byte) 0x00 // Index (31)
    };

    /** SZL Next telegram request */
    static final byte S7_SZL_NEXT[] = { (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x21, (byte) 0x02, (byte) 0xf0, (byte) 0x80, (byte) 0x32, (byte) 0x07,
            (byte) 0x00, (byte) 0x00,  // Redundancy identification
            (byte) 0x00, (byte) 0x00,  // Protocol Data Unit Reference
            (byte) 0x00, (byte) 0x0c, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x01, (byte) 0x12,
            (byte) 0x08, (byte) 0x12, (byte) 0x44, (byte) 0x01, (byte) 0x01, // Sequence
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0a, (byte) 0x00, (byte) 0x00, (byte) 0x00 };

    /** S7 Variable MultiRead Header */
    static final byte[] S7_MRD_HEADER = { 0x03, 0x00, 0x00, 0x1f, // Telegram length
            0x02, (byte) 0xf0, (byte) 0x80, // COTP (see above for info)
            0x32, // S7 Protocol ID
            0x01, // Job Type
            0x00, 0x00, // Redundancy identification
            0x00, 0x00, // Protocol Data Unit Reference
            0x00, 0x0e, // Parameters length
            0x00, 0x00, // Data length = Size(bytes) + 4
            0x04, // Function 4 Read Var, 5 Write Var
            0x01 // Items count (idx 18)
    };

    /** S7 Variable MultiRead Item */
    static final byte[] S7_MRD_ITEM = { 0x12, // Var spec.
            0x0a, // length of remaining bytes
            0x10, // Syntax ID
            DataType.BYTE.getValue(), // Transport Size idx=3
            0x00, 0x00, // Num Elements
            0x00, 0x00, // DB Number (if any, else 0)
            (byte) 0x84, // area Type
            0x00, 0x00, 0x00 // area offset
    };

    /** S7 Variable MultiWrite Header */
    static final byte[] S7_MWR_HEADER = { 0x03, 0x00, 0x00, 0x1f, // Telegram length
            0x02, (byte) 0xf0, (byte) 0x80, // COTP (see above for info)
            0x32, // S7 Protocol ID
            0x01, // Job Type
            0x00, 0x00, // Redundancy identification
            0x00, 0x00, // Protocol Data Unit Reference
            0x00, 0x0e, // Parameters length (idx 13)
            0x00, 0x00, // Data length = Size(bytes) + 4 (idx 15)
            0x05, // Function 5 Write Var
            0x01 // Items count (idx 18)
    };

    /** S7 Variable MultiWrite Item (Param) */
    static final byte[] S7_MWR_PARAM = { 0x12, // Var spec.
            0x0a, // length of remaining bytes
            0x10, // Syntax ID
            DataType.BYTE.getValue(), // Transport Size idx=3
            0x00, 0x00, // Num Elements
            0x00, 0x00, // DB Number (if any, else 0)
            (byte) 0x84, // area Type
            0x00, 0x00, 0x00, // area offset
    };

    /** S7 get Block List */
    static final byte S7_BL[] = { (byte) 0x03, (byte) 0x00, (byte) 0x00, (byte) 0x1d, (byte) 0x02, (byte) 0xf0, (byte) 0x80, (byte) 0x32, (byte) 0x07,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // [11,12] Protocol Data Unit Reference
            (byte) 0x00, (byte) 0x08, (byte) 0x00, (byte) 0x04, (byte) 0x00, (byte) 0x01, (byte) 0x12, (byte) 0x04, (byte) 0x11, (byte) 0x43, (byte) 0x01,
            (byte) 0x00, (byte) 0x0a, (byte) 0x00, (byte) 0x00, (byte) 0x00 };

    boolean clearSessionPassword() throws S7Exception;

    /**
     * Connects the client to the hardware at (IP, Rack, Slot) Coordinates. Connects the client to
     * the PLC with the parameters specified in the previous call of
     * {@link #setConnectionParams(String, int, int)}.
     *
     * @see #connect(String, int, int)
     * @return succeed
     * @throws S7Exception ex
     */
    boolean connect() throws S7Exception;

    /**
     * Returns connection state
     *
     * @return connected
     */
    boolean isConnected();

    /**
     * Connects the client to the hardware at (IP, Rack, Slot) Coordinates.
     *
     * <table summary="PLC settings">
     * <tr>
     * <th>PLC</th>
     * <th>Rack</th>
     * <th>Slot</th>
     * <th>Options</th>
     * </tr>
     * <tr>
     * <td>S7 300 CPU</td>
     * <td>0</td>
     * <td>2</td>
     * <td>Always</td>
     * </tr>
     * <tr>
     * <td>S7 400 CPU</td>
     * <td>0..X</td>
     * <td>0..X</td>
     * <td>Follow the hardware configuration</td>
     * </tr>
     * <tr>
     * <td>S7 1200 CPU</td>
     * <td>0</td>
     * <td>0</td>
     * <td>Or 0, 1</td>
     * </tr>
     * <tr>
     * <td>S7 1500 CPU</td>
     * <td>0</td>
     * <td>0</td>
     * <td>Or 0, 1</td>
     * </tr>
     * <tr>
     * <td>WinAC CPU</td>
     * <td>0..X</td>
     * <td>0..X</td>
     * <td>Follow the hardware configuration</td>
     * </tr>
     * <tr>
     * <td>WinAC IE</td>
     * <td>0</td>
     * <td>0</td>
     * <td>Or follow the hardware configuration</td>
     * </tr>
     * </table>
     *
     * @param address host or ip address
     * @param rack Rack number
     * @param slot Slot number
     * @return succeed
     * @throws S7Exception ex
     */
    boolean connect(String address, int rack, int slot) throws S7Exception;

    /**
     * Read an entire DB from the PLC without the need of specifying its size. As output will
     * contain the size read.
     *
     * @param db DB number
     * @param buffer buffer to fill
     * @return the size readed
     * @throws S7Exception ex
     */
    int dbGet(int db, byte[] buffer) throws S7Exception;

    /**
     * Fill the entire DB from the PLC with a fill byte parameter. As output will contain the size
     * written.
     *
     * @param db DB number
     * @param fill char/byte to fill with
     * @return the written byte count
     * @throws S7Exception ex
     */
    int dbFill(int db, byte fill) throws S7Exception;

    /**
     * Disconnects "gracefully" the Client from the PLC.
     */
    void disconnect();

    /**
     * Returns some information about a given block. This function is very useful if you need to
     * read or write data in a DB which you do not know the size in advance (see MC7Size field).
     *
     * @param type of Block that we need
     * @param blockNumber Number of Block
     * @return {@link S7BlockInfo} info
     * @throws S7Exception ex
     */
    S7BlockInfo getAgBlockInfo(BlockType type, int blockNumber) throws S7Exception;

    S7BlockList getS7BlockList() throws S7Exception;

    S7CpInfo getCpInfo() throws S7Exception;

    S7CpuInfo getCpuInfo() throws S7Exception;

    S7OrderCode getOrderCode() throws S7Exception;

    LocalDateTime getPlcDateTime() throws S7Exception;

    PlcCpuStatus getPlcStatus() throws S7Exception;

    S7Protection getProtection() throws S7Exception;

    int getPduLength();

    boolean setPlcColdStart() throws S7Exception;

    boolean setPlcHotStart() throws S7Exception;

    boolean setPlcStop() throws S7Exception;

    boolean readMultiVars(S7DataItem[] items, int itemsCount) throws S7Exception;

    boolean writeMultiVars(S7DataItem[] items, int itemsCount) throws S7Exception;

    S7Szl getSzl(int id, int index, int bufferSize) throws S7Exception;

    /**
     * Writes the given DateTime to the PLC
     *
     * @param dateTime time
     * @return succeed
     * @throws S7Exception ex
     */
    boolean setPlcDateTime(LocalDateTime dateTime) throws S7Exception;

    /**
     * Set the current time to PLC
     *
     * @return succeed
     * @throws S7Exception ex
     * @see #setPlcDateTime(LocalDateTime)
     */
    boolean setPlcDateTime() throws S7Exception;

    boolean setSessionPassword(String password) throws S7Exception;

    int getIsoExchangeBuffer(byte[] buffer) throws S7Exception;

    default Boolean readBit(AreaType area, int db, int start, int bitPos) throws S7Exception {
        return readBit(area, db, start, bitPos, buffer);
    }

    default Boolean readBit(AreaType area, int db, int start, int bitPos, byte[] buffer) throws S7Exception {
        if (readArea(area, db, start * 8 + bitPos, 1, DataType.BIT, buffer) > 0) {
            return S7.getBitAt(buffer, 0, 0);
        }
        return null;
    }

    default Byte readByte(AreaType area, int db, int start) throws S7Exception {
        return readByte(area, db, start, buffer);
    }

    default Byte readByte(AreaType area, int db, int start, byte[] buffer) throws S7Exception {
        if (readArea(area, db, start, 1, DataType.BYTE, buffer) > 0) {
            return S7.getByteAt(buffer, 0);
        }
        return null;
    }

    default byte[] readBytes(AreaType area, int db, int start, int amount) throws S7Exception {
        return readBytes(area, db, start, amount, buffer);
    }

    default byte[] readBytes(AreaType area, int db, int start, int amount, byte[] buffer) throws S7Exception {
        if (readArea(area, db, start, amount, DataType.BYTE, buffer) > 0) {
            return Arrays.copyOf(buffer, amount);
        }
        return null;
    }

    default Integer readInt(AreaType area, int db, int start) throws S7Exception {
        return readInt(area, db, start, buffer);
    }

    default Integer readInt(AreaType area, int db, int start, byte[] buffer) throws S7Exception {
        if (readArea(area, db, start, 1, DataType.DINT, buffer) > 0) {
            return S7.getDIntAt(buffer, 0);
        }
        return null;
    }

    default Long readLong(AreaType area, int db, int start) throws S7Exception {
        return readLong(area, db, start, buffer);
    }

    default Long readLong(AreaType area, int db, int start, byte[] buffer) throws S7Exception {
        if (readArea(area, db, start, 1, DataType.DWORD, buffer) > 0) {
            return S7.getDWordAt(buffer, 0);
        }
        return null;
    }

    default String readString(AreaType area, int db, int start, int length) throws S7Exception {
        return readString(area, db, start, length, StandardCharsets.UTF_8, buffer);
    }

    default String readString(AreaType area, int db, int start, int length, byte[] buffer) throws S7Exception {
        return readString(area, db, start, length, StandardCharsets.UTF_8, buffer);
    }

    default String readString(AreaType area, int db, int start, int length, Charset charset) throws S7Exception {
        return readString(area, db, start, length, charset, buffer);
    }

    default String readString(AreaType area, int db, int start, int length, Charset charset, byte[] buffer) throws S7Exception {
        if (readArea(area, db, start, length, DataType.BYTE, buffer) > 0) {
            return S7.getStringAt(buffer, 0, length);
        }
        return null;
    }

    default boolean writeBit(AreaType area, int db, int start, int bitPos, boolean value) throws S7Exception {
        // reset first byte?
        S7.setByteAt(buffer, 0, (byte) 0x00);
        S7.setBitAt(buffer, 0, bitPos, value);
        return writeArea(area, db, (start * 8) + bitPos, 1, DataType.BIT, buffer);
    }

    default boolean writeByte(AreaType area, int db, int start, byte value) throws S7Exception {
        S7.setByteAt(buffer, 0, value);
        return writeArea(area, db, start, 1, DataType.BYTE, buffer);
    }

    default boolean writeBytes(AreaType area, int db, int start, byte[] values) throws S7Exception {
        System.arraycopy(values, 0, buffer, 0, values.length);
        return writeArea(area, db, start, values.length, DataType.BYTE, buffer);
    }

    default boolean writeInt(AreaType area, int db, int start, int value) throws S7Exception {
        S7.setDIntAt(buffer, 0, value);
        return writeArea(area, db, start, 1, DataType.DINT, buffer);
    }

    default boolean writeLong(AreaType area, int db, int start, long value) throws S7Exception {
        S7.setDWordAt(buffer, 0, value);
        return writeArea(area, db, start, 1, DataType.DWORD, buffer);
    }

    default boolean writeString(AreaType area, int db, int start, String value) throws S7Exception {
        return writeString(area, db, start, value, StandardCharsets.UTF_8);
    }

    default boolean writeString(AreaType area, int db, int start, String value, Charset charset) throws S7Exception {
        byte[] values = Objects.requireNonNull(value).getBytes(charset);
        return writeBytes(area, db, start, values);
    }

    public int readArea(AreaType area, int db, int start, int amount, DataType wordLen, byte[] buffer) throws S7Exception;

    public boolean writeArea(AreaType area, int db, int start, int amount, DataType type, byte[] buffer) throws S7Exception;



}
