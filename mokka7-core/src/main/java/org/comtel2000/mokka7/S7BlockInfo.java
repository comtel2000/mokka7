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

import java.time.LocalDateTime;
import java.time.ZoneOffset;

/**
 *
 * @author Davide
 */
public class S7BlockInfo {

    /** MilliSeconds between 1970/1/1 (Java time base) and 1984/1/1 (Siemens base) */
    private final static long S7_TIME_OFFSET = 441763200000L;

    public String author, family, header;
    public int blkFlags, blkLang, blkNumber, blkType, checksum, loadSize, localData, mc7Size, sbbLength, version;
    public LocalDateTime codeDate, intfDate;

    public static S7BlockInfo of(byte[] src, int pos) {
        S7BlockInfo info = new S7BlockInfo();
        info.update(src, pos);
        return info;
    }

    protected void update(byte[] buffer, int pos) {
        version = buffer[pos + 57];
        sbbLength = S7.getWordAt(buffer, pos + 25);
        mc7Size = S7.getWordAt(buffer, pos + 31);
        localData = S7.getWordAt(buffer, pos + 29);
        loadSize = S7.getDIntAt(buffer, pos + 5);
        long intf = (S7.getWordAt(buffer, pos + 23)) * 86400000L + S7_TIME_OFFSET;
        intfDate = LocalDateTime.ofEpochSecond(intf, 0, ZoneOffset.UTC);
        header = S7.getStringAt(buffer, pos + 49, 8);
        family = S7.getStringAt(buffer, pos + 41, 8);
        long block = (S7.getWordAt(buffer, pos + 17)) * 86400000L + S7_TIME_OFFSET;
        codeDate = LocalDateTime.ofEpochSecond(block, 0, ZoneOffset.UTC);
        checksum = S7.getWordAt(buffer, pos + 59);
        blkType = buffer[pos + 2];
        blkNumber = S7.getWordAt(buffer, pos + 3);
        blkLang = buffer[pos + 1];
        blkFlags = buffer[pos + 0];
        author = S7.getStringAt(buffer, pos + 33, 8);
    }

    @Override
    public String toString() {
        return "S7BlockInfo [author=" + author + ", family=" + family + ", header=" + header + ", blkFlags=" + blkFlags + ", blkLang=" + blkLang
                + ", blkNumber=" + blkNumber + ", blkType=" + blkType + ", checksum=" + checksum + ", loadSize=" + loadSize + ", localData=" + localData
                + ", mc7Size=" + mc7Size + ", sbbLength=" + sbbLength + ", version=" + version + ", codeDate=" + codeDate + ", intfDate=" + intfDate + "]";
    }

}
