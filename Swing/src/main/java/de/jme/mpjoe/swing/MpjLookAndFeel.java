package de.jme.mpjoe.swing;

import java.awt.Color;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import de.jme.toolbox.SystemInfo;

// Siehe auch:
// - http://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
// Beim MetalLookAndFeel gibt es sowas wie: MetalLookAndFeel.setCurrentTheme(javax.swing.plaf.metal.MetalTheme)
// Nimbus hat dies jedoch nicht, dort soll man die Farben per UIManager.put() setzen
// - http://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/color.html
// - http://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/_nimbusDefaults.html#primary

// Weiteres Interessantes zu Swing Farben
// - http://nadeausoftware.com/articles/2010/12/java_tip_how_use_systemcolors_access_os_user_interface_theme_colors#SystemColorsvsUIDefaults
public class MpjLookAndFeel {
    static boolean darkTheme = false;
    static boolean initialized = false;

    static String[] yellow1;
    static String[] yellow2;

    // setYello() sind Hilfsfunktionen für's Feintuning der Farbschemata
    public static void setYellow1(String[] y) {
        yellow1 = y;
    }

    public static void setYellow2(String[] y) {
        yellow2 = y;
    }

    public static void setYellow(String[] yellow) {
        if (yellow == null || yellow.length == 0) return;

        final ArrayList<String> colorKeys = new ArrayList<String>();
        final Set<Entry<Object, Object>> entries = UIManager.getDefaults().entrySet(); // der hier liefert die von mir geänderten Farben
        for (final Entry<Object, Object> entry : entries)
            if (entry.getValue() instanceof Color)
                colorKeys.add((String)entry.getKey());

        final Color col = new Color(255,255,0);
        for (String y : yellow) {
            boolean isInt = false;
            int index = 0;
            try {
                index = Integer.parseInt(y);
                isInt = true;
            } catch(NumberFormatException e) {}
            if (y.contains("-")) {
                // Vermutlich sowas wie "0-100"
                String[] vals = y.split("-");
                try {
                    int a = Integer.parseInt(vals[0]);
                    int b = Integer.parseInt(vals[1]);
                    for (int i = a; i <= b; i++) {
                        if (i >=0 && i < colorKeys.size()) {
                            System.out.println("Setting color " + i + " \"" + colorKeys.get(i) + "\"");
                            UIManager.put(colorKeys.get(i), col);
                        }
                    }
                } catch(NumberFormatException e) {
                    e.printStackTrace();
                }
            } else if (isInt) {
                System.out.println("Setting color " + index + " \"" + colorKeys.get(index) + "\"");
                UIManager.put(colorKeys.get(index), col);
            } else {
                index = -1;
                int i = 0;
                for (String k : colorKeys) {
                    if (k.equals(y)) index = i;
                    i++;
                }
                System.out.println("Setting color " + index + " \"" + y + "\"");
                UIManager.put(index, col);
            }
        }
    }

