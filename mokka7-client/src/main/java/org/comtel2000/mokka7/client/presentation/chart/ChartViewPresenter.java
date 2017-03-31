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
package org.comtel2000.mokka7.client.presentation.chart;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.LongAdder;

import javax.inject.Inject;

import org.comtel2000.mokka7.client.presentation.StatusBinding;
import org.comtel2000.mokka7.client.service.SessionManager;
import org.comtel2000.mokka7.metrics.MonitoredS7Client;
import org.comtel2000.mokka7.type.AreaType;
import org.comtel2000.mokka7.type.DataType;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.BorderPane;

public class ChartViewPresenter implements Initializable {

    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ChartViewPresenter.class);

    @Inject
    private MonitoredS7Client client;

    @Inject
    private SessionManager session;

    @Inject
    private StatusBinding bindings;

    @FXML
    private BorderPane pane;

    @FXML
    private ComboBox<AreaType> area;

    @FXML
    private TextField db;

    @FXML
    private TextField start;

    @FXML
    private ComboBox<DataType> dataType;

    @FXML
    private Label intervalLabel;

    @FXML
    private Slider interval;

    @FXML
    private Button add;

    @FXML
    private ListView<ChartDataItem> chartList;

    @FXML
    private Button remove;

    @FXML
    private LineChart<Number, Number> chart;

    @FXML
    private NumberAxis timeAxis;

    @FXML
    private NumberAxis valueAxis;

    @FXML
    private Button toggleService;

    @FXML
    private Label rangeLabel;

    @FXML
    private Slider range;

    @FXML
    private Label historyLabel;

    @FXML
    private Slider history;

    private final LongAdder timer = new LongAdder();
    private final LongProperty timeout = new SimpleLongProperty();
    private final LongProperty timeRange = new SimpleLongProperty();
    private final LongProperty maxHistory = new SimpleLongProperty();

    @Inject
    private ScheduledReaderService service;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        db.setText("0");
        addNumericValidation(db);
        session.bind(db.textProperty(), "chart.db");
        start.setText("0");
        addNumericValidation(start);
        session.bind(start.textProperty(), "chart.start");
        area.getItems().addAll(AreaType.values());
        area.getSelectionModel().select(AreaType.DB);
        session.bind(area, "chart.area");

        session.bind(interval.valueProperty(), "chart.interval");
        session.bind(history.valueProperty(), "chart.history");
        session.bind(range.valueProperty(), "chart.range");

        dataType.getItems().addAll(DataType.values());
        dataType.getSelectionModel().select(DataType.BYTE);
        session.bind(dataType, "chart.dataType");

        timeAxis.setForceZeroInRange(false);
        timeAxis.setAutoRanging(false);

        timeAxis.setTickUnit(range.valueProperty().get() / 100);
        timeAxis.setMinorTickCount(0);
        timeAxis.setLowerBound(0);
        timeAxis.setUpperBound(range.valueProperty().intValue() + 1);

        chart.setAnimated(false);

        timeout.bind(interval.valueProperty());
        timeRange.bind(range.valueProperty());
        maxHistory.bind(history.valueProperty());

        timeRange.addListener((l, a, r) -> {
            double tickMarks = r.longValue() / 100;
            timeAxis.setTickUnit(tickMarks);
            timeAxis.setLowerBound(Math.max(0, timer.longValue() - timeRange.longValue()));
            timeAxis.setUpperBound(timeRange.longValue());
        });

        maxHistory.addListener((l, a, r) -> chartList.getItems().forEach(i -> i.setMaxHistory(r.intValue())));

        intervalLabel.textProperty().bind(timeout.asString("(%d)"));
        rangeLabel.textProperty().bind(timeRange.asString("(%d)"));
        historyLabel.textProperty().bind(maxHistory.asString("(%d)"));

        db.disableProperty().bind(bindings.progressProperty().or(area.getSelectionModel().selectedItemProperty().isNotEqualTo(AreaType.DB)));
        toggleService.disableProperty()
                .bind(bindings.progressProperty().or(Bindings.isEmpty(chartList.getItems())).or(bindings.connectedProperty().not())
                        .or(db.textProperty().isEmpty()).or(start.textProperty().isEmpty()).or(area.getSelectionModel().selectedItemProperty().isNull())
                        .or(dataType.getSelectionModel().selectedItemProperty().isNull()));

        chartList.setCellFactory((v) -> new ChartDataItemCell());
        chartList.disableProperty().bind(service.runningProperty());

        interval.disableProperty().bind(service.runningProperty());
        add.disableProperty().bind(service.runningProperty().or(Bindings.size(chartList.getItems()).greaterThan(10)));
        remove.disableProperty().bind(service.runningProperty().or(chartList.getSelectionModel().selectedItemProperty().isNull()));

        bindings.connectedProperty().addListener((l, a, c) -> {
            if (!c && service.isRunning()) {
                service.stop();
            }
        });

        service.runningProperty().addListener((l, a, running) -> Platform.runLater(() -> toggleService.setText(running ? "Stop Service" : "Start Service")));
    }

    static class ChartDataItemCell extends ListCell<ChartDataItem> {
        @Override
        public void updateItem(ChartDataItem item, boolean empty) {
            super.updateItem(item, empty);
            textProperty().unbind();
            if (item != null) {
                textProperty().bind(item.valueProperty().asString(item.getName() + " (%,d)"));
            } else {
                setText(null);
            }
        }
    }

    @FXML
    void add(ActionEvent event) {
        ChartDataItem item = new ChartDataItem(area.getSelectionModel().getSelectedItem(), dataType.getSelectionModel().getSelectedItem(),
                Integer.valueOf(db.getText()), Integer.valueOf(start.getText()));
        item.setMaxHistory(maxHistory.intValue());
        if (!chartList.getItems().contains(item)) {
            chartList.getItems().add(item);
        }
    }

    @FXML
    void remove(ActionEvent event) {
        ChartDataItem item = chartList.getSelectionModel().getSelectedItem();
        if (item != null) {
            chartList.getItems().remove(item);
        }
    }

    @FXML
    void toggleService(ActionEvent event) {

        if (service.isRunning()) {
            service.stop();
            return;
        }

        if (chartList.getItems().isEmpty()) {
            logger.debug("no items");
            return;
        }
        timer.reset();
        chart.getData().clear();

        timeAxis.setLowerBound(0);
        timeAxis.setUpperBound(timeRange.get() + 1);
        chart.requestLayout();

        final ChartDataItem[] list = chartList.getItems().stream().toArray(s -> new ChartDataItem[s]);
        for (int i = 0; i < list.length; i++) {
            ChartDataItem item = list[i];
            item.getData().clear();
            XYChart.Series<Number, Number> series = new XYChart.Series<>(item.getName(), item.getData());
            chart.getData().add(series);
        }

        service.setItems(list);
        service.start(client.getConfig(), timeout.get(), this::updateItems);

    }

    private void updateItems(ChartDataItem[] items, Throwable th) {
        Platform.runLater(() -> {
            if (th != null) {
                report(th);
                service.stop();
                return;
            }
            timer.increment();
            for (int i = 0; i < items.length; i++) {
                ChartDataItem item = items[i];
                item.create(timer.longValue());
            }

            if (timer.longValue() > timeRange.longValue() - 1) {
                timeAxis.setLowerBound(timer.longValue() - timeRange.longValue() - 1);
                timeAxis.setUpperBound(timer.longValue());
            }
        });

    }

    private static void addNumericValidation(TextField field) {
        field.getProperties().put("vkType", "numeric");
        field.setTextFormatter(new TextFormatter<>(c -> {
            if (c.isContentChange()) {
                if (c.getControlNewText().length() == 0) {
                    return c;
                }
                try {
                    Integer.parseInt(c.getControlNewText());
                    return c;
                } catch (NumberFormatException e) {
                }
                return null;

            }
            return c;
        }));
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
