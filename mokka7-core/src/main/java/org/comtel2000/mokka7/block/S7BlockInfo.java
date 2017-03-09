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
package org.comtel2000.mokka7.block;

import java.time.LocalDateTime;

import org.comtel2000.mokka7.util.S7;

/**
 *
 * @author Davide
 */
public class S7BlockInfo {

    public String author, family, header;
    public int blkFlags, blkLang, blkNumber, blkType, checksum, loadSize, localData, mc7Size, sbbLength, version;
    public LocalDateTime codeDate, intfDate;

    public static S7BlockInfo of(byte[] src, int pos) {
        S7BlockInfo info = new S7BlockInfo();
        info.decode(src, pos);
        return info;
    }

    protected void decode(byte[] buffer, int pos) {
        blkFlags = buffer[pos];
        blkLang = buffer[pos + 1];
        blkType = buffer[pos + 2];
        blkNumber = S7.getWordAt(buffer, pos + 3);
        loadSize = S7.getDIntAt(buffer, pos + 5);
        codeDate = LocalDateTime.of(1984, 1, 1, 0, 0).plusSeconds(S7.getWordAt(buffer, pos + 17) * 86400);
        intfDate = LocalDateTime.of(1984, 1, 1, 0, 0).plusSeconds(S7.getWordAt(buffer, pos + 23) * 86400);
        sbbLength = S7.getWordAt(buffer, pos + 25);
        localData = S7.getWordAt(buffer, pos + 29);
        mc7Size = S7.getWordAt(buffer, pos + 31);
        author = S7.getStringAt(buffer, pos + 33, 8);
        family = S7.getStringAt(buffer, pos + 41, 8);
        header = S7.getStringAt(buffer, pos + 49, 8);
        version = buffer[pos + 57];
        checksum = S7.getWordAt(buffer, pos + 59);
    }

    @Override
    public String toString() {
        return "S7BlockInfo [author=" + author + ", family=" + family + ", header=" + header + ", blkFlags=" + blkFlags + ", blkLang=" + blkLang
                + ", blkNumber=" + blkNumber + ", blkType=" + blkType + ", checksum=" + checksum + ", loadSize=" + loadSize + ", localData=" + localData
                + ", mc7Size=" + mc7Size + ", sbbLength=" + sbbLength + ", version=" + version + ", codeDate=" + codeDate + ", intfDate=" + intfDate + "]";
    }

}
