package org.comtel2000.mokka7;

import org.comtel2000.mokka7.AreaType;
import org.comtel2000.mokka7.DataType;
import org.comtel2000.mokka7.S7;
import org.comtel2000.mokka7.S7Client;

public class ReadByteSample extends ClientRunner {

    final int db = 201;

    public ReadByteSample() {
        super();
    }

    @Override
    public void call(S7Client client) throws Exception {
        int result;
        result = client.readArea(AreaType.S7AreaDB, db, 0, 1, DataType.S7WLByte, buffer);
        checkResult(result);
        bitSet(buffer[0]);
        S7.hexDump(buffer, 16);

        byte b = client.readByte(AreaType.S7AreaDB, db, 0);
        bitSet(b);
        S7.hexDump(buffer, 16);

        result = client.readArea(AreaType.S7AreaDB, db, 1, 1, DataType.S7WLByte, buffer);
        checkResult(result);
        bitSet(buffer[0]);
        S7.hexDump(buffer, 16);

        b = client.readByte(AreaType.S7AreaDB, db, 1);
        bitSet(b);
        S7.hexDump(buffer, 16);
    }

    public static void main(String[] args) {
        new ReadByteSample();
    }
}
