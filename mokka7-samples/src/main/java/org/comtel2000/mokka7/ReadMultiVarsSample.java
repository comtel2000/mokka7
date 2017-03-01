package org.comtel2000.mokka7;

import org.comtel2000.mokka7.AreaType;
import org.comtel2000.mokka7.DataType;
import org.comtel2000.mokka7.S7Client;
import org.comtel2000.mokka7.S7MultiVar;

public class ReadMultiVarsSample extends ClientRunner {

    final int db = 3;

    public ReadMultiVarsSample() {
        super();
    }

    @Override
    public void call(S7Client client) throws Exception {

        byte b = client.readByte(AreaType.S7AreaDB, db, 0);
        bitSet(b);
        b = client.readByte(AreaType.S7AreaDB, db, 1);
        bitSet(b);
        b = client.readByte(AreaType.S7AreaDB, db, 2);
        bitSet(b);
        b = client.readByte(AreaType.S7AreaDB, db, 3);
        bitSet(b);

        try (S7MultiVar mv = new S7MultiVar(client)) {
            mv.add(AreaType.S7AreaDB, db, 0, 1, DataType.S7WLByte);
            mv.add(AreaType.S7AreaDB, db, 1, 1, DataType.S7WLByte);
            mv.add(AreaType.S7AreaDB, db, 2, 1, DataType.S7WLByte);
            mv.add(AreaType.S7AreaDB, db, 3, 1, DataType.S7WLByte);

            mv.read();

            checkResult(mv.getResult(0).result);
            checkResult(mv.getResult(1).result);
            checkResult(mv.getResult(2).result);
            checkResult(mv.getResult(3).result);

            bitSet(mv.getResult(0).data[0]);
            bitSet(mv.getResult(1).data[0]);
            bitSet(mv.getResult(2).data[0]);
            bitSet(mv.getResult(3).data[0]);
        }

        try (S7MultiVar mv = new S7MultiVar(client)) {
            mv.add(AreaType.S7AreaDB, db, 0, 1, DataType.S7WLWord);
            mv.add(AreaType.S7AreaDB, db, 2, 1, DataType.S7WLWord);
            mv.read();

            checkResult(mv.getResult(0).result);
            checkResult(mv.getResult(1).result);

            S7.hexDump(mv.getResult(0).data);
            S7.hexDump(mv.getResult(1).data);
        }

        try (S7MultiVar mv = new S7MultiVar(client)) {
            mv.add(AreaType.S7AreaDB, db, 0, 2, DataType.S7WLWord);
            mv.read();

            checkResult(mv.getResult(0).result);
            S7.hexDump(mv.getResult(0).data);
        }

        try (S7MultiVar mv = new S7MultiVar(client)) {
            mv.add(AreaType.S7AreaDB, db, 0, 1, DataType.S7WLDWord);
            mv.add(AreaType.S7AreaDB, db, 4, 1, DataType.S7WLDWord);
            mv.read();

            checkResult(mv.getResult(0).result);
            checkResult(mv.getResult(1).result);
            S7.hexDump(mv.getResult(0).data);
            S7.hexDump(mv.getResult(1).data);
        }

    }

    public static void main(String[] args) {
        new ReadMultiVarsSample();
    }
}
