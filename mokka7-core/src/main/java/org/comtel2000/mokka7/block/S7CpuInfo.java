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
package org.comtel2000.mokka7.block;

import org.comtel2000.mokka7.util.S7;

/**
 *
 * @author Davide
 * @author comtel
 */
public class S7CpuInfo {

    public String asName;
    public String copyright;
    public String moduleName;
    public String moduleTypeName;
    public String serialNumber;

    public static S7CpuInfo of(byte[] src, int pos) {
        S7CpuInfo info = new S7CpuInfo();
        info.update(src, pos);
        return info;
    }

    protected void update(byte[] src, int pos) {
        asName = S7.getStringAt(src, pos + 2, 24);
        copyright = S7.getStringAt(src, pos + 104, 26);
        moduleName = S7.getStringAt(src, pos + 36, 24);
        moduleTypeName = S7.getStringAt(src, pos + 172, 32);
        serialNumber = S7.getStringAt(src, pos + 138, 24);
    }

    @Override
    public String toString() {
        return "S7CpuInfo [asName=" + asName + ", copyright=" + copyright + ", moduleName=" + moduleName + ", moduleTypeName=" + moduleTypeName
                + ", serialNumber=" + serialNumber + "]";
    }
}
