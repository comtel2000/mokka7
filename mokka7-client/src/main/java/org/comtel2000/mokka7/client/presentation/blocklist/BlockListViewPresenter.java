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
package org.comtel2000.mokka7.client.presentation.blocklist;

import java.net.URL;
import java.util.ResourceBundle;

import javax.inject.Inject;

import org.comtel2000.mokka7.block.BlockSubType;
import org.comtel2000.mokka7.block.S7BlockList;
import org.comtel2000.mokka7.client.presentation.StatusBinding;
import org.comtel2000.mokka7.client.service.CompletableService;
import org.comtel2000.mokka7.metrics.MonitoredS7Client;
import org.slf4j.LoggerFactory;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class BlockListViewPresenter implements Initializable {

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(BlockListViewPresenter.class);

    @Inject
    MonitoredS7Client client;

    @Inject
    StatusBinding bindings;

    @FXML
    private TextField ob;

    @FXML
    private TextField fc;

    @FXML
    private TextField sfb;

    @FXML
    private TextField sdb;

    @FXML
    private TextField db;

    @FXML
    private TextField sfc;

    @FXML
    private TextField fb;

    @FXML
    private Button refresh;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        reset();
        refresh.disableProperty().bind(bindings.progressProperty().or(bindings.connectedProperty().not()));
    }

    @FXML
    void refresh(ActionEvent event) {
        CompletableService.supply(() -> client.getS7BlockList()).bindRunning(bindings.progressProperty()).onFailed(this::report).onSucceeded(this::update)
                .start();
    }

    private void update(S7BlockList list) {
        logger.debug("set block list: {}", list);
        ob.setText(Integer.toString(list.getSize(BlockSubType.OB)));
        fc.setText(Integer.toString(list.getSize(BlockSubType.FC)));
        sfb.setText(Integer.toString(list.getSize(BlockSubType.SFB)));
        sfc.setText(Integer.toString(list.getSize(BlockSubType.SFC)));
        sdb.setText(Integer.toString(list.getSize(BlockSubType.SDB)));
        db.setText(Integer.toString(list.getSize(BlockSubType.DB)));
        fb.setText(Integer.toString(list.getSize(BlockSubType.FB)));
    }

    private void reset() {
        ob.setText(null);
        fc.setText(null);
        sfb.setText(null);
        sfc.setText(null);
        sdb.setText(null);
        db.setText(null);
        fb.setText(null);
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
