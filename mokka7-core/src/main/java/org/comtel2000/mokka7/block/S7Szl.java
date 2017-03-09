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

/**
 * System Status Lists buffer
 * 
 * @author Davide
 * @author comtel
 */
public class S7Szl {

    public byte[] data;
    public int dataSize;
    public int lenthdr;
    public int n_dr;

    public S7Szl(int bufferSize) {
        data = new byte[bufferSize];
    }

    public void copy(byte[] src, int srcPos, int destPos, int size) {
        System.arraycopy(src, srcPos, data, destPos, size);
    }
}
