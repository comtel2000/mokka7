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

import java.nio.ByteBuffer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class HexTableView extends TableView<Integer> {

    private final static String DEFAULT_STYLE = HexTableView.class.getResource("hex-table.css").toExternalForm();

    private final static char EMP_CHAR = '\u0020', DOT_CHAR = '.';
    private final static char HEX_DIGIT[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    private final StringBuilder sb = new StringBuilder();

    private ByteBuffer buffer;

    public HexTableView() {
        super();
        init();
    }

    @SuppressWarnings("unchecked")
    private void init() {
        getStyleClass().add("hex-table-view");
        // setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Integer, String> count = new TableColumn<>("OFFSET");
        count.setId("count-column");
        count.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<String>(createCountItem(cellData.getValue())));

        TableColumn<Integer, String> hex = new TableColumn<>("00 01 02 03 04 05 06 07  08 09 0A 0B 0C 0D 0E 0F");
        hex.setId("hex-column");
        hex.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<String>(createHexItem(cellData.getValue())));

        TableColumn<Integer, String> text = new TableColumn<>("TEXT");
        text.setId("text-column");
        text.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<String>(createItem(cellData.getValue())));

        getColumns().addAll(count, hex, text);
        sortPolicyProperty().set(t -> false);
        setColumnResizePolicy((param) -> true);
    }

    @Override
    public String getUserAgentStylesheet() {
        return DEFAULT_STYLE;
    }

    public void setData(byte[] buf) {
        buffer = ByteBuffer.wrap(buf);
        Platform.runLater(() -> initItems(Math.floorDiv(buf.length, 16) + 1));
    }

    public ByteBuffer getData() {
        return buffer;
    }

    private void initItems(int size) {
        getItems().setAll(IntStream.range(0, size).boxed().collect(Collectors.toList()));
    }

    private String createCountItem(int value) {

        int pos = getPosition(value);
        if (pos < 0) {
            return null;
        }
        int length = Math.min(16, buffer.limit() - pos);
        if (length < 1) {
            return null;
        }

        sb.setLength(0);
        for (int i = 16; i >= 0; i -= 4) {
            sb.append(HEX_DIGIT[0x0F & value >>> i]);
        }
        return sb.toString();
    }

    private String createHexItem(int value) {

        int pos = getPosition(value);
        if (pos < 0) {
            return null;
        }
        int length = Math.min(16, buffer.limit() - pos);
        if (length < 1) {
            return null;
        }
        sb.setLength(0);
        for (int i = 0; i < 16; i++) {
            if (i < length) {
                sb.append(HEX_DIGIT[0x0F & buffer.get(pos + i) >> 4]);
                sb.append(HEX_DIGIT[0x0F & buffer.get(pos + i)]);
            } else {
                sb.append(EMP_CHAR).append(EMP_CHAR);
            }
            sb.append(EMP_CHAR);
            if (i == 7) {
                sb.append(EMP_CHAR);
            }
        }
        return sb.toString();
    }

    private String createItem(int value) {

        int pos = getPosition(value);
        if (pos < 0) {
            return null;
        }
        int length = Math.min(16, buffer.limit() - pos);
        if (length < 1) {
            return null;
        }

        sb.setLength(0);
        for (int i = 0; i < 16; i++) {
            if (i >= length) {
                sb.append(EMP_CHAR);
                continue;
            }
            char ch = (char) (buffer.get(pos + i) & 0xFF);
            if (Character.isISOControl(ch) || Character.isWhitespace(ch)) {
                sb.append(DOT_CHAR);
                continue;
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    private int getPosition(int value) {
        if (buffer == null || buffer.limit() == 0) {
            return -1;
        }
        int pos = value * 16;
        if (pos >= buffer.limit()) {
            return -1;
        }
        return pos;
    }
}