    public static void initialize() {
        try {

            if (SystemInfo.isOsx()) {
                // erforderlich für Osx
                // -> siehe auch https://developer.apple.com/library/mac/documentation/Java/Reference/Java_PropertiesRef/Articles/JavaSystemProperties.html
                System.setProperty("apple.laf.useScreenMenuBar", "true");
                // Bei "brushMetalLook" sehe ich keinen Unterschied
                System.setProperty("apple.awt.brushMetalLook", "true");
                // Developer-setting zum Testen von Fullscreen, scheint aber noch mehr notwendig zu sein
                //System.setProperty("apple.awt.fakefullscreen", "true");
            }

            // Look & Feel kann man auch von Kommandozeile mitgeben:
            //   java -Dswing.defaultlaf=com.sun.java.swing.plaf.gtk.GTKLookAndFeel MyApp"
            //   siehe auch http://docs.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            //UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());

            // Bei Osx lassen wir das Loog & Feel auf nativ wg. dem Menü an der Bildschirmkante
            // -> System.setProperty("apple.laf.useScreenMenuBar", "true");  funktioniert leider nicht mit Nimbus
            // Für die anderen (Linux und Windows) setzen wir "Nimbus Dark"
            // - Unter Windows XP habe ich bei Nimbus kein Antialiasing, hat aber der Windows Explorer und Mpjoe1 auch nicht - allerdings fällt das bei denen nicht so auf.
            //   - Windows Explorer macht schwarz auf weiss und Mpjoe1 hat Font Strichstärke 2 Pixel
            //   - Metal hätte zumindest im FileChooser Antialiasing, evtl. weil dort ein etwas größerer / fetterer Font verwendet wird
            if (!SystemInfo.isOsx()) {
                //UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                //UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");       // -> damit funktionieren meine Listener im FilesystemPanel noch nicht richtig
                //UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");           // -> damit funktionieren meine Listener im FilesystemPanel nicht
                //UIManager.setLookAndFeel("com.apple.laf.AquaLookAndFeel");
                //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                //UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
                darkTheme = true;
            }

            // Splitpanes bitte ohne Rand -> http://stackoverflow.com/a/12800669/2880699
            UIManager.getDefaults().put("SplitPane.border", BorderFactory.createEmptyBorder());
            UIManager.getDefaults().put("ScrollPane.border", BorderFactory.createEmptyBorder());

        } catch (Exception e) {
            e.printStackTrace();
        }
        initialized = true;

        setYellow(yellow1);
        if (darkTheme) {
            darkTheme = false;
            setDarkTheme(true);
        }
        setYellow(yellow2);

        //printLookAndFeels();
        //printColors();
    }


    public static LookAndFeelInfo[] getLookAndFeels() {
        return UIManager.getInstalledLookAndFeels();
    }

    public static int getLookAndFeelCount() {
        return UIManager.getInstalledLookAndFeels().length;
    }

    public static int getCurrentLookAndFeelIndex() {
        String lfclassname = UIManager.getLookAndFeel().getClass().getName();
        final LookAndFeelInfo[] lfiList = UIManager.getInstalledLookAndFeels();
        int index = 0;
        for (LookAndFeelInfo lfi : lfiList) {
            if (lfi.getClassName() == lfclassname) return index;
            index++;
        }
        return -1;
    }

