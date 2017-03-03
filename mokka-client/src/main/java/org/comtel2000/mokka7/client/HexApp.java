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

import org.comtel2000.mokka7.client.control.HexTableView;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class HexApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {

        stage.setTitle("HEX");
        stage.setResizable(true);

        HexTableView table = new HexTableView();
        table.titleProperty().set("HEX");
        table.setData(new byte[]{0,0,0,0,0,0,1,2,0,0,0,0,0,0,0,0,2,3});
        table.setOnMouseClicked((ev) -> {
            table.setData(new byte[]{1, 2, 3, 4, 5, 6, 7});
        });
        System.out.println(table.getData().length);
        BorderPane pane = new BorderPane(table);
        final Scene scene = new Scene(pane, 800, 600);
        stage.setOnCloseRequest((e) -> {
            System.exit(0);
        });
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
