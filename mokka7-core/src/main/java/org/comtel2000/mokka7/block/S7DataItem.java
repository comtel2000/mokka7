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

import org.comtel2000.mokka7.type.AreaType;
import org.comtel2000.mokka7.type.DataType;

/**
 *
 * @author comtel
 *
 */
public class S7DataItem {

    public AreaType area;
    public DataType type;
    public int result;
    public int db;
    public int start;
    public int amount;
    public byte[] data;
}
