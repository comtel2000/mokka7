package org.comtel2000.mokka7.clone;

import org.comtel2000.mokka7.AreaType;
import org.comtel2000.mokka7.ClientRunner;
import org.comtel2000.mokka7.S7Client;

/**
 * Clone bit of DB200.DBX34.0 to DB200.DBX34.1
 *
 * @author comtel
 *
 */
public class HearbeatSample1 extends ClientRunner {

    public HearbeatSample1() {
        super();
    }

    @Override
    public void call(S7Client client) throws Exception {
        boolean plcBit, clientBit;
        for (int i = 0; i < 1000; i++) {
            plcBit = client.readBit(AreaType.S7AreaDB, 200, 34, 0);
            clientBit = client.readBit(AreaType.S7AreaDB, 200, 34, 1);
            if (plcBit != clientBit) {
                System.out.println("update: " + plcBit + "->" + clientBit);
                client.writeBit(AreaType.S7AreaDB, 200, 34, 1, plcBit);
            }else{
                System.out.println("ok: " + plcBit + "/" + clientBit);
            }

            Thread.sleep(500);
        }
    }

    public static void main(String[] args) {
        new HearbeatSample1();
    }
}
