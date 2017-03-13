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
package org.comtel2000.mokka7.client.presentation;

import java.net.URL;
import java.util.ResourceBundle;

import javax.inject.Inject;

import org.comtel2000.mokka7.client.presentation.blocklist.BlockListView;
import org.comtel2000.mokka7.client.presentation.chart.ChartView;
import org.comtel2000.mokka7.client.presentation.connect.ConnectView;
import org.comtel2000.mokka7.client.presentation.info.InfoView;
import org.comtel2000.mokka7.client.presentation.read.ReadView;
import org.comtel2000.mokka7.client.service.SessionManager;
import org.comtel2000.mokka7.metrics.MonitoredS7Client;
import org.slf4j.LoggerFactory;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;

public class MainViewPresenter implements Initializable {

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(MainViewPresenter.class);

    @Inject
    SessionManager session;

    @Inject
    StatusBinding bindings;

    @Inject
    MonitoredS7Client client;

    @FXML
    ProgressIndicator progress;

    @FXML
    Label status;

    @FXML
    TabPane tabPane;

    @FXML
    BorderPane mainPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        logger.info("init main view presenter");

        progress.visibleProperty().bind(bindings.progressProperty());
        status.textProperty().bind(bindings.statusTextProperty());

        ConnectView connect = new ConnectView();
        mainPane.setTop(connect.getView());

        mainPane.setCenter(tabPane);
        loadTabs();

        logger.debug("session id: {} ", session.getSessionName());
    }

    private void loadTabs() {
        tabPane.getTabs().add(buildTab("System Info", new InfoView().getView(), false));
        tabPane.getTabs().add(buildTab("Read/Write", new ReadView().getView(), false));
        tabPane.getTabs().add(buildTab("Block List", new BlockListView().getView(), false));
        tabPane.getTabs().add(buildTab("Chart", new ChartView().getView(), false));
    }


    private Tab buildTab(String name, Node node, boolean closable) {
        Tab tab = new Tab(name);
        tab.setClosable(closable);
        tab.setContent(node);
        return tab;
    }
}
