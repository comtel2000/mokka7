/*
 * PROJECT Mokka7 (fork of Moka7)
 *
 * Copyright (C) 2017 J.Zimmermann All rights reserved.
 *
 * Mokka7 is free software: you can redistribute it and/or modify it under the terms of the Lesser
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
package org.comtel2000.mokka7.client.presentation;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class StatusBinding {

    private final BooleanProperty connected = new SimpleBooleanProperty(false);
    private final BooleanProperty progress = new SimpleBooleanProperty(false);
    private final StringProperty statusText = new SimpleStringProperty("");

    public final BooleanProperty progressProperty() {
        return progress;
    }

    public final BooleanProperty connectedProperty() {
        return connected;
    }

    public final StringProperty statusTextProperty() {
        return statusText;
    }
}
