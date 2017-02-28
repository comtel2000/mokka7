package org.comtel2000.mokka7;

import org.comtel2000.mokka7.AreaType;
import org.comtel2000.mokka7.S7Client;

/**
 * write a running bit pointer
 * 
 * @author comtel
 *
 */
public class WriteBitSample extends ClientRunner {

    final int db = 201;
    final int start = 0;

    public WriteBitSample() {
        super();
    }

    @Override
    public void call(S7Client client) throws Exception {
        int result;
        for (int i = 0; i < 8; i++) {
            result = client.writeBit(AreaType.S7AreaDB, db, start, i, true);
            checkResult(result);
            Thread.sleep(200);
        }
        for (int i = 0; i < 8; i++) {
            result = client.writeBit(AreaType.S7AreaDB, db, start, i, false);
            checkResult(result);
            Thread.sleep(200);
        }

        int count = 0;
        boolean inc = true;
        for (int i = 0; i < 85; i++) {
            if (inc) {
                if (count > 0) {
                    result = client.writeBit(AreaType.S7AreaDB, db, start, count - 1, false);
                    checkResult(result);
                }
                result = client.writeBit(AreaType.S7AreaDB, db, start, count, true);
                checkResult(result);
                count++;
            } else {
                if (count < 7) {
                    result = client.writeBit(AreaType.S7AreaDB, db, start, count + 1, false);
                    checkResult(result);
                }
                result = client.writeBit(AreaType.S7AreaDB, db, start, count, true);
                checkResult(result);
                count--;
            }
            if (inc && count > 7) {
                inc = false;
                count = 6;
            } else if (!inc && count == -1) {
                inc = true;
                count = 1;
            }
            Thread.sleep(100);
        }


    }

    public static void main(String[] args) {
        new WriteBitSample();
    }
}
