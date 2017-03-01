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
        return "S7CpuInfo [asName=" + asName + ", copyright=" + copyright + ", moduleName=" + moduleName + ", moduleTypeName=" + moduleTypeName + ", serialNumber="
                + serialNumber + "]";
    }
}
