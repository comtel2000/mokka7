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
package org.comtel2000.mokka7.client.presentation.info;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.inject.Inject;

import org.comtel2000.mokka7.client.presentation.StatusBinding;
import org.comtel2000.mokka7.client.service.SessionManager;
import org.comtel2000.mokka7.exception.S7Exception;
import org.comtel2000.mokka7.metrics.MonitoredS7Client;
import org.slf4j.LoggerFactory;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class InfoViewPresenter implements Initializable {

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(InfoViewPresenter.class);

    @Inject
    MonitoredS7Client client;

    @Inject
    SessionManager session;

    @Inject
    StatusBinding bindings;

    @FXML
    private BorderPane pane;

    @FXML
    private TextField orderCode;

    @FXML
    private TextField moduleType;

    @FXML
    private TextField serial;

    @FXML
    private TextField maxPDU;

    @FXML
    private TextField moduleName;

    @FXML
    private TextField asName;

    @FXML
    private TextField vendorCopyright;

    @FXML
    private TextField version;

    @FXML
    private TextField maxMPI;

    @FXML
    private TextField maxActConn;

    @FXML
    private TextField maxCBUS;


    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        bindings.connectedProperty().addListener((l, a, con) -> update(con));
    }

    private void update(boolean con) {
        if (!con) {
            return;
        }
        reset();
        try {
            Optional.ofNullable(client.getOrderCode()).ifPresent(o -> {
                orderCode.setText(o.getCode());
                version.setText(o.getFirmware());
            });
        } catch (S7Exception e) {
            logger.error(e.getMessage(), e);
        }

        try {
            Optional.ofNullable(client.getCpuInfo()).ifPresent(o -> {
                moduleType.setText(o.moduleTypeName);
                serial.setText(o.serialNumber);
                vendorCopyright.setText(o.copyright);
                asName.setText(o.asName);
                moduleName.setText(o.moduleName);
            });
        } catch (S7Exception e) {
            logger.error(e.getMessage(), e);
        }

        try {
            Optional.ofNullable(client.getCpInfo()).ifPresent(o -> {
                maxPDU.setText(Integer.toString(o.maxPduLength));
                maxMPI.setText(Integer.toString(o.maxMpiRate));
                maxActConn.setText(Integer.toString(o.maxConnections));
                maxCBUS.setText(Integer.toString(o.maxBusRate));
            });
        } catch (S7Exception e) {
            logger.error(e.getMessage(), e);
        }


    }

    private void reset() {
        orderCode.setText(null);
        moduleType.setText(null);
        serial.setText(null);
        maxPDU.setText(null);
        moduleName.setText(null);
        asName.setText(null);
        vendorCopyright.setText(null);
        version.setText(null);
        maxMPI.setText(null);
        maxActConn.setText(null);
        maxCBUS.setText(null);
    }

    @FXML
    void read(ActionEvent event) {}

}
