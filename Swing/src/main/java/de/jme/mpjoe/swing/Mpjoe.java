package de.jme.mpjoe.swing;

// Trotz diverser Versuche ist es mir leider nicht gelungen, den in Osx sichtbaren Applikationsnamen programmseitig zu ändern
// Einzig  java -Xdock:name="Tralala"  hat funktioniert, aber das finde ich doof
// Mein Workaround: Mainclass = "Mpjoe", damit steht im Osx Menü "Mpjoe" - fertig.

// Hier: https://developer.apple.com/library/mac/documentation/Java/Conceptual/Java14Development/07-NativePlatformIntegration/NativePlatformIntegration.html
// steht noch eine weitere Möglichkeit "... or it can be set in the information property list file for your application as the CFBundleName value ..."
// aber dazu müsste ich wohl ein Apple-konformes Application Bundle bauen.

// Für's Osx Menü
// - Der Tip von http://stackoverflow.com/questions/2553941/programatically-setting-the-dockname-java-mac-os-x-jvm-property
//   mittels System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Tralala");
//   functioniert wohl nicht mehr
// - wohin gegen das java -Xdock:name="Tralala" -jar myapp.jar
//   noch funktioniert
//
//System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Hello World!");
//System.setProperty("apple.awt.graphics.EnableQ2DX", "true");
//System.setProperty("apple.laf.useScreenMenuBar", "true");
//System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Trullala");

public class Mpjoe {

    public static void main(final String[] args) {
        de.jme.toolbox.logging.Log4jConfigure.configureRollingFile();
        MainWin.main(args);
    }
}
