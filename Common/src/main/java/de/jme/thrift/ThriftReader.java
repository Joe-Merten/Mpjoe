package de.jme.thrift;

import java.io.IOException;

/**
 * Rudimentäre Thrift Implementation für Mpjoe
 * Parser zum Deserialisieren von Thrift Messages im Binary Protokoll
 *
 * @author Joe Merten
 */

public class ThriftReader {
    //private final static String TAG = ThriftReader.class.getSimpleName();

    private byte[] buffer = null; ///< Buffer mit den Thrift Binärdaten
    private int index = 0;        ///< Aktueller Lesezeiger
    private int indent = 0;       ///< Aktuelle Strukturtiefe

    private int messageVersion = 0;
    private ThriftBase.MessageType messageType = null;
    private String messageName = null;
    private int sequenceNumber = 0;

    static public class Field {
        public int         indent = 0;              ///< Strukturtiefe des Feldes
        public ThriftBase.FieldType type = null;
        public int         id = 0;
        public boolean     valueBool = false;
        public int         valueInt = 0;
        public String valueString = null;
        public Field() {}
    }
    private Field parsedField = null;

    public ThriftReader(final byte[] buffer) throws IOException {
        this.buffer = buffer;
        checkRemain(14); // Mindestlänge einer Thrift Message = 4 (Version) + 5 (NameLen+Name) + 4 (SeqNr) + 1 (STOP)

        int v = readInt32();
        messageVersion = v & ThriftBase.versionMask;
        if (messageVersion != ThriftBase.version1)
            throw new IOException("Unsupported version \"" + String.format("%08X", messageVersion) + "\"");
        int t = v & ~ThriftBase.versionMask;
        if (t < 1 || t > 4)
            throw new IOException("Illegal message type \"" + t + "\"");
        messageType = ThriftBase.MessageType.values()[t];

        int nameLen = readInt32();
        if (nameLen <= 0 || nameLen > 1024)
            throw new IOException("Invalid message name with length of " + nameLen);
        checkRemain(nameLen);
        messageName = new String(buffer, index, nameLen, "UTF-8");
        index += nameLen;

        sequenceNumber = readInt32();
    }

    public int getMessageVersion()                   { return messageVersion; }
    public ThriftBase.MessageType getMessageType()   { return messageType;    }
    public String getMessageName()                   { return messageName;    }
    public int getSequenceNumber()                   { return sequenceNumber; }

    boolean parseNextField() throws IOException {
        parsedField = new Field();

        for (;;) {
            parsedField.indent = indent;
            int t = readByte();
            parsedField.type = ThriftBase.FieldType.values()[t];
            if (parsedField.type != ThriftBase.FieldType.STOP) break;

            if (indent <= 0) {
                // Letztes STOP
                int remain = buffer.length - index;
                if (remain != 0)
                    throw new IOException("At index = " + (index-1) + ": Too much data, found message termination but have " + remain + " byte left");
                return false;
            } else {
                // STOP markiert hier "End of Struct"
                indent--;
                // mit "return true" könnten wir dies dem Aufrufer mitteilen - halte ich aber vorerst nicht für notwendig
                //return true;
            }
        }

        // Alle anderen Fieldtypes haben nachfolgend eine 16 Bit FieldId
        checkRemain(2);
        int id = readByte();
        id <<= 8;
        id |= readByte();
        parsedField.id = id;

        // Das Nachfolgende ist nun stark abhängig vom FieldType.
        // Wir unterstützen vorerst nur STRUCT, BOOL, INT32, STRING
        switch (parsedField.type) {
            case STRUCT: {
                indent++;
                break;
            }
            case BOOL: {
                int v = readByte();
                if (v != 0 && v != 1)
                    throw new IOException("At index = " + (index-1) + ": Invalid value " + v + " for bool field");
                parsedField.valueBool = v!=0;
                break;
            }

            case I32: {
                parsedField.valueInt = readInt32();
                break;
            }

            case STRING:
            case UTF8: {
                int len = readInt32();
                if (len < 0 || len > 1024*1024) // Mehr 1MB erscheint mir verdächtig!
                    throw new IOException("At index = " + (index-4) + ": Courios length of string field value " + len);
                checkRemain(len);
                parsedField.valueString = new String(buffer, index, len, "UTF-8");  // TODO: Encoding
                index += len;
                break;
            }

            default: {
                throw new IOException("At index = " + (index-3) + ": Invalid or unsupported field type " + parsedField.type);
            }
        } // switch

        return true;
    }

    Field getParsedField() { return parsedField; }


    private void checkRemain(int expectedData) throws IOException {
        int remain = buffer.length - index;
        if (expectedData < 0 || expectedData > remain) // "expectedData < 0" ist hier nur eine zus. Sicherheitsabfrage
            throw new IOException("At index = " + index + ": Need at least " + expectedData + " byte but have just " + remain + " byte left.");
    }

    private int readByte() throws IOException {
        checkRemain(1);
        int ret = buffer[index];
        if (ret < 0) ret += 256;
        index++;
        return ret;
    }

    private int readInt32() throws IOException {
        checkRemain(4);
        int ret = 0;
        ret = readByte(); ret <<= 8;
        ret |= readByte(); ret <<= 8;
        ret |= readByte(); ret <<= 8;
        ret |= readByte();
        return ret;
    }

    @Override public String toString() {
        return toString(0);
    }

    private static String spaces(int n) {
        return n <= 0 ? "" : String.format("%" + n + "s", "");
    }

    public String toString(int indentSpace) {
        StringBuilder b = new StringBuilder(1024);
        final String i1 = spaces(indentSpace);
        try {
            b.append(       i1 + String.format("Version = %08Xh"     , messageVersion));
            b.append("\n" + i1 + String.format("Type    = %d = %s"   , messageType.ordinal(), messageType.toString()));
            b.append("\n" + i1 + String.format("Name    = %s"        , messageName));
            b.append("\n" + i1 + String.format("SeqNr   = %d = %08Xh", sequenceNumber, sequenceNumber));

            while (parseNextField()) {
                final Field field = getParsedField();
                final String i2 = "\n" + spaces(indentSpace + field.indent * 4);
                b.append(i2 + String.format("+ Type  = %d = %02Xh = %s", field.type.ordinal(), field.type.ordinal(), field.type.toString()));  // das + Zeichen soll in der Auflistung den Anfang eines neuen Feldes markieren
                if (field.type != ThriftBase.FieldType.STOP)   b.append(i2 + String.format("  Id    = %d = %04Xh", field.id, field.id));
                if (field.type == ThriftBase.FieldType.BOOL)   b.append(i2 + String.format("  Bool  = %b",         field.valueBool));
                if (field.type == ThriftBase.FieldType.I32 )   b.append(i2 + String.format("  Int   = %d = %04Xh", field.valueInt, field.valueInt));
                if (field.type == ThriftBase.FieldType.STRING) b.append(i2 + String.format("  String= \"%s\"",     field.valueString));
            }
        } catch (Throwable t) {
            // Die Exception Message hängen wir einfach mal an das bis dahin gebaute hinten dran
            b.append("\n" + i1 + t.toString());
        }

        return b.toString();
    }

    static public String toString(final byte[] buffer) {
        return toString(buffer, 0);
    }

    static public String toString(final byte[] buffer, int indentSpace) {
        String ret;
        try {
            ThriftReader reader = new ThriftReader(buffer);
            ret = reader.toString(indentSpace);
        } catch (Throwable t) {
            ret = t.toString();
        }
        return ret;
    }
}
