package de.jme.jsi;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Scripting Interface
 *
 * Siehe auch:
 *     http://affy.blogspot.de/2003/05/simple-extensible-command-line.html
 *     http://java-source.net/open-source/command-line
 *     http://commons.apache.org/proper/commons-cli/introduction.html
 *
 * @author Joe Merten
 */
public class Jsi {
    /**
     * Globale Instanz des Jsi.
     *
     * Üblicherweise gibt es eine globale Instanz des Jsi.
     * Zu Testzwecken (also auch für die automatisierten Tests) können aber auch weitere (z.B. temporäre) Instanzen erzeugt werden.
     */
    public static Jsi instance = new Jsi();

    /**
     * Exception für ungewöhnliche Umstände.
     */
    public static class JsiException extends Exception {
        private static final long serialVersionUID = -3139602980731050791L;
        public JsiException(String desc) { super(desc); }
    }

    public static class JsiUnknownActionException extends JsiException {  // TODO: Hmm, hier mit "Jsi" prefixen?
        private static final long serialVersionUID = 2846397047881528572L;
        public JsiUnknownActionException(String desc) { super(desc); }
    }

    public static class JsiActionParameterException extends JsiException {
        private static final long serialVersionUID = 4246863191635109629L;
        public JsiActionParameterException(String desc) { super(desc); }
    }

    /**
     * Informationen bzgl. Streams, Benutzerdaten, Rechte et cetera, die der Aufrufer einer Action mitgeben muss
     */
    public static class SessionData {
        public InputStream  in;            ///< Für interaktive Actions, z.B. Monitorbefehle mit Rückfrage j/n oder Esc für Abbruch. Wird durch den Aufrufer von Jsi::executeAction() übergeben.
        public PrintStream  out;           ///< Falls die Action etwas auszugeben hat. Wird durch den Aufrufer von Jsi::executeAction() übergeben.
        public boolean      interactive;   ///< false bei Skriptverarbeitung (z.B. MemoryStream), true bei z.B. interaktivem Debugmonitor (z.B. Konsole)
        public SessionData(InputStream is, PrintStream os) {
            this.in = is;
            this.out = os;
        }

        @Override public String toString() {
            return "SessionData [in=" + ((in != null) ? "..." : in) + ", out=" + ((out != null) ? "..." : out) + ", interactive=" + interactive + "]";
        }
    }

    /**
     * Beschreibung eines Parameters, der der Action übergeben wurde.
     */
    public static class ActionParameter {
        public String value;
        public boolean hasQuotes;

        public void setFromString(String s, boolean hasQuotes) {
            value = s;
            this.hasQuotes = hasQuotes;
        }

