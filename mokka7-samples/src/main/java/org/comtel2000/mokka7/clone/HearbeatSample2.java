package org.comtel2000.mokka7.clone;

import java.util.Arrays;

import org.comtel2000.mokka7.AreaType;
import org.comtel2000.mokka7.ClientRunner;
import org.comtel2000.mokka7.DataType;
import org.comtel2000.mokka7.S7;
import org.comtel2000.mokka7.S7Client;

public class HearbeatSample2 extends ClientRunner {

    public HearbeatSample2() {
        super();
    }

    @Override
    public void call(S7Client client) throws Exception {
        boolean snycFailed = false;
        Arrays.fill(buffer, (byte)0);
        for (int i = 0; i < 50; i++) {
            client.readArea(AreaType.S7AreaDB, 200, 34 * 8 + 0, 1, DataType.S7WLBit, buffer);
            boolean plcBit = S7.getBitAt(buffer, 0, 0);
            System.err.println("heartbeat: " + plcBit);
            if (snycFailed) {
                System.err.println("write: " + plcBit);
                S7.setBitAt(buffer, 0, 1, plcBit);
                client.writeArea(AreaType.S7AreaDB, 200, 34 * 8 + 1, 1, DataType.S7WLBit, buffer);
            }
            client.readArea(AreaType.S7AreaDB, 200, 34, 1, DataType.S7WLByte, buffer);
            boolean plmBit = S7.getBitAt(buffer, 0, 1);
            snycFailed = plcBit != plmBit;
            if (snycFailed) {
                System.err.println("sync failed: " + plcBit + "/" + plmBit);
            }
            Thread.sleep(500);
        }
    }
    public static void main(String[] args) {
        new HearbeatSample2();
    }
}
