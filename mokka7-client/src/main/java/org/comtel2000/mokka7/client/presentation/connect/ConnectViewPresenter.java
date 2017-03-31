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
package org.comtel2000.mokka7.client.presentation.connect;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import javax.inject.Inject;

import org.comtel2000.mokka7.block.S7CpInfo;
import org.comtel2000.mokka7.block.S7CpuInfo;
import org.comtel2000.mokka7.block.S7OrderCode;
import org.comtel2000.mokka7.client.presentation.StatusBinding;
import org.comtel2000.mokka7.client.service.CompletableService;
import org.comtel2000.mokka7.client.service.PingWatchdogService;
import org.comtel2000.mokka7.client.service.SessionManager;
import org.comtel2000.mokka7.metrics.MonitoredS7Client;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ConnectViewPresenter implements Initializable {

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ConnectViewPresenter.class);

    @Inject
    private StatusBinding bindings;

    @Inject
    private SessionManager session;

    @Inject
    private MonitoredS7Client client;

    @Inject
    private PingWatchdogService pingService;

    @FXML
    private Button connect;

    @FXML
    private Button disconnect;

    @FXML
    private TextField host;

    @FXML
    private ComboBox<Integer> rack;

    @FXML
    private ComboBox<Integer> slot;


    @FXML
    private Label label0;

    @FXML
    private Label label1;

    @FXML
    private Label label2;

    @FXML
    private Label label3;

    @FXML
    private Label label4;

    @FXML
    private Label label5;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        rack.getItems().addAll(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8));
        rack.getSelectionModel().select(0);
        slot.getItems().addAll(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8));
        slot.getSelectionModel().select(0);

        session.bind(host.textProperty(), "connect.host");

        BooleanBinding disabled = bindings.connectedProperty().or(bindings.progressProperty());
        connect.disableProperty().bind(disabled.or(host.textProperty().isEmpty()));
        host.disableProperty().bind(disabled);
        rack.disableProperty().bind(disabled);
        slot.disableProperty().bind(disabled);
        disconnect.disableProperty().bind(bindings.connectedProperty().not().or(bindings.progressProperty()));


        bindings.connectedProperty().addListener((l, a, con) -> update(con));
        pingService.setOnPingFailed(this::pingFailed);
        reset();
    }

    private void update(boolean connected) {
        String text = connected ? String.format("online (%s)", host.getText()) : "offine";
        label0.setText(text);
        bindings.statusTextProperty().set(text);
    }

    @FXML
    void connect() {
        reset();
        bindings.statusTextProperty().set("try to connect to: " + host.getText());
        CompletableService.supply(() -> client.connect(host.getText(), rack.getSelectionModel().getSelectedItem(), slot.getSelectionModel().getSelectedItem()))
                .bindRunning(bindings.progressProperty()).onFailed(this::report).onSucceeded(this::updateFields).start();
    }

    @FXML
    void disconnect() {
        pingService.stop();
        CompletableService.supply(() -> {
            client.disconnect();
            return true;
        }).bindRunning(bindings.progressProperty()).onComplete((b, th) -> bindings.connectedProperty().set(client.isConnected())).start();
    }


    private void pingFailed(Throwable th) {
        logger.error("ping failed", th);
        Platform.runLater(() -> bindings.statusTextProperty().set("connection lost to: " + host.getText()));
        disconnect();
    }

    private void updateFields(Boolean result) {

        if (result == null) {
            return;
        }
        bindings.connectedProperty().set(result);
        if (result) {
            pingService.setHost(host.getText());
            pingService.setTimeout(2000);
            try {
                pingService.start(1000);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            bindings.statusTextProperty().set("connected to: " + host.getText());
            CompletableService.supply(() -> client.getOrderCode()).bindRunning(bindings.progressProperty()).onFailed(this::report).onComplete((o, th) -> update(o))
                    .start();
        } else {
            bindings.statusTextProperty().set("error code: " + result);
        }
    }

    private void update(S7OrderCode code) {
        bindings.orderCodeProperty().set(code);
        if (code != null) {
            label1.setText(code.getCode());
            label2.setText("v." + code.getFirmware());
        }
        CompletableService.supply(() -> client.getCpInfo()).bindRunning(bindings.progressProperty()).onFailed(this::report).onComplete((o, th) -> update(o)).start();
    }

    private void update(S7CpInfo info) {
        bindings.cpInfoProperty().set(info);
        logger.debug("{}", info);
        if (info != null) {
            label3.setText("Max PDU: " + info.maxPduLength);
            label4.setText("Max Con: " + info.maxConnections);
            label5.setText("MPI/Bus: " + info.maxMpiRate + "/" + info.maxBusRate);
        }
        CompletableService.supply(() -> client.getCpuInfo()).bindRunning(bindings.progressProperty()).onFailed(this::report).onComplete((o, th) -> update(o)).start();
    }

    private void update(S7CpuInfo info) {
        bindings.cpuInfoProperty().set(info);
    }

    private void reset() {
        label0.setText(null);
        label1.setText(null);
        label2.setText(null);
        label3.setText(null);
        label4.setText(null);
        label5.setText(null);
    }

    private void report(Throwable th) {
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
