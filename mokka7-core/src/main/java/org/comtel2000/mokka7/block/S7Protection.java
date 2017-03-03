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
 * See §33.19 of "System Software for S7-300/400 System and Standard Functions"
 *
 * @author Davide
 * @author comtel
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
