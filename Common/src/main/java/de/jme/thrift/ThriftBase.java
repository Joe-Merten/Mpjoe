package de.jme.thrift;

/**
 * Rudimentäre Thrift Implementation für Mpjoe
 *
 * @author Joe Merten
 */

public class ThriftBase {
    //private final static String TAG = ThriftBase.class.getSimpleName();

    // Die ersten 32 Bit einer Thrift Message sind die Version (oberste 16 Bit) und der MessageType (unterste 8 Bit)
    public static final int versionMask = 0xFFFF0000;  // siehe VERSION_MASK int TBinaryProtocol.h
    public static final int version1    = 0x80010000;  // siehe VERSION_1 int TBinaryProtocol.h

    // Analog zu Thrift TProtocol.h, enum TMessageType
    public enum MessageType {
        NONE,
        CALL,       // = 1
        REPLY,      // = 2
        EXCEPTION,  // = 3
        ONEWAY      // = 4
    };
    public static final int messageType_CALL      = 1;
    public static final int messageType_REPLY     = 2;
    public static final int messageType_EXCEPTION = 3;
    public static final int messageType_ONEWAY    = 4;

    // Analog zu Thrift TProtocol.h, enum TType
    public enum FieldType {
        STOP,     // =  0
        VOID,     // =  1
        BOOL,     // =  2
        BYTE,     // =  3
      //I08,      // =  3
        DOUBLE,   // =  4
        _DUMMY_5,
        I16,      // =  6
        _DUMMY_7,
        I32,      // =  8
        U64,      // =  9
        I64,      // = 10
        STRING,   // = 11
      //UTF7,     // = 11
        STRUCT,   // = 12
        MAP,      // = 13
        SET,      // = 14
        LIST,     // = 15
        UTF8,     // = 16
        UTF16     // = 17
    };
    public static final int fieldType_STOP       =  0;
    public static final int fieldType_VOID       =  1;
    public static final int fieldType_BOOL       =  2;
    public static final int fieldType_BYTE       =  3;
    public static final int fieldType_I08        =  3;
    public static final int fieldType_DOUBLE     =  4;
    public static final int fieldType_I16        =  6;
    public static final int fieldType_I32        =  8;
    public static final int fieldType_U64        =  9;
    public static final int fieldType_I64        = 10;
    public static final int fieldType_STRING     = 11;
    public static final int fieldType_UTF7       = 11;
    public static final int fieldType_STRUCT     = 12;
    public static final int fieldType_MAP        = 13;
    public static final int fieldType_SET        = 14;
    public static final int fieldType_LIST       = 15;
    public static final int fieldType_UTF8       = 16;
    public static final int fieldType_UTF16      = 17;
}
