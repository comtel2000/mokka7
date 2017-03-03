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
package org.comtel2000.mokka7.metrics;

import java.io.Closeable;
import java.util.concurrent.TimeUnit;

import org.comtel2000.mokka7.AreaType;
import org.comtel2000.mokka7.DataType;
import org.comtel2000.mokka7.S7Client;
import org.comtel2000.mokka7.S7DataItem;
import org.comtel2000.mokka7.exception.S7Exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.codahale.metrics.Timer;

public class MonitoredS7Client extends S7Client implements Closeable {

    private static final Logger logger = LoggerFactory.getLogger(MonitoredS7Client.class);

    protected final MetricRegistry metrics = new MetricRegistry();

    private final Slf4jReporter reporter =
            Slf4jReporter.forRegistry(metrics).outputTo(logger).convertRatesTo(TimeUnit.SECONDS).convertDurationsTo(TimeUnit.MILLISECONDS).build();

    protected final Counter receivedBytes = metrics.counter(MetricRegistry.name(getClass(), "received", "bytes"));
    protected final Counter sendBytes = metrics.counter(MetricRegistry.name(getClass(), "send", "bytes"));

    protected final Counter exceptions = metrics.counter(MetricRegistry.name(getClass(), "exception", "count"));

    protected final Timer reads = metrics.timer(MetricRegistry.name(getClass(), "read", "time"));
    protected final Timer writes = metrics.timer(MetricRegistry.name(getClass(), "write", "time"));
    protected final Timer multiReads = metrics.timer(MetricRegistry.name(getClass(), "multiread", "time"));
    protected final Timer multiWrites = metrics.timer(MetricRegistry.name(getClass(), "multiwrite", "time"));

    /**
     * {@link S7Client} registry of metric instances.
     *
     * @return metrics
     */
    public MetricRegistry getMetricRegistry() {
        return metrics;
    }

    /**
     * @see Slf4jReporter#start(long, TimeUnit)
     *
     * @param period the amount of time between polls
     * @param unit the unit for period
     */
    public void start(long period, TimeUnit unit) {
        reporter.start(period, unit);
    }

    /**
     * @see Slf4jReporter#stop()
     */
    public void stop() {
        reporter.stop();
    }

    /**
     * @see Slf4jReporter#report()
     */
    public void report() {
        reporter.report();
    }

    @Override
    protected boolean sendPacket(byte[] buffer, int len) throws S7Exception {
        sendBytes.inc(len);
        return super.sendPacket(buffer, len);
    }

    @Override
    protected int recvPacket(byte[] buffer, int start, int size) throws S7Exception {
        int reads = super.recvPacket(buffer, start, size);
        receivedBytes.inc(reads);
        return reads;
    }

    @Override
    public boolean readArea(AreaType area, int db, int start, int amount, DataType type, byte[] buffer) throws S7Exception {
        final Timer.Context context = reads.time();
        try {
            return super.readArea(area, db, start, amount, type, buffer);
        } finally {
            context.stop();
        }
    }

    @Override
    public boolean writeArea(AreaType area, int db, int start, int amount, DataType type, byte[] buffer) throws S7Exception {
        final Timer.Context context = writes.time();
        try {
            return super.writeArea(area, db, start, amount, type, buffer);
        } finally {
            context.stop();
        }
    }

    @Override
    public boolean writeMultiVars(S7DataItem[] items, int itemsCount) throws S7Exception {
        final Timer.Context context = multiWrites.time();
        try {
            return super.writeMultiVars(items, itemsCount);
        } finally {
            context.stop();
        }
    }

    @Override
    public boolean readMultiVars(S7DataItem[] items, int itemsCount) throws S7Exception {
        final Timer.Context context = multiReads.time();
        try {
            return super.readMultiVars(items, itemsCount);
        } finally {
            context.stop();
        }
    }

    @Override
    protected void buildException(int code, Throwable e) throws S7Exception {
        exceptions.inc();
        super.buildException(code, e);
    }
    @Override
    public void close() {
        reporter.close();
    }

}
