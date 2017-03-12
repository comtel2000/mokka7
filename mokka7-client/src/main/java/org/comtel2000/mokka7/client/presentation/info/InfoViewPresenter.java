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

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;

public class InfoViewPresenter implements Initializable {

    @Inject
    private StatusBinding bindings;

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
        if (o == null) {
            orderCode.setText(null);
            version.setText(null);
            return;
        }
        orderCode.setText(o.getCode());
        version.setText(o.getFirmware());
    }

    private void update(S7CpInfo o) {
        if (o == null) {
            maxPDU.setText(null);
            maxMPI.setText(null);
            maxActConn.setText(null);
            maxCBUS.setText(null);
            return;
        }
        maxPDU.setText(Integer.toString(o.maxPduLength));
        maxMPI.setText(Integer.toString(o.maxMpiRate));
        maxActConn.setText(Integer.toString(o.maxConnections));
        maxCBUS.setText(Integer.toString(o.maxBusRate));
    }

    private void update(S7CpuInfo o) {
        if (o == null) {
            moduleType.setText(null);
            serial.setText(null);
            vendorCopyright.setText(null);
            asName.setText(null);
            moduleName.setText(null);
            return;
        }
        moduleType.setText(o.moduleTypeName);
        serial.setText(o.serialNumber);
        vendorCopyright.setText(o.copyright);
        asName.setText(o.asName);
        moduleName.setText(o.moduleName);
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
