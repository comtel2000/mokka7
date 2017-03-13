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
package org.comtel2000.mokka7.client.presentation.read;

import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.inject.Inject;

import org.comtel2000.mokka7.client.control.HexTableView;
import org.comtel2000.mokka7.client.presentation.StatusBinding;
import org.comtel2000.mokka7.client.service.CompletableService;
import org.comtel2000.mokka7.client.service.SessionManager;
import org.comtel2000.mokka7.metrics.MonitoredS7Client;
import org.comtel2000.mokka7.type.AreaType;
import org.comtel2000.mokka7.type.DataType;
import org.comtel2000.mokka7.util.S7;
import org.slf4j.LoggerFactory;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.BorderPane;

public class ReadViewPresenter implements Initializable {

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ReadViewPresenter.class);

    @Inject
    private MonitoredS7Client client;

    @Inject
    private SessionManager session;

    @Inject
    private StatusBinding bindings;

    @Inject
    private HexTableView table;

    @FXML
    private BorderPane pane;

    @FXML
    private ComboBox<AreaType> area;

    @FXML
    private ComboBox<DataType> dataType;

    @FXML
    private TextField db;

    @FXML
    private TextField start;


    @FXML
    private TextField amount;

    @FXML
    private Button read;

    @FXML
    private Button dbget;

    @FXML
    private TextField value;

    @FXML
    private Button write;

    @FXML
    private Button dbfill;

    private final byte[] buffer = new byte[16 * 128];

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        db.setText("200");
        addNumericValidation(db);
        session.bind(db.textProperty(), "read.db");
        start.setText("0");
        addNumericValidation(start);
        session.bind(start.textProperty(), "read.start");
        amount.setText("1");
        addNumericValidation(amount);
        session.bind(amount.textProperty(), "read.amount");
        area.getItems().addAll(AreaType.values());
        area.getSelectionModel().select(AreaType.DB);
        session.bind(area, "read.area");

        dataType.getItems().addAll(DataType.values());
        dataType.getSelectionModel().select(DataType.BYTE);
        session.bind(dataType, "read.dataType");

        read.disableProperty()
                .bind(bindings.progressProperty().or(bindings.connectedProperty().not()).or(db.textProperty().isEmpty()).or(start.textProperty().isEmpty())
                        .or(amount.textProperty().isEmpty()).or(area.getSelectionModel().selectedItemProperty().isNull())
                        .or(dataType.getSelectionModel().selectedItemProperty().isNull()));
        dbget.disableProperty().bind(read.disabledProperty().or(area.getSelectionModel().selectedItemProperty().isNotEqualTo(AreaType.DB)));
        write.disableProperty().bind(read.disabledProperty().or(value.textProperty().isEmpty()));
        dbfill.disableProperty().bind(write.disabledProperty().or(area.getSelectionModel().selectedItemProperty().isNotEqualTo(AreaType.DB)));

        area.disableProperty().bind(bindings.progressProperty());
        dataType.disableProperty().bind(bindings.progressProperty());
        db.disableProperty().bind(bindings.progressProperty().or(area.getSelectionModel().selectedItemProperty().isNotEqualTo(AreaType.DB)));
        start.disableProperty().bind(bindings.progressProperty());
        amount.disableProperty().bind(bindings.progressProperty());
        value.disableProperty().bind(bindings.progressProperty());

        table.setPrefWidth(600);
        table.setEditable(false);
        pane.setCenter(table);

    }

    private static void addNumericValidation(TextField field) {
        field.getProperties().put("vkType", "numeric");
        field.setTextFormatter(new TextFormatter<>(c -> {
            if (c.isContentChange()) {
                if (c.getControlNewText().length() == 0) {
                    return c;
                }
                try {
                    Integer.parseInt(c.getControlNewText());
                    return c;
                } catch (NumberFormatException e) {
                }
                return null;

            }
            return c;
        }));
    }

    @FXML
    void read(ActionEvent event) {
        Arrays.fill(buffer, (byte) 0);
        long time = System.currentTimeMillis();
        CompletableService
                .supply(() -> client.readArea(area.getSelectionModel().getSelectedItem(), Integer.valueOf(db.getText()), Integer.valueOf(start.getText()),
                        Integer.valueOf(amount.getText()), dataType.getSelectionModel().getSelectedItem(), buffer))
                .bindRunning(bindings.progressProperty()).onFailed(this::report).onSucceeded((size) -> updateTable(size, time)).start();
    }

    @FXML
    void dbget(ActionEvent event) {
        Arrays.fill(buffer, (byte) 0);
        long time = System.currentTimeMillis();
        CompletableService.supply(() -> client.dbGet(Integer.valueOf(db.getText()), buffer)).bindRunning(bindings.progressProperty()).onFailed(this::report)
                .onSucceeded((size) -> updateTable(size, time)).start();
    }

    @FXML
    void dbfill(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to fill DB block " + db.getText() + "?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }
        Arrays.fill(buffer, (byte) 0);
        try {
            byte fill = parseByte(value.getText());
            Arrays.fill(buffer, fill);
            long time = System.currentTimeMillis();
            CompletableService.supply(() -> client.dbFill(Integer.valueOf(db.getText()), fill)).bindRunning(bindings.progressProperty()).onFailed(this::report)
                    .onSucceeded((size) -> updateTable(size, time)).start();
        } catch (Exception e) {
            report(new Exception("parse " + value.getText() + " to byte failed", e));
        }
    }

    @FXML
    void write(ActionEvent event) {
        String v = value.getText();
        if (v == null || v.isEmpty()) {
            return;
        }
        int size;
        try {
            size = updateBytes(buffer, Integer.valueOf(start.getText()), dataType.getSelectionModel().getSelectedItem(), v, Integer.valueOf(amount.getText()));
        } catch (Exception e) {
            report(new Exception("write " + dataType.getSelectionModel().getSelectedItem() + " failed", e));
            return;
        }

        long time = System.currentTimeMillis();
        CompletableService
                .supply(() -> client.writeArea(area.getSelectionModel().getSelectedItem(), Integer.valueOf(db.getText()), Integer.valueOf(start.getText()),
                        Integer.valueOf(amount.getText()), dataType.getSelectionModel().getSelectedItem(), buffer))
                .bindRunning(bindings.progressProperty()).onFailed(this::report).onSucceeded((b) -> updateTable(b, size, time)).start();
    }

    private byte parseByte(String value) throws Exception {
        if (value == null || value.trim().isEmpty()) {
            return 0;
        }
        return (byte) (Integer.valueOf(value, 16) & 0xFF);
    }

    private void updateTable(boolean write, int size, long time) {
        if (size < 1 || !write) {
            table.setData(new byte[0]);
            return;
        }
        table.setData(Arrays.copyOfRange(buffer, 0, size));
        bindings.statusTextProperty().set(String.format("write (bytes: %d) done in %dms", size, System.currentTimeMillis() - time));
    }

    private void updateTable(int size, long time) {
        if (size < 1) {
            table.setData(new byte[0]);
            return;
        }
        table.setData(Arrays.copyOfRange(buffer, 0, size));
        //table.titleProperty().set(createTitle());
        String title = createTitle();
        bindings.statusTextProperty()
                .set(String.format("read %s (bytes: %d) done in %dms", title, size, System.currentTimeMillis() - time));
    }

    private String createTitle() {
        switch (area.getSelectionModel().getSelectedItem()) {
            case DB:
                return String.format("%s%s.%s%s [%s]", area.getSelectionModel().getSelectedItem(), db.getText(),
                        toString(dataType.getSelectionModel().getSelectedItem()), start.getText(), amount.getText());
            case MK:
                return String.format("%s%s (%s) [%s]", area.getSelectionModel().getSelectedItem(), start.getText(),
                        dataType.getSelectionModel().getSelectedItem(), amount.getText());
            default:
                return String.format("%s %s (%s) [%s]", area.getSelectionModel().getSelectedItem(), start.getText(),
                        dataType.getSelectionModel().getSelectedItem(), amount.getText());
        }
    }

    private static String toString(DataType type) {
        switch (type) {
            case BIT:
                return "DBX";
            case BYTE:
                return "DBB";
            case WORD:
                return "DBW";
            case DWORD:
                return "DBD";
            default:
                return String.format(" (%s) ", type);
        }
    }

    private static int updateBytes(byte[] buffer, int pos, DataType type, String value, int amount) throws Exception {
        switch (type) {
            case BIT:
                boolean b = "true".equals(value) || "1".equals(value);
                for (int i = pos; i < amount; i++) {
                    S7.setBitAt(buffer, i, 0, b);
                }
                return amount;
            case BYTE:
                byte by = (byte) (Integer.valueOf(value) & 0xFF);
                for (int i = pos; i < amount; i++) {
                    S7.setByteAt(buffer, i, by);
                }
                return amount;
            case WORD:
                int word = Short.valueOf(value);
                for (int i = pos; i < amount; i = i + 2) {
                    S7.setWordAt(buffer, i, word);
                }
                return amount * 2;
            case DWORD:
                int dword = Integer.valueOf(value);
                for (int i = pos; i < amount; i = i + 4) {
                    S7.setDWordAt(buffer, i, dword);
                }
                return amount * 4;
            default:
                return 0;
        }
    }

    private void report(Throwable th) {
        table.setData(new byte[0]);
        if (th != null) {
            logger.error(th.getMessage(), th);
            if (th.getCause() != null) {
                bindings.statusTextProperty().set(String.format("%s (%s)", th.getMessage(), th.getCause().getMessage()));
            } else {
                bindings.statusTextProperty().set(th.getMessage());
            }
        }
    }
}
