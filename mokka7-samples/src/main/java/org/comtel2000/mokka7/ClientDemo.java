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

/**
 *
 * @author Dave Nardella
 */
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.comtel2000.mokka7.AreaType;
import org.comtel2000.mokka7.ConnectionType;
import org.comtel2000.mokka7.DataType;
import org.comtel2000.mokka7.PlcCpuStatus;
import org.comtel2000.mokka7.ReturnCode;
import org.comtel2000.mokka7.S7;
import org.comtel2000.mokka7.S7BlockInfo;
import org.comtel2000.mokka7.S7Client;
import org.comtel2000.mokka7.S7CpInfo;
import org.comtel2000.mokka7.S7CpuInfo;
import org.comtel2000.mokka7.S7OrderCode;
import org.comtel2000.mokka7.S7Protection;
import org.comtel2000.mokka7.S7Szl;


public class ClientDemo {

    // If MakeAllTests = true, also DBWrite and Run/Stop tests will be performed
    private static final boolean makeAllTests = true;

    private static long elapsed;
    private static byte[] buffer = new byte[65536]; // 64K buffer (maximum for S7400 systems)
    private static final S7Client client = new S7Client();
    private static int ok = 0;
    private static int ko = 0;
    private static String ipAddress = "";
    private static int rack = 0; // Default 0 for S71200
    private static int slot = 0; // Default 0 for S71200
    private static int dbSample = 1; // Sample DB that must be present in the CPU
    private static int dataToMove; // Data size to read/write
    private static PlcCpuStatus currentStatus = PlcCpuStatus.UNKNOWN;

    static void testBegin(String FunctionName) {
        System.out.println();
        System.out.println("+================================================================");
        System.out.println("| " + FunctionName);
        System.out.println("+================================================================");
        elapsed = System.currentTimeMillis();
    }

    static void testEnd(int result) {
        if (result != 0) {
            ko++;
            error(result);
        } else {
            ok++;
        }
        System.out.println("Execution time " + (System.currentTimeMillis() - elapsed) + " ms");
    }

    static void error(int code) {
        System.out.println(ReturnCode.getErrorText(code));
    }

    static void blockInfo(int BlockType, int BlockNumber) {
        testBegin("GetAgBlockInfo()");

        S7BlockInfo block = client.getAgBlockInfo(BlockType, BlockNumber);
        if (block != null) {
            System.out.println("Block Flags     : " + Integer.toBinaryString(block.blkFlags));
            System.out.println("Block Number    : " + block.blkNumber);
            System.out.println("Block Languege  : " + block.blkLang);
            System.out.println("Load Size       : " + block.loadSize);
            System.out.println("SBB Length      : " + block.sbbLength);
            System.out.println("Local Data      : " + block.localData);
            System.out.println("MC7 Size        : " + block.mc7Size);
            System.out.println("Author          : " + block.author);
            System.out.println("Family          : " + block.family);
            System.out.println("Header          : " + block.header);
            System.out.println("Version         : " + block.version);
            System.out.println("Checksum        : 0x" + Integer.toHexString(block.checksum));
            System.out.println("Code Date       : " + DateTimeFormatter.ISO_DATE.format(block.codeDate));
            System.out.println("Interface Date  : " + DateTimeFormatter.ISO_DATE.format(block.intfDate));
        }
        testEnd(client.getLastError());
    }

    public static boolean dbGet() {
        testBegin("DBGet()");
        int sizeRead = client.dbGet(dbSample, buffer);
        testEnd(client.getLastError());
        if (client.getLastError() == 0) {
            dataToMove = sizeRead; // Stores DB size for next test
            System.out.println("DB " + dbSample + " - Size read " + dataToMove + " bytes");
            S7.hexDump(buffer, dataToMove);
            return true;
        }
        return false;
    }

    public static void readArea() {
        testBegin("ReadArea()");
        int result = client.readArea(AreaType.S7AreaDB, dbSample, 0, dataToMove, DataType.S7WLByte, buffer);
        if (result == 0) {
            System.out.println("DB " + dbSample + " succesfully read using size reported by DBGet()");
        }
        testEnd(result);
    }

    public static void writeArea() {
        testBegin("WriteArea()");
        int result = client.writeArea(AreaType.S7AreaDB, dbSample, 0, dataToMove, DataType.S7WLByte, buffer);
        if (result == 0) {
            System.out.println("DB " + dbSample + " succesfully written using size reported by DBGet()");
        }
        testEnd(result);
    }

    /**
     * Performs read and write on a given DB
     */
    public static void dbPlay() {
        // We use DBSample (default = DB 1) as DB Number
        // modify it if it doesn't exists into the CPU.
        if (dbGet()) {
            readArea();
            if (makeAllTests) {
                writeArea();
            }
        }
    }

    public static void delay(int millisec) {
        try {
            Thread.sleep(millisec);
        } catch (InterruptedException e) {
        }
    }

    public static void showStatus() {

        testBegin("GetplcStatus()");
        PlcCpuStatus status = client.getPlcStatus();

        System.out.println("PLC Status : " + status);

        currentStatus = status;
        testEnd(client.getLastError());
    }

