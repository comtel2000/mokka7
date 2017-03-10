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
package org.comtel2000.mokka7.util;

import java.util.ResourceBundle;

/**
 *
 * @author comtel
 *
 */
public interface ReturnCode {

    static final ResourceBundle bundle = ResourceBundle.getBundle("mokka7");
    static final String BUNDLE_ERR_PREFIX = "err_";

    public static final int SUCCEED = 0x00;
    public static final int TCP_CONNECTION_FAILED = 0x0001;
    public static final int TCP_DATA_SEND = 0x0002;
    public static final int TCP_DATA_RECV = 0x0003;
    public static final int TCP_DATA_RECV_TOUT = 0x0004;
    public static final int TCP_CONNECTION_RESET = 0x0005;
    public static final int ISO_INVALID_PDU = 0x0006;
    public static final int ISO_CONNECTION_FAILED = 0x0007;
    public static final int ISO_NEGOTIATING_PDU = 0x0008;
    public static final int S7_INVALID_PDU = 0x0009;
    public static final int S7_DATA_READ = 0x000A;
    public static final int S7_DATA_WRITE = 0x000B;
    public static final int S7_BUFFER_TOO_SMALL = 0x000C;
    public static final int S7_FUNCTION_ERROR = 0x000D;
    public static final int S7_INVALID_PARAMS = 0x000E;

    public static final int CODE7_OK = 0x0000;
    public static final int CODE7_ADDRESS_OUT_OF_RANGE = 0x0005;
    public static final int CODE7_INVALID_TRANSPORT_SIZE = 0x0006;
    public static final int CODE7_WRITE_DATA_SIZE_MISMATCH = 0x0007;
    public static final int CODE7_RES_ITEM_NOT_AVAILABLE = 0x000A;
    public static final int CODE7_RES_ITEM_NOT_AVAILABLE1 = 0xD209;
    public static final int CODE7_INVALID_VALUE = 0xDC01;
    public static final int CODE7_NEED_PASSWORD = 0xD241;
    public static final int CODE7_INVALID_PASSWORD = 0xD602;
    public static final int CODE7_NO_PASSWORD_TO_CLEAR = 0xD604;
    public static final int CODE7_NO_PASSWORD_TO_SET = 0xD605;
    public static final int CODE7_FUN_NOT_AVAILABLE = 0x8104;
    public static final int CODE7_DATA_OVER_PDU = 0x8500;

    public static final int ERR_TCP_UNREACHABLE_HOST = 0x00002751;

    public static final int ERR_ISO_CONNECT = 0x00010000;
    public static final int ERR_ISO_INVALID_PDU = 0x00030000;
    public static final int ERR_ISO_INVALID_DATA_SIZE = 0x00040000;

    public static final int ERR_NEGOTIATING_PDU = 0x00100000;
    public static final int ERR_INVALID_PARAMS = 0x00200000;
    public static final int ERR_JOB_PENDING = 0x00300000;
    public static final int ERR_TOO_MANY_ITEMS = 0x00400000;
    public static final int ERR_INVALID_WORD_LEN = 0x00500000;
    public static final int ERR_PARTIAL_DATA_WRITTEN = 0x00600000;
    public static final int ERR_SIZE_OVER_PDU = 0x00700000;
    public static final int ERR_INVALID_PLC_ANSWER = 0x00800000;
    public static final int ERR_ADDRESS_OUT_OF_RANGE = 0x00900000;
    public static final int ERR_INVALID_TRANSPORT_SIZE = 0x00A00000;
    public static final int ERR_WRITE_DATA_SIZE_MISMATCH = 0x00B00000;
    public static final int ERR_ITEM_NOT_AVAILABLE = 0x00C00000;
    public static final int ERR_INVALID_VALUE = 0x00D00000;
    public static final int ERR_CANNOT_STARTPLC = 0x00E00000;
    public static final int ERR_ALREADY_RUN = 0x00F00000;
    public static final int ERR_CANNOT_STOPPLC = 0x01000000;
    public static final int ERR_CANNOT_COPY_RAM_TO_ROM = 0x01100000;
    public static final int ERR_CANNOT_COMPRESS = 0x01200000;
    public static final int ERR_ALREADY_STOP = 0x01300000;
    public static final int ERR_FUN_NOT_AVAILABLE = 0x01400000;
    public static final int ERR_UPLOAD_SEQUENCE_FAILED = 0x01500000;
    public static final int ERR_INVALID_DATA_SIZE_RECVD = 0x01600000;
    public static final int ERR_INVALID_BLOCK_TYPE = 0x01700000;
    public static final int ERR_INVALID_BLOCK_NUMBER = 0x01800000;
    public static final int ERR_INVALID_BLOCK_SIZE = 0x01900000;
    public static final int ERR_NEED_PASSWORD = 0x01D00000;
    public static final int ERR_INVALID_PASSWORD = 0x01E00000;
    public static final int ERR_NO_PASSWORD_TO_SET_OR_CLEAR = 0x01F00000;
    public static final int ERR_JOB_TIMEOUT = 0x02000000;
    public static final int ERR_PARTIAL_DATA_READ = 0x02100000;
    public static final int ERR_BUFFER_TOO_SMALL = 0x02200000;
    public static final int ERR_FUNCTION_REFUSED = 0x02300000;
    public static final int ERR_DESTROYING = 0x02400000;
    public static final int ERR_INVALID_PARAM_NUMBER = 0x02500000;
    public static final int ERR_CANNOT_CHANGE_PARAM = 0x02600000;
    public static final int ERR_FUNCTION_NOT_IMPLEMENTED = 0x02700000;

    public static String getErrorText(int errorCode) {
        try {
            return bundle.getString(BUNDLE_ERR_PREFIX + Integer.toHexString(errorCode));
        } catch (Exception e) {
            return String.format("error code: [0x%s]", Integer.toHexString(errorCode));
        }
    }

    public static int getCpuError(int error) {
        switch (error) {
            case CODE7_OK:
                return 0;
            case CODE7_ADDRESS_OUT_OF_RANGE:
                return ERR_ADDRESS_OUT_OF_RANGE;
            case CODE7_INVALID_TRANSPORT_SIZE:
                return ERR_INVALID_TRANSPORT_SIZE;
            case CODE7_WRITE_DATA_SIZE_MISMATCH:
                return ERR_WRITE_DATA_SIZE_MISMATCH;
            case CODE7_RES_ITEM_NOT_AVAILABLE:
            case CODE7_RES_ITEM_NOT_AVAILABLE1:
                return ERR_ITEM_NOT_AVAILABLE;
            case CODE7_DATA_OVER_PDU:
                return ERR_SIZE_OVER_PDU;
            case CODE7_INVALID_VALUE:
                return ERR_INVALID_VALUE;
            case CODE7_FUN_NOT_AVAILABLE:
                return ERR_FUN_NOT_AVAILABLE;
            case CODE7_NEED_PASSWORD:
                return ERR_NEED_PASSWORD;
            case CODE7_INVALID_PASSWORD:
                return ERR_INVALID_PASSWORD;
            case CODE7_NO_PASSWORD_TO_SET:
            case CODE7_NO_PASSWORD_TO_CLEAR:
                return ERR_NO_PASSWORD_TO_SET_OR_CLEAR;
            default:
                return ERR_FUNCTION_REFUSED;
        }
    }
}
