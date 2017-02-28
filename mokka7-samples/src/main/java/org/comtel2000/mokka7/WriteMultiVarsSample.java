package org.comtel2000.mokka7;

import com.google.common.primitives.Ints;

public class WriteMultiVarsSample extends ClientRunner {

    public WriteMultiVarsSample() {
        super();
    }

    @Override
    public void call(S7Client client) throws Exception {
        int result;

        try (S7MultiVar mv = new S7MultiVar(client)) {
            mv.add(AreaType.S7AreaDB, 201, 0, 1, DataType.S7WLByte, new byte[]{0x09});
            mv.add(AreaType.S7AreaDB, 201, 1, 1, DataType.S7WLByte, new byte[]{0x08});
            mv.add(AreaType.S7AreaDB, 201, 2, 1, DataType.S7WLByte, new byte[]{0x07});
            mv.add(AreaType.S7AreaDB, 201, 3, 1, DataType.S7WLByte, new byte[]{0x06});

            mv.add(AreaType.S7AreaDB, 201, 4, 1, DataType.S7WLByte, new byte[]{0x09});
            mv.add(AreaType.S7AreaDB, 201, 5, 1, DataType.S7WLByte, new byte[]{0x08});
            mv.add(AreaType.S7AreaDB, 201, 6, 1, DataType.S7WLByte, new byte[]{0x07});
            mv.add(AreaType.S7AreaDB, 201, 7, 1, DataType.S7WLByte, new byte[]{0x06});

            result = mv.write();
            checkResult(result);
        }

        byte b = client.readByte(AreaType.S7AreaDB, 201, 0);
        bitSet(b);
        b = client.readByte(AreaType.S7AreaDB, 201, 1);
        bitSet(b);
        b = client.readByte(AreaType.S7AreaDB, 201, 2);
        bitSet(b);
        b = client.readByte(AreaType.S7AreaDB, 201, 3);
        bitSet(b);
        Long sum = client.readLong(AreaType.S7AreaDB, 201, 0);
        System.err.println(Ints.fromByteArray(new byte[] { 0x09, 0x08, 0x07, 0x06 }));
        System.err.println(sum);

//        try (S7MultiVar mv = new S7MultiVar(client)) {
//
//            mv.add(AreaType.S7AreaDB, 201, 4, 1, DataType.S7WLDWord, Ints.toByteArray(151521030));
//            mv.add(AreaType.S7AreaDB, 201, 8, 1, DataType.S7WLDWord, Ints.toByteArray(151521030));
//
//            result = mv.write();
//            checkResult(result);
//        }
//
//        Long l = client.readLong(AreaType.S7AreaDB, 201, 4);
//        System.err.println(l);
//        l = client.readLong(AreaType.S7AreaDB, 201, 8);
//        System.err.println(l);
    }

    public static void main(String[] args) {
        new WriteMultiVarsSample();
    }
}
