/*
 * PROJECT Mokka7 (fork of Snap7/Moka7)
 *
 * Copyright (c) 2017 J.Zimmermann (comtel2000)
 *
 * All rights reserved. This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Mokka7 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE whatever license you
 * decide to adopt.
 *
 * Contributors: J.Zimmermann - Mokka7 fork
 *
 */
package org.comtel2000.mokka7;

import java.time.LocalDateTime;

public class DateTimeSample extends ClientRunner {

    public DateTimeSample() {
        super();
    }

    @Override
    public void call(S7Client client) throws Exception {

        LocalDateTime date = client.getPlcDateTime();
        System.out.println(date);
        client.setPlcDateTime(date);
        
    }

    public static void main(String[] args) {
        new DateTimeSample();
    }
}
