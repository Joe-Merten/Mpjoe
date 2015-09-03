package de.jme.toolbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class to produce hex dump from binary data
 *
 * @author Joe Merten
 */
public class HexDump {

    static final Logger logger = LoggerFactory.getLogger(HexDump.class);

    // Wird momentan nicht verwendet, deshalb keine neue Implementation
    // Idee für ein Interface:
    // - Verschiedene Input-Alternativen, vorranging byte[]
    // - Output auf
    //   - PrintStream
    //   - Logger & LogLevel
    //   - String (Komplett oder Zeilenweise
    // - Optionen wie z.B.
    //   - Indent
    //   - Addresse
    //   - Anzahl Byte/Zeile (default z.B. 16)
    // Aber erst mal nur das Implementieren, was benötigt wird

    // TODO: ggf. auf String-Return umstellen, denn System.out wollen wir vermeiden, seit wir einen Logger haben (wird aber m.E. z.Zt. nicht verwendet)
    //@Deprecated public static void printHexTable(List<Integer> integerList) {
    //    int count = 0;
    //    List<Integer> tempList = new ArrayList<Integer>();
    //
    //    for (int i:integerList) {
    //        if (i < 16) {
    //            System.out.print(" 0x0" + Integer.toHexString(i));
    //        } else {
    //            System.out.print(" 0x" + Integer.toHexString(i));
    //        }
    //        tempList.add(i);
    //        count++;
    //        if (count == 8) {
    //            System.out.print("    ");
    //            for (int j : tempList) {
    //                if (j<=31) System.out.print(".");
    //                else System.out.print((char)j);
    //            }
    //            count = 0;
    //            tempList.clear();
    //            System.out.println();
    //        }
    //    }
    //
    //    for (int z=0; z<=8-tempList.size(); z++) {
    //        System.out.print("     ");
    //    }
    //
    //    for (int j:tempList) {
    //        if (j<=31 || j >= 126) System.out.print(".");
    //        else System.out.print((char)j);
    //
    //    }
    //    count = 0;
    //    tempList.clear();
    //    System.out.println();
    //}
}
