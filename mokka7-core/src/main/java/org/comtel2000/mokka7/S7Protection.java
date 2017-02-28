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
 * See §33.19 of "System Software for S7-300/400 System and Standard Functions"
 *
 * @author Davide
 *
 */
public class S7Protection {

    public int anl_sch;
    public int bart_sch;
    public int sch_par;
    public int sch_rel;
    public int sch_schal;

    public static S7Protection of(byte[] src) {
        S7Protection pro = new S7Protection();
        pro.update(src);
        return pro;
    }

    protected void update(byte[] src) {
        sch_schal = S7.getWordAt(src, 2);
        sch_par = S7.getWordAt(src, 4);
        sch_rel = S7.getWordAt(src, 6);
        bart_sch = S7.getWordAt(src, 8);
        anl_sch = S7.getWordAt(src, 10);
    }

    @Override
    public String toString() {
        return "S7Protection [anl_sch=" + anl_sch + ", bart_sch=" + bart_sch + ", sch_par=" + sch_par + ", sch_rel=" + sch_rel + ", sch_schal=" + sch_schal
                + "]";
    }
}
