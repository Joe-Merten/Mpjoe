package de.jme.thrift;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Rudimentäre Thrift Implementation für Mpjoe
 * Composer zum Serialisieren von Thrift Messages im Binary Protokoll
 *
 * @author Joe Merten
 */

public class ThriftWriter {
    //private final static String TAG = ThriftWriter.class.getSimpleName();
    private final ByteArrayOutputStream buffer;
    private int indent = 0;

    private final int stateVirgin = 0;
    private final int stateOpen   = 1;
    private final int stateClose  = 2;
    private final int stateError  = 3;
    private int state = stateVirgin;

    public ThriftWriter() {
        this(1024);
    }

    public ThriftWriter(int initialSize) {
        buffer = new ByteArrayOutputStream(initialSize);
    }

    public byte[] toByteArray() throws IOException {
        close();

        final byte[] arr = buffer.toByteArray();
        StringBuilder b = new StringBuilder(4 * arr.length);
        for (int i = 0; i < arr.length; i++) {
            int c = arr[i];
            if (c < 0) c += 256;
            if (c >= 20 && c <= 126)
                b.append((char)c);
            else
                b.append(String.format("\\x%02X", c));
        }
        //Log.v(TAG, "Buffer = \"" + b + "\"");
        return buffer.toByteArray();
    }

    public void open(ThriftBase.MessageType messageType, String messageName, int sequenceNumber) throws IOException {
        checkForState(stateVirgin);
        int v = ThriftBase.version1 | messageType.ordinal();
        state = stateOpen;
        try {
            writeInt32(v);
            writeString(messageName);
            writeInt32(sequenceNumber);
        } catch (Throwable t) {
            state = stateError;
            throw t;
        }
    }

    public void close() throws IOException {
        if (state == stateClose) return; // Wenn schon closed, dann ignorieren wir den call
        checkForState(stateOpen);
        if (indent != 0)
            throw new IOException("Missing closeStruct(), indent = " + indent);
        writeByte(ThriftBase.FieldType.STOP.ordinal());
        state = stateClose;
    }

    public void openStruct(int id) throws IOException {
        writeTypeAndId(ThriftBase.FieldType.STRUCT, id);
        indent++;
    }

    public void closeStruct() throws IOException {
        if (indent <= 0)
            throw new IOException("Unexpected closeStruct()");
        writeByte(ThriftBase.FieldType.STOP.ordinal());
        indent--;
    }

    public void addBool(int id, boolean value) throws IOException {
        writeTypeAndId(ThriftBase.FieldType.BOOL, id);
        if (value)
            writeByte(1);
        else
            writeByte(0);
    }

    public void addInt32(int id, int value) throws IOException {
        writeTypeAndId(ThriftBase.FieldType.I32, id);
        writeInt32(value);
    }

    // Bzgl. String / Utf-8:
    // - In den Thrift C++ Sroucen, TProtocol.h: "enum TType { ... T_STRING = 11, T_UTF7 = 11, T_UTF8 = 16, T_UTF16 = 17 }"
    //   Dies ist ein Hinweis darauf, dass T_STRING=11 eher UTF-7 ist und man für Utf-8 hingegen T_UTF8=16 verwenden muss.
    // - In den Thrift Cocoa Sourcen, TProtocol.h: "TType_STRING = 11", aber kein "TType_UTF...irgendwas"
    //   Aus der Cocoa Implementation ist ersichtlich, dass dort mit Datentyp TType_STRING=11 Utf-8 kodierte Strings übertragen werden.
    // - Und hier steht: "Base Types ... string: A text string encoded using UTF-8 encoding"
    //   -> http://thrift-tutorial.readthedocs.org/en/latest/thrift-types.html
    // - Betrachtet am 2.9.2014 und zur Feststellung gekommen:
    //   - T_UTF8=16 scheint nicht sinnvoll, da dies bei Cocoa nicht implementiert ist
    //   - statt dessen scheint es üblich zu ein, T_STRING=11 zu verwenden und dort utf-8 kodierte Strings hinein zu tun
    //   - folglich machen wir das auch so
    public void addString(int id, String value) throws IOException {
        writeTypeAndId(ThriftBase.FieldType.STRING, id);
        writeString(value);
    }

    // Nach Untersuchung des Thrift Sourcecode stellte sich heraus, dass Binary exakt genauso übertragen wird wie String.
    // - es gibt also keinen eigenen FieldType für Binary (also nicht z.B. Void)
    // - Im Thrift Binary Protocol ist somit leider nicht mehr unterscheidbar, ob es sich um einen String oder um Binärdaten handelt.
    // Thrift verwendet hier ByteBuffer anstelle von byte[]
    // TODO: Überlegen, ob wird das auch so machen
    // - https://issues.apache.org/jira/browse/THRIFT-830
    // - http://docs.oracle.com/javase/7/docs/api/java/nio/ByteBuffer.html
    // - http://developer.android.com/reference/java/nio/ByteBuffer.html
    public void addBinary(int id, final byte[] value) throws IOException {
        writeTypeAndId(ThriftBase.FieldType.STRING, id);
        writeBinary(value);
    }

    private void checkForState(int needState) throws IOException {
        if (state != needState)
            throw new IOException("Invalid call in state " + state + ", this call is only allowed in state " + needState);
    }

    // n muss im Bereich 0-255 liegen
    private void writeByte(int n) throws IOException {
        checkForState(stateOpen);
        if (n < 0 || n > 255)
            throw new IOException("Value out of Range (" + n + ")");
        buffer.write(n);
    }

    private void writeInt32(int n) throws IOException {
        checkForState(stateOpen);
        buffer.write((byte)(n >> 24));
        buffer.write((byte)(n >> 16));
        buffer.write((byte)(n >>  8));
        buffer.write((byte)(n));
    }

    private void writeString(String s) throws IOException {
        checkForState(stateOpen);
        int len = s.length();
        writeInt32(len);
        buffer.write(s.getBytes("UTF-8"));
    }

    private void writeBinary(final byte[] data) throws IOException {
        checkForState(stateOpen);
        int len = data.length;
        writeInt32(len);
        buffer.write(data);
    }

    private void writeTypeAndId(ThriftBase.FieldType type, int id) throws IOException {
        checkForState(stateOpen);
        if (id < 0 || id > 0xFFFF)
            throw new IOException("Field id out of Range (" + id + ")");
        buffer.write((byte)type.ordinal());
        buffer.write((byte)(id >> 8));
        buffer.write((byte)(id));
    }

}