        public int toInt() throws JsiActionParameterException {
            if (hasQuotes)
                throw new JsiActionParameterException("Integer value expected but got string parameter \"" + value + "\"");
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                throw new JsiActionParameterException("Integer value expected but got \"" + value + "\"");
            }
        }
    }

    /**
     * Alle Aufrufparameter, die der Action übergeben wurde.
     */
    public static class ActionParameters extends ArrayList<ActionParameter> {
        private static final long serialVersionUID = 4330209050663826062L;

        //public List<ActionParameter> list;

        public ActionParameters() {
            //list = new ArrayList<ActionParameter>();
        }

        public void setFromCommandLine(String parameters) throws JsiActionParameterException {
            /*list.*/clear();
            if (parameters == null) return;
            parameters = parameters.trim();
            if (parameters.isEmpty()) return;

            int i=0;
            for (;;) {
                // Leading Whitespace ignorieren
                for (;;) {
                    if (i >= parameters.length()) break;
                    char c = parameters.charAt(i);
                    if (c!=' ' && c!='\t') break;
                    i++;
                }

                // Keine weiteren Parameter im String?
                if ( i>= parameters.length()) break;

                char c = parameters.charAt(i);
                if (c != '"') {
                    // Alle Zeichen bis Whitespace gehören zum Parameter
                    int begin=i;
                    int end=i;
                    for (;;) {
                        if (i >= parameters.length()) break;
                        c = parameters.charAt(i);
                        if (c==' ' || c=='\t') break;
                        i++;
                    }
                    end=i;
                    ActionParameter ap = new ActionParameter();
                    ap.setFromString(parameters.substring(begin, end), false);
                    /*list.*/add(ap);
                } else {
                    // Alle Zeichen bis zum nächsten " gehören zum Parameter
                    StringBuilder s = new StringBuilder(parameters.length());
                    i++;
                    for (;;) {
                        if (i >= parameters.length())
                            throw new JsiActionParameterException("Missing closing quote.");
                        c = parameters.charAt(i);
                        if (c=='"') break;
                        if (c != '\\') {
                            // Ok, das Zeichen einfach dranhängen
                            s.append(c);
                        } else {
                            // Quoting auswerten
                            i++;
                            if (i >= parameters.length())
                                throw new JsiActionParameterException("Missing char after backslash.");
                            c = parameters.charAt(i);
                            switch (c) {
                            case 'n': s.append('\n'); break;
                            case 'r': s.append('\r'); break;
                            case 't': s.append('\t'); break;
                            case '"':
                            case '\'':
                            case '\\': s.append(c); break;
                            case 'x':
                                throw new JsiActionParameterException("Sorry, \\xNN is not implemented");
                            case 'u':
                                throw new JsiActionParameterException("Sorry, \\uNNNN is not implemented");
                            default:
                                throw new JsiActionParameterException("Unknown char after backslash (" + (int)c + ").");
                            }
                        }
                        i++;
                    }
                    i++; // Closing Quote auch noch weglesen
                    ActionParameter ap = new ActionParameter();
                    ap.setFromString(s.toString(), true);
                    /*list.*/add(ap);
                }
            }
        }
    }

    /**
     * Aufrufparameter einer Action und weitere Zusatzinformationen für den Aufruf.
     *
     * Absichtlich nicht static, denn die Klasse soll die Parent-Instanz kennen.
     */
    public class JsiActionExt {
        public SessionData       session;
        public ActionParameters  parameters;          ///< Menge aller aktueller Übergabeparameter
        public boolean           justCheckParameters; ///< Action soll nicht wirklich ausgeführt werden, sondern lediglich die Übergabeparameter checken

        public JsiActionExt(SessionData session, boolean justCheckParameters) {
            this.session = session;
            this.justCheckParameters = justCheckParameters;
            parameters = new ActionParameters();
        }

        public Jsi getJsi() {
            return Jsi.this;
        }

        @Override public String toString() {
            return "JsiActionExt [session=" + session + ", parameters=" + parameters + ", justCheckParameters=" + justCheckParameters + "]";
        }
    }

    /**
     * Basisklasse für alle Jsi Actions
     */
    public static abstract class JsiAction {
        public JsiActionExt ext;
        public InputStream  in;
        public PrintStream  out;

        /* Beispielimplementation - jede JsiAction sollte eine solche statische Methode besitzen.
        static void registerAction(Jsi jsi) {
            jsi.registerAction(new Jsi.JsiActionEntry("MoniFunc", "Sample", "s") {
                @Override public JsiAction createAction() { return new MoniSampleAction(); }});
        } */

        public JsiAction() {
        }

        /**
         * Ausführung der Action - muss von jeder konkreten Action implementiert werden.
         */
        public abstract void execute() throws Throwable;

        /**
         * Liefert eine kurze (einzeilige) Beschreibung, was die Action macht.
         */
        public abstract String getHelp();

        /**
         * Liefert eine Beschreibung der Aufrufparameter.
         */
        public abstract String getUsage();

        public Jsi getJsi() {
            return ext.getJsi();
        }

        public boolean isInteractive() {
            return ext.session.interactive;
        }

        public ActionParameters getParameters() {
            return ext.parameters;
        }

        public int getParameterCount() {
            return ext.parameters.size();
        }

        public ActionParameter getParameter(int i) {
            return ext.parameters.get(i);
        }

        public boolean isJustCheckParameters() {
            return ext.justCheckParameters;
        }

        /**
         * Wirft eine Exception, wenn die Action mit Parametern aufgerufen wurde.
         *
         * Diese Methode ist für Actions gedacht, die keine Aufrufparameter haben.
         * Der Aufruf dieser Methode vereinfacht dann die Parameterprüfung.
         */
        protected void throwIfHasParameters() throws JsiActionParameterException {
            if (getParameterCount() != 0)
                throw new JsiActionParameterException("This action doesn't accept any parameters");
        }

        /**
         * Wirft eine Exception, wenn die Action mit falscher Anzahl von Parametern aufgerufen wurde.
         *
         * @param  expected  Anzahl der Aufrufparameter, die die Action erwartet.
         *
         * Der Aufruf dieser Methode vereinfacht die Parameterprüfung.
         */
        protected void throwIfWrongParameterCount(int expected) throws JsiActionParameterException {
            if (expected == 0) throwIfHasParameters();

            if (getParameterCount() != expected)
                throw new JsiActionParameterException("This action needs " + expected + " parameters");
        }

        /**
         * Wirft eine Exception, wenn die Action mit falscher Anzahl von Parametern aufgerufen wurde.
         *
         * @param  expectedMin  Minimale Anzahl der Aufrufparameter, die die Action erwartet.
         * @param  expectedMax  Maximale Anzahl der Aufrufparameter, die die Action erwartet.
         *
         * Der Aufruf dieser Methode vereinfacht die Parameterprüfung.
         */
        protected void throwIfWrongParameterCount(int expectedMin, int expectedMax) throws JsiActionParameterException {
            if (expectedMin == expectedMax) throwIfWrongParameterCount(expectedMin);
            int count = getParameterCount();

            if (expectedMin == 0 && count > expectedMax)
                throw new JsiActionParameterException("Too many parameters (max. " + expectedMax + " parameters expected but got " + count + ")");

            if (count < expectedMin || count > expectedMax)
                throw new JsiActionParameterException("This action needs " + expectedMin + " to " + expectedMax + " parameters but got " + count);
        }

        /**
         * Wirft eine Exception, um eine ungültige Aufrufoption anzuzeigen.
         *
         * @param  option  Aufrufoption, die nicht akzeptiert wird
         *
         * Der Aufruf dieser Methode vereinfacht die Parameterprüfung.
         */
        protected void throwInvalidOption(String option) throws JsiActionParameterException {
            throw new JsiActionParameterException("Invalid option '" + option + "'");
        }

        /**
         * Liefert true zurück, wenn eine Abbruchtaste (ESC, ^D, ^C, x, q) im InStream anliegt.
         *
         * Der Aufruf dieser Methode vereinfacht das Beenden fortlaufender Ausgaben.
         *
         * Läuft die App in der Eclipse Konsole, so ist diese nicht in den Character-Mode umschaltbar (sondern läuft im
         * Canonical-Mode).
         * In diesem Fall kommen die Daten erst nach Enter bei uns an - der Anwender muss dann z.B. x + Enter drücken.
         *
         * Die Möglichkeit, hier einen Abbruch via Esc wird hier evtl. wieder entfernt, da mittels Esc bei VT100 Terminals auch
         * Sondertasten (z.B. Cursortasten) dargestellt werden. Evtl. verwenden wir hier aber auch mal die Funktionalitäten aus
         * Terminal.hxx.
         */
        protected boolean isEndRequested(int sleepTime) {
            if (!isInteractive()) {
                // Keine Abfrage, wenn wir nicht in einer interaktiven Session sind.
                // Sonst fallen wir mit Monitor-Skripten auf die Nase, z.B. wenn diese als Kommandozeilenparameter bei Programmstart mitgegeben wurden
                // oder auch bei den MemoryStreams, mit denen der Monitor bei den automatisierten Tests gefüttert wird.
                return false;
            }

            // TODO...
            return false;
        }
    }

    /**
     * Entry zur Speicherung einer Action am Jsi in einem Container.
     */
    public static abstract class JsiActionEntry {
        public final String                moduleName;     ///< Name des SW-Moduls, welchem die Action angehört (quasi Kategorie/Gruppenbildung)
        public final String                actionName;     ///< Name der Action
        public final String                monitorAlias;   ///< Kurzname für Debugmonitor

        public JsiActionEntry(String moduleName, String actionName, String monitorAlias) {
            this.moduleName = moduleName;
            this.actionName = actionName;
            this.monitorAlias = monitorAlias;
        }

        public JsiActionEntry(String moduleName, String actionName) {
            this.moduleName = moduleName;
            this.actionName = actionName;
            this.monitorAlias = null;
        }

        public abstract JsiAction createAction();
    }


    Map<String, JsiActionEntry> actionsByName;            ///< Menge aller angemeldeter Actions
    Map<String, JsiActionEntry> actionsByUpperName;       ///< Menge aller angemeldeter Actions Uppercase
    Map<String, JsiActionEntry> actionsByAlias;           ///< Aliasnamen für den Debugmonitor Uppercase
    Map<String, JsiActionEntry> actionsByFullName;        ///< Mit vollem Namen, also "Module.Function" in Uppercase (erst mal nur für die Sortierung in Monitorbefehl "Help")


    public Jsi() {
        actionsByName      = new TreeMap<String, JsiActionEntry>();
        actionsByUpperName = new TreeMap<String, JsiActionEntry>();
        actionsByAlias     = new TreeMap<String, JsiActionEntry>();
        actionsByFullName  = new TreeMap<String, JsiActionEntry>();
    }

    public void registerAction(JsiActionEntry entry) {
        // TODO: Prüfen, ob Name schon vergeben ist
        actionsByName.put(entry.actionName, entry);

        // Zusätzlich als UpperCase, damit die Befehle im Debugmonitor nicht case-sensitiv eingegeben werden müssen
        actionsByUpperName.put(entry.actionName.toUpperCase(), entry);

        if (entry.monitorAlias != null && !entry.monitorAlias.isEmpty())
            actionsByAlias.put(entry.monitorAlias.toUpperCase(), entry);

        actionsByFullName.put(new String(entry.moduleName + "." + entry.actionName).toUpperCase(), entry);
    }

    // TODO: deregisterAction

    public void executeAction(String commandLine, SessionData session) throws Throwable  {
        // Leerzeilen erlauben -> also ohne Aktion returnieren
        if (commandLine == null) return;
        commandLine = commandLine.trim();
        if (commandLine.isEmpty()) return;
        // Auskommentierte Zeilen wie Leerzeilen behandeln -> also ohne Aktion returnieren
        if (commandLine.startsWith("#") || commandLine.startsWith("//")) return;

        // Kommandozeile ggf. in 2 Teile zerlegen (commandName & parameters)
        String commandName = commandLine;
        String parameters = null;
        int p = commandLine.indexOf(" ");
        if (p >= 0) {
            commandName = commandLine.substring(0, p).trim();
            parameters = commandLine.substring(p+1).trim();
        }

        JsiActionEntry entry = null;
        String commandUpperName = commandName.toUpperCase();
        entry = actionsByFullName.get(commandUpperName);
        if (entry == null)
            entry = actionsByUpperName.get(commandUpperName);
        if (entry == null)
            entry = actionsByAlias.get(commandUpperName);

        if (entry == null)
            throw new JsiUnknownActionException("Action \"" + commandName + "\" not found.");

        JsiAction action = entry.createAction();

        if (parameters != null && (parameters.equals("?") || parameters.equals("-h") || parameters.equals("--help"))) {
            String commandCamelName = entry.actionName;
            String usageInfo = action.getUsage();
            if (usageInfo == null || usageInfo.isEmpty()) usageInfo = "\nSorry, but there is no help available for this command.";
            session.out.println("Usage: " + commandCamelName + " " + usageInfo);
        } else {
            JsiActionExt ext = new JsiActionExt(session, false);
            ext.parameters.setFromCommandLine(parameters);
            action.ext = ext;
            action.in  = ext.session.in;
            action.out = ext.session.out;
            action.execute();
            action.ext = null; // ist eigentlich nicht notwendig, da die Action eh' gleich destruiert wird
            action.in  = null;
            action.out = null;
        }
    }

}
