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
package org.comtel2000.mokka7.client.control;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class HexTableView extends TableView<Integer> {

    private final static String DEFAULT_STYLE = HexTableView.class.getResource("hex-table.css").toExternalForm();

    private final static char EMP_CHAR = '\u0020', DOT_CHAR = '.';
    private final static char HEX_DIGIT[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    private final StringBuilder sb = new StringBuilder();

    private byte[] data;

    private final StringProperty title = new SimpleStringProperty();

    public HexTableView() {
        this(32);
    }

    public HexTableView(int size) {
        super();
        init(size);
    }

    @Override
    public String getUserAgentStylesheet() {
        return DEFAULT_STYLE;
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setData(byte[] data) {
        this.data = Objects.requireNonNull(data);
        Platform.runLater(() -> initItems(Math.floorDiv(data.length, 16) + 1));
    }

    public byte[] getData() {
        return data;
    }

    private void init(int size) {

        setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        initItems(size);

        TableColumn<Integer, String> column = new TableColumn<>(title.get());
        column.textProperty().bind(title);

        column.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<String>(createItem(cellData.getValue())));

        getColumns().add(column);
        sortPolicyProperty().set(t -> false);
    }


    private void initItems(int size) {
        getItems().setAll(IntStream.range(0, size).boxed().collect(Collectors.toList()));
    }

    private String createItem(int value) {

        if (data == null || data.length == 0) {
            return null;
        }
        sb.setLength(0);
        int pos = value * 16;
        if (pos >= data.length) {
            return null;
        }
        int length = Math.min(16, data.length - pos);
        if (length < 1) {
            return null;
        }

        for (int i = 16; i >= 0; i -= 4) {
            sb.append(HEX_DIGIT[0x0F & value >>> i]);
        }
        sb.append('0').append(':').append(EMP_CHAR).append(EMP_CHAR);

        for (int i = 0; i < 16; i++) {
            if (i < length) {
                sb.append(HEX_DIGIT[0x0F & data[pos + i] >> 4]);
                sb.append(HEX_DIGIT[0x0F & data[pos + i]]);
            } else {
                sb.append(EMP_CHAR).append(EMP_CHAR);
            }
            sb.append(EMP_CHAR);
            if (i == 7) {
                sb.append(EMP_CHAR);
            }
        }
        sb.append(EMP_CHAR);
        for (int i = 0; i < 16; i++) {
            if (i >= length) {
                sb.append(EMP_CHAR);
                continue;
            }
            char ch = (char) (data[pos + i] & 0xFF);
            if (Character.isISOControl(ch) || Character.isWhitespace(ch)) {
                sb.append(DOT_CHAR);
                continue;
            }
            sb.append(ch);
        }
        return sb.toString();
    }

}
