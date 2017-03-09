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
package org.comtel2000.mokka7.client.service;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import javax.annotation.PreDestroy;

import org.slf4j.LoggerFactory;

/**
 * Ping watchdog to check host availability of host name or ip address
 *
 * @author comtel
 *
 */
public class PingWatchdogService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(PingWatchdogService.class);

    private static final int DEFAULT_TIMEOUT = 1000;

    private final ScheduledExecutorService service;
    private String host;
    private int timeout = DEFAULT_TIMEOUT;

    private ScheduledFuture<?> future;

    private Consumer<Throwable> consumer;

    public PingWatchdogService() {
        this(Executors.newSingleThreadScheduledExecutor((r) -> {
            Thread th = new Thread(r);
            th.setName("ping-watchdog-" + th.getId());
            th.setDaemon(true);
            return th;
        }));
    }

    public PingWatchdogService(ScheduledExecutorService es) {
        this.service = Objects.requireNonNull(es);
    }

    public void start(int delay, TimeUnit unit) throws UnknownHostException {
        stop();
        if (host == null || host.isEmpty()) {
            throw new IllegalArgumentException("host must not be null");
        }
        if (delay < 1) {
            throw new IllegalArgumentException("delay must be greater 0");
        }
        if (timeout < 1) {
            throw new IllegalArgumentException("delay must be greater 0");
        }
        final InetAddress address = InetAddress.getByName(host);
        final int tout = timeout;
        final Consumer<Throwable> cons = consumer;
        if (cons == null) {
            logger.warn("no registered ping failed consumer");
        }
        future = service.scheduleWithFixedDelay(() -> ping(address, tout, cons), delay, delay, Objects.requireNonNull(unit));
    }

    @PreDestroy
    public void stop() {
        if (future == null) {
            return;
        }
        future.cancel(true);
        future = null;
    }

    public boolean isRunning() {
        if (future == null) {
            return false;
        }
        return !future.isDone();
    }

    private void ping(InetAddress address, int tout, Consumer<Throwable> cons) {
        try {
            long time = 0;
            if (logger.isTraceEnabled()) {
                time = System.currentTimeMillis();
            }
            boolean reachable = address.isReachable(tout);
            if (logger.isTraceEnabled()) {
                time = System.currentTimeMillis() - time;
                logger.trace("ping [{}] time {}ms", address.getHostName(), time);
            }
            if (!reachable) {
                logger.warn("[{}] not reachable ({})", address.getHostName(), tout);
                if (cons != null) {
                    cons.accept(new IOException(String.format("host: %s not reachable (%s)", address.getHostName(), tout)));
                }
                stop();
            }
        } catch (IOException e) {
            if (cons != null) {
                cons.accept(e);
            } else {
                logger.error(e.getMessage(), e);
            }
            stop();
        }
    }

    public void setOnPingFailed(Consumer<Throwable> c) {
        this.consumer = c;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String h) {
        this.host = h;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

}