    public static void doRun() {
        testBegin("PlcHotStart()");
        int result = client.setPlcHotStart();
        if (result == 0) {
            System.out.println("PLC Started");
        }
        testEnd(result);
    }

    public static void doStop() {
        testBegin("PlcStop()");
        int result = client.setPlcStop();
        if (result == 0) {
            System.out.println("PLC Stopped");
        }
        testEnd(result);
    }

    public static void runStop() {
        switch (currentStatus) {
            case RUN:
                doStop();
                delay(1000);
                doRun();
                break;
            case STOP:
                doRun();
                delay(1000);
                doStop();
            default:
                break;
        }
    }

    public static void getSysInfo() {
        testBegin("GetOrderCode()");
        S7OrderCode orderCode = client.getOrderCode();
        if (orderCode != null) {
            System.out.println("Order Code        : " + orderCode.getCode());
            System.out.println("Firmware version  : " + orderCode.getFirmware());
        }
        testEnd(client.getLastError());

        testBegin("GetCpuInfo()");
        S7CpuInfo cpuInfo = client.getCpuInfo();
        if (cpuInfo != null) {
            System.out.println("Module Type Name  : " + cpuInfo.moduleTypeName);
            System.out.println("Serial Number     : " + cpuInfo.serialNumber);
            System.out.println("AS Name           : " + cpuInfo.asName);
            System.out.println("CopyRight         : " + cpuInfo.copyright);
            System.out.println("Module Name       : " + cpuInfo.moduleName);
        }
        testEnd(client.getLastError());

        testBegin("GetCpInfo()");
        S7CpInfo cpInfo = client.getCpInfo();
        if (cpInfo != null) {
            System.out.println("Max PDU Length    : " + cpInfo.maxPduLength);
            System.out.println("Max connections   : " + cpInfo.maxConnections);
            System.out.println("Max MPI rate (bps): " + cpInfo.maxMpiRate);
            System.out.println("Max Bus rate (bps): " + cpInfo.maxBusRate);
        }
        testEnd(client.getLastError());
    }

    public static void getDateAndTime() {
        testBegin("GetPlcDateTime()");
        LocalDateTime date = client.getPlcDateTime();
        if (date != null) {
            System.out.println("CPU Date/Time : " + date);
        }
        testEnd(client.getLastError());
    }

    public static void syncDateAndTime() {
        testBegin("SetPlcSystemDateTime()");
        int result = client.setPlcSystemDateTime();
        testEnd(result);
    }

    public static void readSzl() {
        testBegin("ReadSZL() - ID : 0x0011, IDX : 0x0000");
        S7Szl szl = client.readSzl(0x0011, 0x0000, 1024);
        if (szl != null) {
            System.out.println("LENTHDR : " + szl.lenthdr);
            System.out.println("N_DR    : " + szl.n_dr);
            System.out.println("Size    : " + szl.dataSize);
            S7.hexDump(szl.data, szl.dataSize);
        }
        testEnd(client.getLastError());
    }

    public static void getProtectionScheme() {

        testBegin("GetProtection()");
        S7Protection protection = client.getProtection();
        if (protection != null) {
            System.out.println("sch_schal : " + protection.sch_schal);
            System.out.println("sch_par   : " + protection.sch_par);
            System.out.println("sch_rel   : " + protection.sch_rel);
            System.out.println("bart_sch  : " + protection.bart_sch);
            System.out.println("anl_sch   : " + protection.anl_sch);
        }
        testEnd(client.getLastError());
    }

    public static void summary() {
        System.out.println();
        System.out.println("+================================================================");
        System.out.println("Tests performed : " + (ok + ko));
        System.out.println("Passed          : " + ok);
        System.out.println("Failed          : " + ko);
        System.out.println("+================================================================");
    }

    public static boolean connect() {
        testBegin("connect()");
        client.setConnectionType(ConnectionType.OP);
        int result = client.connect(ipAddress, rack, slot);
        if (result == 0) {
            System.out.println("Connected to   : " + ipAddress + " (Rack=" + rack + ", Slot=" + slot + ")");
            System.out.println("PDU negotiated : " + client.getPduLength() + " bytes");
        }
        testEnd(result);
        return result == 0;
    }


    public static void PerformTests() {
        getSysInfo();
        getProtectionScheme();
        getDateAndTime();
        if (makeAllTests) {
            syncDateAndTime();
        }
        readSzl();
        showStatus();
        if (makeAllTests) {
            runStop();
        }
        blockInfo(S7.Block_SFC, 1); // get SFC 1 info (always present in a CPU)
        dbPlay();
        summary();
    }

    public static void usage() {
        System.out.println("Usage");
        System.out.println("  client <IP> [Rack=0 Slot=2]");
        System.out.println("Example");
        System.out.println("  client 192.168.1.101 0 2");
        System.out.println("or");
        System.out.println("  client 192.168.1.101");
    }

    public static void main(String[] args) throws IOException {
        if ((args.length != 1) && (args.length != 3)) {
            usage();
            return;
        }
        if (args.length == 3) {
            rack = Integer.valueOf(args[1]);
            slot = Integer.valueOf(args[2]);
        }
        ipAddress = args[0];

        if (connect()) {
            PerformTests();
        }
    }

}
