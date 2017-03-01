package org.comtel2000.mokka7.clone;

import org.comtel2000.mokka7.AreaType;
import org.comtel2000.mokka7.ClientRunner;
import org.comtel2000.mokka7.DataType;
import org.comtel2000.mokka7.S7;
import org.comtel2000.mokka7.S7Client;

public class HearbeatSample3 extends ClientRunner {

    public HearbeatSample3() {
        super();
    }

    @Override
    public void call(S7Client client) throws Exception {
        boolean plcBit, clientBit;
        for (int i = 0; i < 1000; i++) {
            client.readArea(AreaType.S7AreaDB, 200, 34, 1, DataType.S7WLByte, buffer);
            plcBit = S7.getBitAt(buffer, 0, 0);
            clientBit = S7.getBitAt(buffer, 0, 1);
            if (plcBit != clientBit) {
                System.err.println("update: " + plcBit + "/" + clientBit);
                S7.setBitAt(buffer, 0, 1, plcBit);
                client.writeArea(AreaType.S7AreaDB, 200, 34, 1, DataType.S7WLByte, buffer);
            }
            Thread.sleep(500);
        }

        // client.WriteArea(S7.S7AreaDB, 200, 34, 1, new
        // byte[]{0x00});
    }

    public static void main(String[] args) {
        new HearbeatSample3();
    }
}
