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
import java.util.ResourceBundle;

import javax.inject.Inject;

import org.comtel2000.mokka7.block.S7CpInfo;
import org.comtel2000.mokka7.block.S7CpuInfo;
import org.comtel2000.mokka7.block.S7OrderCode;
import org.comtel2000.mokka7.client.presentation.StatusBinding;
import org.comtel2000.mokka7.client.service.SessionManager;
import org.comtel2000.mokka7.metrics.MonitoredS7Client;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

public class InfoViewPresenter implements Initializable {

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
        reset();
        bindings.orderCodeProperty().addListener((l, a, o) -> update(o));
        bindings.cpuInfoProperty().addListener((l, a, o) -> update(o));
        bindings.cpInfoProperty().addListener((l, a, o) -> update(o));
    }

    private void update(S7OrderCode o) {
        orderCode.setText(o == null ? null : o.getCode());
        version.setText(o == null ? null : o.getFirmware());
    }

    private void update(S7CpInfo o) {
        maxPDU.setText(o == null ? null : Integer.toString(o.maxPduLength));
        maxMPI.setText(o == null ? null : Integer.toString(o.maxMpiRate));
        maxActConn.setText(o == null ? null : Integer.toString(o.maxConnections));
        maxCBUS.setText(o == null ? null : Integer.toString(o.maxBusRate));
    }

    private void update(S7CpuInfo o) {
        moduleType.setText(o == null ? null : o.moduleTypeName);
        serial.setText(o == null ? null : o.serialNumber);
        vendorCopyright.setText(o == null ? null : o.copyright);
        asName.setText(o == null ? null : o.asName);
        moduleName.setText(o == null ? null : o.moduleName);
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

}
