package org.comtel2000.mokka7.client.presentation.connect;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import org.comtel2000.mokka7.S7Client;
import org.comtel2000.mokka7.S7CpInfo;
import org.comtel2000.mokka7.S7OrderCode;
import org.comtel2000.mokka7.client.presentation.StatusBinding;
import org.comtel2000.mokka7.client.service.SessionManager;
import org.comtel2000.mokka7.exception.S7Exception;
import org.slf4j.LoggerFactory;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ConnectViewPresenter implements Initializable {


    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(ConnectViewPresenter.class);

    @Inject
    StatusBinding log;

    @Inject
    SessionManager session;

    @Inject
    S7Client client;

    @FXML
    private Button connect;

    @FXML
    private Button disconnect;

    @FXML
    private TextField host;

    @FXML
    private ChoiceBox<Integer> rack;

    @FXML
    private ChoiceBox<Integer> slot;


    @FXML
    private Label label0;

    @FXML
    private Label label1;

    @FXML
    private Label label2;

    @FXML
    private Label label3;

    @FXML
    private Label label4;

    @FXML
    private Label label5;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        rack.getItems().addAll(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8));
        rack.getSelectionModel().select(0);
        slot.getItems().addAll(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8));
        slot.getSelectionModel().select(0);

        session.bind(host.textProperty(), "connect.host");

        connect.disableProperty().bind(log.connectedProperty());
        host.disableProperty().bind(log.connectedProperty());
        rack.disableProperty().bind(log.connectedProperty());
        slot.disableProperty().bind(log.connectedProperty());

        disconnect.disableProperty().bind(log.connectedProperty().not());

        log.connectedProperty().addListener((l, a, con) -> label0.setText(con ? "online" : "offine"));

        reset();
    }

    @FXML
    public void connect() {
        log.progressProperty().set(true);
        CompletableFuture
                .supplyAsync(() -> {
                    try {
                        return client.connect(host.getText(), rack.getSelectionModel().getSelectedItem(), slot.getSelectionModel().getSelectedItem());
                    } catch (S7Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .whenCompleteAsync(this::updateFields, Platform::runLater);
    }

    @FXML
    public void disconnect() {
        CompletableFuture.runAsync(() -> client.disconnect()).whenCompleteAsync((a, th) -> log.connectedProperty().set(client.connected), Platform::runLater);
    }

    private void updateFields(boolean result, Throwable th) {
        report(th);
        log.progressProperty().set(false);
        log.connectedProperty().set(result);
        if (result) {
            CompletableFuture.supplyAsync(() -> {
                try {
                    return client.getOrderCode();
                } catch (S7Exception e) {
                    throw new RuntimeException(e);
                }
            }).whenCompleteAsync(this::update, Platform::runLater);
        } else {
            log.statusTextProperty().set("error code: " + result);
        }
    }

    private void update(S7OrderCode code, Throwable th) {
        logger.debug("{}", code);
        report(th);
        if (code != null) {
            label1.setText(code.getCode());
            label2.setText("v." + code.getFirmware());
        }
        CompletableFuture.supplyAsync(() -> {
            try {
                return client.getCpInfo();
            } catch (S7Exception e) {
                throw new RuntimeException(e);
            }
        }).whenCompleteAsync(this::update, Platform::runLater);
    }

    private void update(S7CpInfo info, Throwable th) {
        logger.debug("{}", info);
        report(th);
        if (info != null) {
            label3.setText("Max PDU: " + info.maxPduLength);
            label4.setText("Max Con: " + info.maxConnections);
            label4.setText("MPI/Bus: " + info.maxMpiRate + "/" + info.maxBusRate);
        }
    }

    private void reset() {
        label0.setText(null);
        label1.setText(null);
        label2.setText(null);
        label3.setText(null);
        label4.setText(null);
        label5.setText(null);
    }

    private void report(Throwable th) {
        if (th != null) {
            log.statusTextProperty().set(th.getMessage());
        }
    }
}