    // Look & Feel Umschaltung scheint irgendwie noch buggee zu sein
    public static void setCurrentLookAndFeelIndex(int n) {
        final LookAndFeelInfo[] lfiList = UIManager.getInstalledLookAndFeels();
        if (n < 0) n = 0;
        if (n > lfiList.length-1) n = lfiList.length-1;
        try {
            System.out.println("Switching to LookAndFeel " + n + " of " + lfiList.length + " \"" + lfiList[n].getName() + "\"" + " \"" + lfiList[n].getClassName() + "\"");
            setCurrentLookAndFeelClassName(lfiList[n].getClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setCurrentLookAndFeelClassName(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(className);
        updateUI();
    }

    public static boolean isDarkTheme() {
        return darkTheme;
    }

    // Umschalten des Farbschema zur Laufzeit funktioniert noch nicht richtig
    public static void setDarkTheme(boolean dark) {
        if (dark == darkTheme) return;

        if (initialized) {
            if (!dark) {
                UIManager.getDefaults().putAll(UIManager.getLookAndFeelDefaults());
            } else {
                if (!SystemInfo.isOsx())
                    setNimbusDarkTheme();
                else
                    setAquaDarkTheme();
            }
            updateUI();
        }

        darkTheme = dark;
    }

    private static void updateUI() {
        // updateComponentTreeUI() über alle Frames oder Windows?
        // Hab' bisher noch keinen Unterschied gesehen
        // Bei Nimbus werden leider einige Elemente nicht korrekt dargestellt (bei Umschaltung DarkTheme)
        // - Header von JTable
        // - Devider von JSplitPane
        // - Scrollbar Buttons von JScrollPane
        // revalidate() hilft hier leider auch nicht (getestet am JSplitPane)

        //final Frame[] frames = Frame.getFrames();
        //for (Frame frame : frames) {
        //    SwingUtilities.updateComponentTreeUI(frame);
        //    // Danach noch pack() aufrufen? Nein, das verkleinert mein Applikationsfenster!
        //    //window.pack();
        //}

        final Window[] windows = Window.getWindows();
        for (Window window : windows) {
            SwingUtilities.updateComponentTreeUI(window);
            // Danach noch pack() aufrufen? Nein, das verkleinert mein Applikationsfenster!
            //window.pack();
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
      //UIManager.put("nimbusBase"               , new Color( 18,  30,  49));  // u.a. für Menüzeile, Scrollbars, Splitter, wird aber aufgehellt
        UIManager.put("nimbusBase"               , new Color( 16,  16,  32));  // u.a. für Menüzeile, Scrollbars, Splitter, wird aber aufgehellt (16,32,32 wird z.B. beim Splitter zu 115,115,115)
        UIManager.put("nimbusAlertYellow"        , new Color(248, 187,   0));
        UIManager.put("nimbusDisabledText"       , new Color(128, 128, 128));
        UIManager.put("nimbusFocus"              , new Color(115, 164, 209));
        UIManager.put("nimbusGreen"              , new Color(176, 179,  50));
        UIManager.put("nimbusInfoBlue"           , new Color( 66, 139, 221));
      //UIManager.put("nimbusLightBackground"    , new Color( 18,  30,  49));  // Background von JTable, JList, Textedit
        UIManager.put("nimbusLightBackground"    , new Color( 32,  32,  48));  // Background von JTable, JList, Textedit
        UIManager.put("nimbusOrange"             , new Color(191,  98,   4));
        UIManager.put("nimbusRed"                , new Color(169,  46,  34));
        UIManager.put("nimbusSelectedText"       , new Color(255, 255, 255));
        UIManager.put("nimbusSelectionBackground", new Color(104,  93, 156));
        UIManager.put("text"                     , new Color(230, 230, 230));

        // Wenns noch etwas dunkler sein soll
        UIManager.put("control"                  , new Color(  0,   0,   0));  // Unbenutzte Bereiche von Panels etc.
        UIManager.put("nimbusLightBackground"    , new Color(  0,   0,   0));  // Background von JTable, JList, Textedit
    }



    // UI Defaults für Osx Aqua
    // - http://www.duncanjauncey.com/java/ui/uimanager/UIDefaults_Java1.6.0_17_Mac_OS_X_10.6.2_Mac_OS_X.html
    // - http://cr.openjdk.java.net/~dcherepanov/7154516/webrev.0/src/macosx/classes/com/apple/laf/AquaLookAndFeel.java-.html

    public static void setAquaDarkTheme() {
        /*{
            // Beim Mac bekomme ich hiermit offenbar alles schwarz, ausser
            // - Scrollbars
            // - Fenstertitel
            // - Comboboxen (bzw. Dropdown Listboxes)
            // - Fokusrechteck, z.B. der "Cursor" im JTable

            // Bei Verwendung von "new ColorUIResource" bleiben die Spaltenüberschriften von JTable unverändert, mit "new Color" bekomme ich die aber auch geändert
            // Dabei spielt es auch keine Rolle, ob ich "new Color" oder "new ColorUIResource" verwende
            final ArrayList<String> colorKeys = new ArrayList<String>();
            final Set<Entry<Object, Object>> entries = UIManager.getDefaults().entrySet(); // der hier liefert die von mir geänderten Farben
            for (final Entry<Object, Object> entry : entries)
                if (entry.getValue() instanceof Color)
                    colorKeys.add((String)entry.getKey());
            int i = 0;
            final int j = 0;
            final int k = 277;
            for (String colorKey : colorKeys) {
                if (i >= j && i <= k)
                    UIManager.put(colorKey, new Color(255, 255, 0));
                i++;
            }
            System.out.println("Found " + colorKeys.size() + " color keys, changed [" + j + "..." + k + "] ");
        }*/


        // Hier erst mal ein paar Farben für das Aqua Dark Theme zusammen gebastelt
        Color backgroundColor     = new Color(0,0,0);
        Color foregroundColor     = new Color(128,128,128);
        //Color controlBackground   = new Color(0,0,0);
        Color controlText         = new Color(128,128,128);
        Color controlTextDisabled = new Color(128,128,128);
        Color controlFace         = new Color(64,64,64);
        Color unknown = new Color(255,0,255);


        UIManager.put("Panel.background"                          , backgroundColor);       //          (238,238,238)

        UIManager.put("Button.background"                         , unknown);               //          (238,238,238)    // com.apple.laf.AquaImageFactory$SystemColorProxy[r=238,g=238,b=238]
        UIManager.put("Button.disabledText"                       , controlTextDisabled);
        UIManager.put("Button.light"                              , unknown);               //          (  9, 80,208)    // javax.swing.plaf.ColorUIResource[r=9,g=80,b=208]
        UIManager.put("Button.select"                             , unknown);               //          (255,102,102)    // javax.swing.plaf.ColorUIResource[r=255,g=102,b=102]
        UIManager.put("Button.shadow"                             , unknown);               //          (142,142,142)    // javax.swing.plaf.ColorUIResource[r=142,g=142,b=142]

        UIManager.put("ToggleButton.background"                   , unknown);               //          (238,238,238)    // com.apple.laf.AquaImageFactory$SystemColorProxy[r=238,g=238,b=238]
        UIManager.put("ToggleButton.disabledText"                 , controlTextDisabled);   //          (128,128,128)    // javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
        UIManager.put("ToggleButton.light"                        , unknown);               //          (  9, 80,208)    // javax.swing.plaf.ColorUIResource[r=9,g=80,b=208]
        UIManager.put("ToggleButton.shadow"                       , unknown);               //          (142,142,142)    // javax.swing.plaf.ColorUIResource[r=142,g=142,b=142]

        UIManager.put("Label.foreground"                          , controlText);           //
        UIManager.put("Label.background"                          , controlFace);           //  Wird für die "Umrandung" meiner Statusbar verwendet
        UIManager.put("Button.foreground"                         , new Color(0,0,0));      //
        UIManager.put("ComboBox.foreground"                       , controlText);           //  Textfarbe
        UIManager.put("ComboBox.background"                       , new Color(0,0,0));      //
        UIManager.put("ComboBox.selectionForeground"              , new Color(0,0,0));      //  Textfarbe des fokussierten Eintrags

        UIManager.put("SplitPane.background"                      , controlFace);         //          (238,238,238)    // Das Splitpane an sich
        UIManager.put("SplitPane.shadow"                          , backgroundColor);     //          (142,142,142)    // Splitpane Border, aber den hab' ich eh abgeschaltet
        UIManager.put("SplitPaneDivider.draggingColor"            , unknown);             //          ( 64, 64, 64)    // javax.swing.plaf.ColorUIResource[r=64,g=64,b=64]

        UIManager.put("TabbedPane.background"                     , unknown);             //          (238,238,238)    // com.apple.laf.AquaImageFactory$SystemColorProxy[r=238,g=238,b=238]
        UIManager.put("TabbedPane.light"                          , unknown);             //          (  9, 80,208)    // javax.swing.plaf.ColorUIResource[r=9,g=80,b=208]
        UIManager.put("TabbedPane.selectedTabTitlePressedColor"   , unknown);             //          (240,240,240)    // javax.swing.plaf.ColorUIResource[r=240,g=240,b=240]
        UIManager.put("TabbedPane.shadow"                         , unknown);             //          (142,142,142)    // javax.swing.plaf.ColorUIResource[r=142,g=142,b=142]
        UIManager.put("TextArea.inactiveForeground"               , unknown);             //          (128,128,128)    // javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
        UIManager.put("TextArea.selectionBackground"              , unknown);             //          (164,205,255)    // com.apple.laf.AquaImageFactory$SystemColorProxy[r=164,g=205,b=255]
        UIManager.put("TextComponent.selectionBackgroundInactive" , unknown);             //          (212,212,212)    // javax.swing.plaf.ColorUIResource[r=212,g=212,b=212]
        UIManager.put("TextField.inactiveForeground"              , unknown);             //          (128,128,128)    // javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
        UIManager.put("TextField.light"                           , unknown);             //          (  9, 80,208)    // javax.swing.plaf.ColorUIResource[r=9,g=80,b=208]
        UIManager.put("TextField.selectionBackground"             , unknown);             //          (164,205,255)    // com.apple.laf.AquaImageFactory$SystemColorProxy[r=164,g=205,b=255]
        UIManager.put("TextField.shadow"                          , unknown);             //          (142,142,142)    // javax.swing.plaf.ColorUIResource[r=142,g=142,b=142]
        UIManager.put("TextPane.inactiveForeground"               , unknown);             //          (128,128,128)    // javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
        UIManager.put("TextPane.selectionBackground"              , unknown);             //          (164,205,255)    // com.apple.laf.AquaImageFactory$SystemColorProxy[r=164,g=205,b=255]


        // Schwarz für Toolbar Background wäre ungünstig, weil die ButtonTexte auch schwarz sind
        UIManager.put("ToolBar.background"                        , controlFace);            // Farbe der Toolbar an sich
        UIManager.put("ToolBar.borderHandleColor"                 , unknown);                //          (140,140,140)    // javax.swing.plaf.ColorUIResource[r=140,g=140,b=140]
        UIManager.put("ToolBar.darkShadow"                        , unknown);                //          (  0,  0,  0)    // javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
        UIManager.put("ToolBar.dockingBackground"                 , new Color(64,64,64));    // Beim Verschieben der Toolbar, wenn im Dock-Bereich
        UIManager.put("ToolBar.dockingForeground"                 , new Color(128,128,128)); // Beim Verschieben der Toolbar, wenn im Dock-Bereich
        UIManager.put("ToolBar.floatingBackground"                , new Color(0,0,0));       // Beim Verschieben der Toolbar, wenn im Float-Bereich
        UIManager.put("ToolBar.floatingForeground"                , new Color(64,64,64));    // Beim Verschieben der Toolbar, wenn im Float-Bereich
        UIManager.put("ToolBar.foreground"                        , new Color(80,80,80));    // Separatoren
        UIManager.put("ToolBar.highlight"                         , unknown);                //          (255,255,255)    // javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
        UIManager.put("ToolBar.light"                             , unknown);                //          (  9, 80,208)    // javax.swing.plaf.ColorUIResource[r=9,g=80,b=208]
        UIManager.put("ToolBar.shadow"                            , unknown);                //          (142,142,142)    // javax.swing.plaf.ColorUIResource[r=142,g=142,b=142]

        UIManager.put("control"           , backgroundColor);     // Fensterhintergrund, wird beim App Start kurz angezeigt bevor das Fenster dann gefüllt wird
        UIManager.put("controlDkShadow"   , unknown);             // (  0,  0,  0)    // javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
        UIManager.put("controlHighlight"  , unknown);             // (  9, 80,208)    // javax.swing.plaf.ColorUIResource[r=9,g=80,b=208]
        UIManager.put("controlLtHighlight", unknown);             // (255,255,255)    // javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
        UIManager.put("controlShadow"     , unknown);             // (142,142,142)    // javax.swing.plaf.ColorUIResource[r=142,g=142,b=142]
        UIManager.put("controlText"       , unknown);             // (  0,  0,  0)    // javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]

        UIManager.put("menu"              , unknown);             //  (255,255,255)    // javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
        UIManager.put("menuText"          , unknown);             //  (  0,  0,  0)    // javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
        UIManager.put("scrollbar"         , unknown);             //  (154,154,154)    // javax.swing.plaf.ColorUIResource[r=154,g=154,b=154]
        UIManager.put("text"              , unknown);             //  (255,255,255)    // javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
        UIManager.put("textHighlight"     , unknown);             //  (164,205,255)    // javax.swing.plaf.ColorUIResource[r=164,g=205,b=255]
        UIManager.put("textHighlightText" , unknown);             //  (  0,  0,  0)    // javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
        UIManager.put("textInactiveText"  , unknown);             //  (108,108,108)    // javax.swing.plaf.ColorUIResource[r=108,g=108,b=108]
        UIManager.put("textText"          , unknown);             //  (  0,  0,  0)    // javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
        UIManager.put("window"            , unknown);             //  (238,238,238)    // javax.swing.plaf.ColorUIResource[r=238,g=238,b=238]
        UIManager.put("windowBorder"      , unknown);             //  (154,154,154)    // javax.swing.plaf.ColorUIResource[r=154,g=154,b=154]
        UIManager.put("windowText"        , unknown);             //  (  0,  0,  0)    // javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]

        // Farben (RGB Werte) ein bisschen aus meinem Nimbus Dark abgeguckt
        UIManager.put("Table.background"                         , backgroundColor);
        UIManager.put("Table.dropLineColor"                      , new Color(115,164,209));
        UIManager.put("Table.dropLineShortColor"                 , new Color(191,98,4));
        UIManager.put("Table.focusCellBackground"                , new Color(104,93,156));
        UIManager.put("Table.focusCellForeground"                , backgroundColor);
        UIManager.put("Table.foreground"                         , foregroundColor);
        UIManager.put("Table.gridColor"                          , new Color(32,32,32));
        UIManager.put("Table.selectionBackground"                , new Color(104,93,156));
        UIManager.put("Table.selectionForeground"                , backgroundColor);
        UIManager.put("Table.selectionInactiveBackground"        , new Color(104,93,156));
        UIManager.put("Table.selectionInactiveForeground"        , backgroundColor);
        UIManager.put("Table.sortIconColor"                      , unknown);
        UIManager.put("TableHeader.background"                   , new Color(80,80,120));
        UIManager.put("TableHeader.foreground"                   , new Color(0,0,0));
        UIManager.put("TableHeader.focusCellBackground"          , unknown);

        UIManager.put("activeCaptionText", new Color(255,0,255));
        UIManager.put("controlText", new Color(255,255,0));

        UIManager.put("Viewport.background", backgroundColor);    // Unbenutzter Bereich von Scrollpanes (z.B. in Verbindung mit JTable oder im JFileChooser)
        UIManager.put("ScrollPane.background", backgroundColor);  // ein kleiner Bereich der frei bleibt, bei Kombination von Scrollpane mit Table (die Ecke zwischen Table Header und Vertical Scrollbar)

        // Hmm, PopupMenu wird zumindest nicht fürs TrayIcon-Popup verwendet (bzw. nur ganz wenig)
        UIManager.put("PopupMenu.background"                     , backgroundColor); // Popup des TrayIcon, aber dort nur der Randbereich (oben und unten)
        UIManager.put("PopupMenu.foreground"                     , unknown);         //         (  0,  0,  0)    // javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
        UIManager.put("PopupMenu.selectionBackground"            , unknown);         //         ( 48,131,251)    // com.apple.laf.AquaImageFactory$SystemColorProxy[r=48,g=131,b=251]
        UIManager.put("PopupMenu.selectionForeground"            , unknown);         //         (255,255,255)    // javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
        UIManager.put("PopupMenu.translucentBackground"          , backgroundColor); // Erscheint kurzzeitig beim Aufklappen von z.B. Dropdown Listboxen

        UIManager.put("Menu.acceleratorForeground"               , unknown);         //         (  0,  0,  0)    // javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
        UIManager.put("Menu.acceleratorSelectionForeground"      , unknown);         //         (  0,  0,  0)    // javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
        UIManager.put("Menu.background"                          , unknown);         //         (255,255,255)    // javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
        UIManager.put("Menu.disabledBackground"                  , unknown);         //         (255,255,255)    // javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
        UIManager.put("Menu.disabledForeground"                  , unknown);         //         (128,128,128)    // javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
        UIManager.put("Menu.foreground"                          , unknown);         //         (  0,  0,  0)    // javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
        UIManager.put("Menu.selectionBackground"                 , unknown);         //         ( 48,131,251)    // com.apple.laf.AquaImageFactory$SystemColorProxy[r=48,g=131,b=251]
        UIManager.put("Menu.selectionForeground"                 , unknown);         //         (255,255,255)    // javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]

        UIManager.put("MenuBar.background"                       , unknown);         //         (255,255,255)    // javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
        UIManager.put("MenuBar.disabledBackground"               , unknown);         //         (255,255,255)    // javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
        UIManager.put("MenuBar.disabledForeground"               , unknown);         //         (128,128,128)    // javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
        UIManager.put("MenuBar.foreground"                       , unknown);         //         (  0,  0,  0)    // javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
        UIManager.put("MenuBar.highlight"                        , unknown);         //         (255,255,255)    // javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
        UIManager.put("MenuBar.selectionBackground"              , unknown);         //         ( 48,131,251)    // com.apple.laf.AquaImageFactory$SystemColorProxy[r=48,g=131,b=251]
        UIManager.put("MenuBar.selectionForeground"              , unknown);         //         (255,255,255)    // javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
        UIManager.put("MenuBar.shadow"                           , unknown);         //         (142,142,142)    // javax.swing.plaf.ColorUIResource[r=142,g=142,b=142]

        UIManager.put("MenuItem.acceleratorForeground"           , unknown);                //           (  0,  0,  0)    // javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
        UIManager.put("MenuItem.acceleratorSelectionForeground"  , unknown);                //           (  0,  0,  0)    // javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
        UIManager.put("MenuItem.background"                      , backgroundColor);        // Hintergrund
        UIManager.put("MenuItem.disabledBackground"              , unknown);                // offenber nicht änderbar    //           (255,255,255)    // javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
        UIManager.put("MenuItem.disabledForeground"              , new Color(64,64,64));    // Textfarbe disabled
        UIManager.put("MenuItem.foreground"                      , new Color(128,128,128)); // Textfarbe
        UIManager.put("MenuItem.selectionBackground"             , unknown);                // offenber nicht änderbar  ( 48,131,251)    // com.apple.laf.AquaImageFactory$SystemColorProxy[r=48,g=131,b=251]
        UIManager.put("MenuItem.selectionForeground"             , new Color(0,0,0));       // Textfarbe des fokussierten Items
    }

    public static class ThemeColor {
        public String name;
        public Color  color;
        public ThemeColor(String name, Color color) {
            this.name = name;
            this.color = color;
        }
    }

    public static ThemeColor[] getMatchingThemeColors(Color color) {
        final Set<Entry<Object, Object>> entries = UIManager.getDefaults().entrySet(); // der hier liefert die von mir geänderten Farben
        List<ThemeColor> ret = new ArrayList<ThemeColor>();
        for (Entry<Object, Object> entry : entries)
            if (entry.getValue() instanceof Color) {
                Color col = (Color)entry.getValue();
                if (col.getRGB() == color.getRGB()) // getRGB() ist inkl. Alphakanal!
                    ret.add(new ThemeColor((String)entry.getKey(), col));
            }
        //Collections.sort(ret); // TODO: Vielleicht mal sortieren
        return ret.toArray(new ThemeColor[0]); // ... merkwürdige Art zu "casten"
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
        final LookAndFeelInfo[] lfiList = UIManager.getInstalledLookAndFeels();
        for (LookAndFeelInfo lfi : lfiList)
            System.out.println("  " + lfi.getName() + " (" + lfi.getClassName() + ")");
    }

}
