package org.comtel2000.mokka7.clone;

import org.comtel2000.mokka7.AreaType;
import org.comtel2000.mokka7.ClientRunner;
import org.comtel2000.mokka7.S7;
import org.comtel2000.mokka7.S7Client;

/**
 * Clone bit of DB200.DBX34.0 to DB200.DBX34.1
 *
 * @author comtel
 *
 */
public class HearbeatSample extends ClientRunner {

    public HearbeatSample() {
        super();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void call(S7Client client) throws InterruptedException {

        int result;
        for (int i = 0; i < 100; i++) {
            result = client.readArea(AreaType.S7AreaDB, 200, 34, 1, buffer);
            checkResult(result);
            if (result == 0) {
                // hexDump(buffer, 40);
                boolean plcBit = S7.getBitAt(buffer, 0, 0);
                System.err.println("heartbeat: " + plcBit);
                S7.setBitAt(buffer, 0, 1, S7.getBitAt(buffer, 0, 0));

                result = client.writeArea(AreaType.S7AreaDB, 200, 34, 1, buffer);
                checkResult(result);
                result = client.readArea(AreaType.S7AreaDB, 200, 34, 1, buffer);
                checkResult(result);
                boolean plmBit = S7.getBitAt(buffer, 0, 1);
                if (plcBit != plmBit) {
                    System.err.println("sync failed: " + plcBit + "/" + plmBit);
                }
            }
            Thread.sleep(1000);
        }
    }

    public static void main(String[] args) {
        new HearbeatSample();
    }



}
