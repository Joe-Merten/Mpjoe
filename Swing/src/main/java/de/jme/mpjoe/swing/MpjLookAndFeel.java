package de.jme.mpjoe.swing;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import de.jme.toolbox.SystemInfo;

// Siehe auch:
// - http://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
// Beim MetalLookAndFeel gibt es sowas wie: MetalLookAndFeel.setCurrentTheme(javax.swing.plaf.metal.MetalTheme)
// Nimbus hat dies jedoch nicht, dort soll man die Farben per UIManager.put() setzen
// - http://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/color.html
// - http://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/_nimbusDefaults.html#primary
public class MpjLookAndFeel {

    public static void initialize() {
        try {
            // Look & Feel kann man auch von Kommandozeile mitgeben:
            //   java -Dswing.defaultlaf=com.sun.java.swing.plaf.gtk.GTKLookAndFeel MyApp"
            //   siehe auch http://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

            //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            if (!SystemInfo.isOsx()) {
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                //UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");       // -> damit funktionieren meine Listener im FilesystemPanel noch nicht richtig
                //UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");           // -> damit funktionieren meine Listener im FilesystemPanel nicht
                //UIManager.setLookAndFeel("com.apple.laf.AquaLookAndFeel");
                //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");

                setNimbusDarkTheme();
            }

            // printLookAndFeels();
            // printColors();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setNimbusDarkTheme() {
        // gemäss http://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/color.html sind die 3 wichtigsten Farben:
        //   UIManager.put("nimbusBase", new Color(...));
        //   UIManager.put("nimbusBlueGrey", new Color(...));
        //   UIManager.put("control", new Color(...));

        // Ohne meine Farbänderungen gibt es genau einen Eintrag für z.B. "text"
        //   text  (0,0,0)        javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
        // nach meinen UIManager.put() calls sind diverse Einträge doppelt:
        //   text  (0,0,0)        javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
        //   text  (230,230,230)  java.awt.Color[r=230,g=20,b=20]
        // aber wenn ich ColorUIResource anstelle von Color verwende sieht das auch nicht besser aus:
        //   text  (0,0,0)        javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
        //   text  (230,230,230)  javax.swing.plaf.ColorUIResource[r=230,g=20,b=20]
        // offenbar ist das normal...?
        // Mache ich aber 2x put (1x Color und 1x ColorUIResource), dann bleibt nur mein letzter Eintrag erhalten.

        // Nimbus Dark
      //UIManager.put("control"                  , new Color(128, 128, 128));
        UIManager.put("control"                  , new Color( 64,  64,  64));  // Unbenutzte Bereiche von Panels etc.
        UIManager.put("info"                     , new Color(128, 128, 128));
        UIManager.put("nimbusBase"               , new Color( 18,  30,  49));  // Menüzeile, Scrollbars, Splitter
        UIManager.put("nimbusAlertYellow"        , new Color(248, 187,   0));
        UIManager.put("nimbusDisabledText"       , new Color(128, 128, 128));
        UIManager.put("nimbusFocus"              , new Color(115, 164, 209));
        UIManager.put("nimbusGreen"              , new Color(176, 179,  50));
        UIManager.put("nimbusInfoBlue"           , new Color( 66, 139, 221));
      //UIManager.put("nimbusLightBackground"    , new Color( 18,  30,  49));  // Backgroud von JTable, JList, Textedit
        UIManager.put("nimbusLightBackground"    , new Color( 32,  32,  48));  // Backgroud von JTable, JList, Textedit
        UIManager.put("nimbusOrange"             , new Color(191,  98,   4));
        UIManager.put("nimbusRed"                , new Color(169,  46,  34));
        UIManager.put("nimbusSelectedText"       , new Color(255, 255, 255));
        UIManager.put("nimbusSelectionBackground", new Color(104,  93, 156));
        UIManager.put("text"                     , new Color(230, 230, 230));
    }

    // Debugausgabe: Alle Color Keys auflisten
    public static void printColors() {
        ArrayList<String> colorKeys = new ArrayList<String>();
        //Set<Entry<Object, Object>> entries = UIManager.getLookAndFeelDefaults().entrySet(); // der hier liefert die Defaults des Laf, also ohne meine Farbänderungen
        Set<Entry<Object, Object>> entries = UIManager.getDefaults().entrySet(); // der hier liefert die von mir geänderten Farben
        for (Entry<Object, Object> entry : entries)
            if (entry.getValue() instanceof Color) {
                String name = (String)entry.getKey();
                Color color = (Color)entry.getValue();
                colorKeys.add(name + "  (" + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + ")  " + color);
            }
        Collections.sort(colorKeys);
        for (String colorKey : colorKeys)
            System.out.println(colorKey);
    }

    // Debugausgabe: Alle Look & Feels auflisten
    // Verfügbare Look & Feels
    // - Kubuntu 14.04, Java 1.7
    //   - Metal            (javax.swing.plaf.metal.MetalLookAndFeel)                   - default
    //   - Nimbus           (javax.swing.plaf.nimbus.NimbusLookAndFeel)
    //   - CDE/Motif        (com.sun.java.swing.plaf.motif.MotifLookAndFeel)
    //   - GTK+             (com.sun.java.swing.plaf.gtk.GTKLookAndFeel)
    // - Osx 10.10, Java 1.8
    //   - Metal            (javax.swing.plaf.metal.MetalLookAndFeel)
    //   - Nimbus           (javax.swing.plaf.nimbus.NimbusLookAndFeel)
    //   - CDE/Motif        (com.sun.java.swing.plaf.motif.MotifLookAndFeel)
    //   - Mac OS X         (com.apple.laf.AquaLookAndFeel)                             - default
    // - Windows XP, Java 1.7
    //   - Metal            (javax.swing.plaf.metal.MetalLookAndFeel)
    //   - Nimbus           (javax.swing.plaf.nimbus.NimbusLookAndFeel)
    //   - CDE/Motif        (com.sun.java.swing.plaf.motif.MotifLookAndFeel)
    //   - Windows          (com.sun.java.swing.plaf.windows.WindowsLookAndFeel)        - default
    //   - Windows Classic  (com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel)
    //
    // Beim MetalLookAndFeel gibt es Themes:
    //   MetalLookAndFeel.setCurrentTheme(new OceanTheme());  // Dies ist der Default ab Java 1.5
    //   MetalLookAndFeel.setCurrentTheme(new DefaultMetalTheme()); // Der Name ist irreführend, das war der Default bis Java 1.4
    //   MetalLookAndFeel.setCurrentTheme(javax.swing.plaf.metal.MetalTheme)

    public static void printLookAndFeels() {
        String lfname = UIManager.getLookAndFeel().getName();
        String lfclassname = UIManager.getLookAndFeel().getClass().getName();
        System.out.println("L&F = " + lfname + " (" + lfclassname + ")");
        LookAndFeelInfo[] lfiList = UIManager.getInstalledLookAndFeels();
        for (LookAndFeelInfo lfi : lfiList)
            System.out.println("  " + lfi.getName() + " (" + lfi.getClassName() + ")");
    }

}
