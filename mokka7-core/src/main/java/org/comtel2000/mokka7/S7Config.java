package org.comtel2000.mokka7;

import org.comtel2000.mokka7.type.ConnectionType;

public class S7Config {
    private ConnectionType type;
    private String host;
    private int port;
    private int slot;
    private int rack;
    private int remoteTSAP;
    private int localTSAP;

    public S7Config() {
        localTSAP = 0x0100;
        remoteTSAP = -1;
        type = ConnectionType.PG;
        port = 102;
        slot = 0;
        rack = 0;

    }

    public ConnectionType getType() {
        return type;
    }

    /**
     * Sets the connection resource type, i.e. the way in which the Clients connects to a PLC.
     *
     * @param type ConnectionType
     */
    public void setType(ConnectionType type) {
        this.type = type;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getRemoteTSAP() {
        if (remoteTSAP < 0) {
            remoteTSAP = (type.getValue() << 8) + (rack * 0x20) + slot;
        }
        return remoteTSAP;
    }

    public void setRemoteTSAP(int remoteTSAP) {
        this.remoteTSAP = remoteTSAP;
    }

    public int getLocalTSAP() {
        return localTSAP;
    }

    public void setLocalTSAP(int localTSAP) {
        this.localTSAP = localTSAP;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public int getRack() {
        return rack;
    }

    public void setRack(int rack) {
        this.rack = rack;
    }



}
