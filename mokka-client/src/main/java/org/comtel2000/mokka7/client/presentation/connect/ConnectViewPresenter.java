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
package org.comtel2000.mokka7.client.presentation.connect;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.comtel2000.mokka7.S7Client;
import org.comtel2000.mokka7.S7CpInfo;
import org.comtel2000.mokka7.S7OrderCode;
import org.comtel2000.mokka7.client.presentation.StatusBinding;
import org.comtel2000.mokka7.client.service.CompletableService;
import org.comtel2000.mokka7.client.service.PingWatchdogService;
import org.comtel2000.mokka7.client.service.SessionManager;
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
    StatusBinding log;

    @Inject
    SessionManager session;

    @Inject
    S7Client client;

    @Inject
    PingWatchdogService pingService;

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

        BooleanBinding disabled = log.connectedProperty().or(log.progressProperty());
        connect.disableProperty().bind(disabled.or(host.textProperty().isEmpty()));
        host.disableProperty().bind(disabled);
        rack.disableProperty().bind(disabled);
        slot.disableProperty().bind(disabled);
        disconnect.disableProperty().bind(log.connectedProperty().not().or(log.progressProperty()));


        log.connectedProperty().addListener((l, a, con) -> label0.setText(con ? String.format("online (%s)", host.getText()) : "offine"));
        pingService.setOnPingFailed(this::pingFailed);
        reset();
    }

    @FXML
    public void connect() {
        CompletableService.supply(() -> client.connect(host.getText(), rack.getSelectionModel().getSelectedItem(), slot.getSelectionModel().getSelectedItem()))
                .bindRunning(log.progressProperty()).onFailed(this::report).onSucceeded(this::updateFields).start();
    }

    @FXML
    public void disconnect() {
        pingService.stop();
        CompletableService.supply(() -> {
            client.disconnect();
            return true;
        }).bindRunning(log.progressProperty()).onComplete((b, th) -> log.connectedProperty().set(client.connected)).start();
    }


    private void pingFailed(Throwable th) {
        logger.error("ping failed", th);
        Platform.runLater(() -> log.statusTextProperty().set("connection lost to: " + host.getText()));
        disconnect();
    }

    private void updateFields(Boolean result) {

        if (result == null) {
            return;
        }
        log.connectedProperty().set(result);
        if (result) {
            pingService.setHost(host.getText());
            pingService.setTimeout(2000);
            try {
                pingService.start(1, TimeUnit.SECONDS);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            log.statusTextProperty().set("connected to: " + host.getText());
            CompletableService.supply(() -> client.getOrderCode()).bindRunning(log.progressProperty()).onFailed(this::report).onSucceeded(this::update).start();
        } else {
            log.statusTextProperty().set("error code: " + result);
        }
    }

    private void update(S7OrderCode code) {
        if (code != null) {
            label1.setText(code.getCode());
            label2.setText("v." + code.getFirmware());
        }
        CompletableService.supply(() -> client.getCpInfo()).bindRunning(log.progressProperty()).onFailed(this::report).onSucceeded(this::update).start();
    }

    private void update(S7CpInfo info) {
        logger.debug("{}", info);
        if (info != null) {
            label3.setText("Max PDU: " + info.maxPduLength);
            label4.setText("Max Con: " + info.maxConnections);
            label5.setText("MPI/Bus: " + info.maxMpiRate + "/" + info.maxBusRate);
        }
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
            if (th.getCause() != null){
                log.statusTextProperty().set(String.format("%s (%s)", th.getMessage(), th.getCause().getMessage()));
            }else{
                log.statusTextProperty().set(th.getMessage());
            }
        }
    }
}
