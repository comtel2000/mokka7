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

import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import javax.annotation.PreDestroy;

import org.comtel2000.mokka7.S7Client;
import org.comtel2000.mokka7.S7Config;
import org.comtel2000.mokka7.exception.S7Exception;
import org.comtel2000.mokka7.metrics.MonitoredS7Client;
import org.slf4j.LoggerFactory;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * Ping watchdog to check host availability of host name or ip address
 *
 * @author comtel
 *
 */
public class ScheduledReaderService implements AutoCloseable {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ScheduledReaderService.class);

    private final ScheduledExecutorService service;

    private ScheduledFuture<?> future;

    private ChartDataItem[] items;

    private final BooleanProperty running = new SimpleBooleanProperty(false);

    private MonitoredS7Client client;

    public ScheduledReaderService() {
        this(Executors.newSingleThreadScheduledExecutor((r) -> {
            Thread th = new Thread(r);
            th.setName("reader-service-" + th.getId());
            th.setDaemon(true);
            return th;
        }));
    }

    public ScheduledReaderService(ScheduledExecutorService es) {
        this.service = Objects.requireNonNull(es);
    }


    public void setItems(ChartDataItem[] list) {
        items = list;
    }


    public void start(S7Config config, long millis, BiConsumer<ChartDataItem[], Throwable> consumer) {
        if (isRunning()){
            stop();
        }
        if (millis < 11) {
            throw new IllegalArgumentException("delay must be greater 10ms");
        }
        if (items == null || items.length == 0) {
            throw new IllegalArgumentException("no items registered");
        }
        client = new MonitoredS7Client();
        client.setConfig(config);
        try {
            client.connect();
        } catch (S7Exception e) {
            consumer.accept(null, e);
            return;
        }
        
        future = service.scheduleWithFixedDelay(() -> read(client, consumer), millis, millis, TimeUnit.MILLISECONDS);
        running.set(!future.isDone());
    }

    public void stop() {
        running.set(false);
        if (client != null){
            client.disconnect();
        }
        if (future == null) {
            return;
        }
        future.cancel(true);
        future = null;
    }

    public boolean isRunning() {
        if (future == null) {
            running.set(false);
            return running.get();
        }
        running.set(!future.isDone());
        return running.get();
    }

    private void read(S7Client client, BiConsumer<ChartDataItem[], Throwable> cons) {
        try {
            client.readMultiVars(items, items.length);
            cons.accept(items, null);
        } catch (S7Exception e) {
            logger.error(e.getMessage(), e);
            cons.accept(null, e);
            stop();
        }
    }

    @PreDestroy
    @Override
    public void close() throws Exception {
        running.set(false);
        service.shutdown();
        try {
            if (!service.awaitTermination(1, TimeUnit.SECONDS)) {
                service.shutdownNow();
                if (!service.awaitTermination(1, TimeUnit.SECONDS))
                    logger.error("Pool did not terminate");
            }
        } catch (InterruptedException ie) {
            service.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public BooleanProperty runningProperty() {
        return running;
    }

}
