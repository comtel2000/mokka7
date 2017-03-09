/*
 * PROJECT Mokka7 (fork of Snap7/Moka7)
 *
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
 *    J.Zimmermann    - Mokka7 fork
 *
 */
package org.comtel2000.mokka7.client.presentation;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class StatusBinding {

    private final BooleanProperty connected = new SimpleBooleanProperty(false);
    private final BooleanProperty progress = new SimpleBooleanProperty(false);
    private final StringProperty statusText = new SimpleStringProperty("");

    private final ObjectProperty<byte[]> hexData = new SimpleObjectProperty<>();

    public final BooleanProperty progressProperty() {
        return progress;
    }

    public final BooleanProperty connectedProperty() {
        return connected;
    }

    public final StringProperty statusTextProperty() {
        return statusText;
    }

    public final ObjectProperty<byte[]> hexDataProperty() {
        return hexData;
    }
}
