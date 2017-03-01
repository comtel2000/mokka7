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

import java.net.URL;
import java.util.ResourceBundle;

import javax.inject.Inject;

import org.comtel2000.mokka7.client.presentation.connect.ConnectView;
import org.comtel2000.mokka7.client.service.SessionManager;
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
	StatusBinding log;

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

		progress.visibleProperty().bind(log.progressProperty());
		status.textProperty().bind(log.statusTextProperty());

		ConnectView connect = new ConnectView();
		mainPane.setTop(connect.getView());

		loadTabs();


		logger.debug("session id: {} ", session.getSessionName());
	}


	private void loadTabs(){

//		tabPane.getTabs().add(buildTab("CONFIG", new ConfigView().getView(), false));
//		tabPane.getTabs().add(buildTab("READ", new ReadView().getView(), true));
//		tabPane.getTabs().add(buildTab("MULTIREAD", new MultiReadView().getView(), true));
//		tabPane.getTabs().add(buildTab("WRITE", new WriteView().getView(), true));
//		tabPane.getTabs().add(buildTab("GAUGE", new GaugeView().getView(), true));
//		tabPane.getTabs().add(buildTab("LED", new LEDView().getView(), true));
//		tabPane.getTabs().add(buildTab("CLOCK", new ClockView().getView(), true));
//		tabPane.getTabs().add(buildTab("SERIAL", new SerialView().getView(), true));
//		tabPane.getTabs().add(buildTab("XML", new XmlReadView().getView(), true));
//		tabPane.getTabs().add(buildTab("CHART", new ChartView().getView(), true));
//		tabPane.getTabs().add(buildTab("HEARTBEAT", new HeartBeatView().getView(), true));

	}


	private Tab buildTab(String name, Node node, boolean closable) {
		Tab tab = new Tab(name);
		tab.setClosable(closable);
		tab.setContent(node);
		return tab;
	}
}
