/*
 * PROJECT Mokka7 (fork of Snap7/Moka7)
 *
 * Copyright (c) 2017 J.Zimmermann (comtel2000)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Mokka7 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE whatever license you
 * decide to adopt.
 *
 * Contributors:
 *    J.Zimmermann    - Mokka7 fork
 *
 */
package org.comtel2000.mokka7.client;

import org.comtel2000.mokka7.client.presentation.MainView;
import org.comtel2000.mokka7.client.service.SessionManager;
import org.comtel2000.mokka7.metrics.MonitoredS7Client;
import org.slf4j.LoggerFactory;

import com.airhacks.afterburner.injection.Injector;

import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Mokka7App extends Application {

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(Mokka7App.class);

    private final SimpleDoubleProperty sceneWidthProperty = new SimpleDoubleProperty(1024);
    private final SimpleDoubleProperty sceneHeightProperty = new SimpleDoubleProperty(600);

    @Override
    public void start(Stage stage) throws Exception {

        String version = Mokka7App.class.getPackage().getImplementationVersion();
        
        stage.setTitle(String.format("Mokka7 client v.%s", version != null ? version : "DEV"));
        stage.setResizable(true);

        Injector.setLogger((t) -> logger.trace(t));

        Injector.setModelOrService(MonitoredS7Client.class, new MonitoredS7Client());

        SessionManager session = Injector.instantiateModelOrService(SessionManager.class);
        session.setSession(getClass().getName().toLowerCase());
        session.loadSession();

        session.bind(sceneWidthProperty, "scene.width");
        session.bind(sceneHeightProperty, "scene.height");

        MainView main = new MainView();

        final Scene scene = new Scene(main.getView(), sceneWidthProperty.get(), sceneHeightProperty.get());
        stage.setOnCloseRequest((e) -> {
            sceneWidthProperty.set(scene.getWidth());
            sceneHeightProperty.set(scene.getHeight());
            Injector.forgetAll();
            System.exit(0);
        });
        stage.setScene(scene);
        Image icon16 = new Image(getClass().getResourceAsStream("icon-16x16.png"));
        Image icon32 = new Image(getClass().getResourceAsStream("icon-32x32.png"));
        Image icon48 = new Image(getClass().getResourceAsStream("icon-48x48.png"));
        
        stage.getIcons().addAll(icon16, icon32, icon48);
        
        stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
