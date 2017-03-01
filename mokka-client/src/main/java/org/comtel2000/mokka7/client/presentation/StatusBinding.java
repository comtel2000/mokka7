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
