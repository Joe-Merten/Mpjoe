package de.jme.jsi;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import de.jme.jsi.Jsi.ActionParameter;
import de.jme.jsi.Jsi.JsiAction;
import de.jme.jsi.Jsi.JsiActionEntry;
import de.jme.jsi.Jsi.JsiActionParameterException;

/**
 * Standard Monitorbefehle
 *
 * @author Joe Merten
 */
public class MoniStd {
    public static void register() {
        register(Jsi.instance);
    }
    public static void register(Jsi jsi) {
        MoniHelpAction.registerAction(jsi);
        //MoniVersionAction.registerAction(jsi);
        MoniQuitAction.registerAction(jsi);
        MoniShutdownAction.registerAction(jsi);
        MoniRestartAction.registerAction(jsi);
        MoniRebootAction.registerAction(jsi);

        MoniShowThreadsAction.registerAction(jsi);
        MoniKillThreadAction.registerAction(jsi);
        //MoniLoglevelAction.registerAction(jsi);
        //MoniMonitorAction.registerAction(jsi);
        //MoniHistoryAction.registerAction(jsi);
        //MoniDateTimeAction.registerAction(jsi);
    }

    static class MoniHelpAction extends Jsi.JsiAction {
        static void registerAction(Jsi jsi) {
            jsi.registerAction(new Jsi.JsiActionEntry("MoniFunc", "Help", "?") {
                @Override public JsiAction createAction() { return new MoniHelpAction(); }});
        }

        // Siehe http://stackoverflow.com/questions/388461/how-can-i-pad-a-string-in-java
        private static String padRight (String str, int size) {
            if (str.length() < size) {
                char[] temp = new char[size];
                int i = 0;
                while (i < str.length()) {
                    temp[i] = str.charAt(i);
                    i++;
                }
                while (i < size) {
                    temp[i] = ' ';
                    i++;
                }
                str = new String(temp);
            }
            return str;
        }


        @Override public void execute() throws Jsi.JsiException {
            // Zunächst Parameter prüfen
            throwIfHasParameters();
            if (isJustCheckParameters()) return;

            Jsi jsi = getJsi();

            Map<String, JsiActionEntry> actions = jsi.actionsByFullName;
            boolean brk = false;
            int     nameLen = 0;
            int     aliasLen = 0;
            for (int i=0; i<2 && !brk; i++) {
                for (Jsi.JsiActionEntry ae : actions.values()) {
                    String name = ae.moduleName + "." + ae.actionName;
                    String alias = ae.monitorAlias;
                    if (alias == null) alias = "";
                    if (i == 0) {
                        int len = name.length();
                        if (nameLen < len) nameLen = len;
                        len = alias.length() + 2;
                        if (aliasLen < len) aliasLen = len;
                    } else {
                        if (name.length() < nameLen) name = padRight(name, nameLen);
                        if (!alias.isEmpty()) alias = "(" + alias + ")";
                        if (alias.length() < aliasLen) alias = padRight(alias, aliasLen);
                        out.println(name + " " + alias + " - " + ae.createAction().getHelp());
                    }
                }
            }
        }

        @Override public String getHelp() {
            return "Shows all available actions.";
        }

        @Override public String getUsage() {
            return null;
        }
    }

    static class MoniQuitAction extends Jsi.JsiAction {
        static void registerAction(Jsi jsi) {
            jsi.registerAction(new Jsi.JsiActionEntry("MoniFunc", "Quit", "Q") {
                @Override public JsiAction createAction() { return new MoniQuitAction(); }});
        }

        @Override public void execute() throws Jsi.JsiException {
            throwIfHasParameters();
            if (isJustCheckParameters()) return;
            Thread.currentThread().interrupt();
        }

        @Override public String getHelp() {
            return "Quit the current monitor session (could terminate the application if we are the main monitor session)";
        }

        @Override public String getUsage() {
            return null;
        }
    }

    static class MoniShutdownAction extends Jsi.JsiAction {
        static void registerAction(Jsi jsi) {
            jsi.registerAction(new Jsi.JsiActionEntry("MoniFunc", "Shutdown", "Halt") {
                @Override public JsiAction createAction() { return new MoniShutdownAction(); }});
        }

        @Override public void execute() throws Jsi.JsiException {
            throwIfWrongParameterCount(0, 1);
            int exitcode = 240;
            if (getParameterCount() >= 1)
                exitcode = getParameter(0).toInt();  // TODO SH: ist das "toInt()" hier ein geeigneter Methodenname?
            if (exitcode < 0 || exitcode > 255)
                throw new JsiActionParameterException("Exitcode must be in range 0...255");
            if (isJustCheckParameters()) return;
            System.exit(exitcode);
        }

        @Override public String getHelp() {
            return "Shutdown the application with exitcode 240 (which may inform the calling startscript that this is an intentionally application shutdown)";
        }

        @Override public String getUsage() {
            return "[<exitcode>]  Optional exitcode, default is 240 (which may inform the calling startscript that this is an intentionally application shutdown)";
        }
    }

    static class MoniRestartAction extends Jsi.JsiAction {
        static void registerAction(Jsi jsi) {
            jsi.registerAction(new Jsi.JsiActionEntry("MoniFunc", "Restart") {
                @Override public JsiAction createAction() { return new MoniRestartAction(); }});
        }

        @Override public void execute() throws Jsi.JsiException {
            throwIfHasParameters();
            if (isJustCheckParameters()) return;
            System.exit(241);
        }

        @Override public String getHelp() {
            return "Shutdown the application with exitcode 241 (which may inform the calling startscript to restart me)";
        }

        @Override public String getUsage() {
            return null;
        }
    }

    static class MoniRebootAction extends Jsi.JsiAction {
        static void registerAction(Jsi jsi) {
            jsi.registerAction(new Jsi.JsiActionEntry("MoniFunc", "Reboot", "") {
                @Override public JsiAction createAction() { return new MoniRebootAction(); }});
        }

        @Override public void execute() throws Jsi.JsiException {
            throwIfHasParameters();
            if (isJustCheckParameters()) return;
            System.exit(242);
        }

        @Override public String getHelp() {
            return "Shutdown the application with exitcode 242 (which may inform the calling startscript to reboot the system)";
        }

        @Override public String getUsage() {
            return null;
        }
    }


    static class MoniShowThreadsAction extends Jsi.JsiAction {
        static void registerAction(Jsi jsi) {
            jsi.registerAction(new Jsi.JsiActionEntry("Threads", "ShowThreads", "ps") {
                @Override public JsiAction createAction() { return new MoniShowThreadsAction(); }});
        }

        @Override public void execute() throws Jsi.JsiException {
            // Zunächst Parameter prüfen
            throwIfHasParameters();
            if (isJustCheckParameters()) return;

            Set<Thread> threadSet = Thread.getAllStackTraces().keySet();

            // Sortiere nach Name und Id
            Comparator<Thread> comp = new Comparator<Thread>() {
                @Override public int compare(Thread o1, Thread o2) {
                    return (int)o1.getId() - (int)o2.getId();
                }};
            TreeSet<Thread>sortedSet = new TreeSet<Thread>(comp);
            sortedSet.addAll(threadSet);

            for (Thread t : sortedSet) {
                String msg;
                if (t.isDaemon())
                    msg = "Daemon ";
                else
                    msg = "Thread ";
                msg += t.getId() + " \"" + t.getName() + "\", State " + t.getState();

                { // Thread Defails - hier stehen auch noch mal der Threadname, die Id  und der State drin
                    ThreadMXBean threadmx = ManagementFactory.getThreadMXBean();
                    ThreadInfo info = threadmx.getThreadInfo(t.getId());
                    msg += " (" + info.toString().trim() + ")";
                }
                out.println(msg);
            }
        }

        @Override public String getHelp() {
            return "Shows all java threads.";
        }

        @Override public String getUsage() {
            return null;
        }
    }

    static class MoniKillThreadAction extends Jsi.JsiAction {
        static void registerAction(Jsi jsi) {
            jsi.registerAction(new Jsi.JsiActionEntry("Threads", "KillThread", "kill") {
                @Override public JsiAction createAction() { return new MoniKillThreadAction(); }});
        }

        @Override public void execute() throws Jsi.JsiException {
            // Zunächst Parameter prüfen
            throwIfWrongParameterCount(1);
            ActionParameter ap = getParameter(0);
            int tid = ap.toInt();
            if (isJustCheckParameters()) return;

            Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
            for (Thread t : threadSet) {
                if (t.getId() == tid) {
                    t.interrupt();
                    return;
                }
            }
            throw new IllegalArgumentException("Thread with Id " + tid + " not found");
        }

        @Override public String getHelp() {
            return "Requests termination of a java thread by invoking Thread.interrupt().";
        }

        @Override public String getUsage() {
            return "<Thread-Id>";
        }
    }

    /*static class MoniLoglevelAction extends Jsi.JsiAction {
        static void registerAction(Jsi jsi) {
            jsi.registerAction(new Jsi.JsiActionEntry("MoniFunc", "Loglevel", "log") {
                @Override public JsiAction createAction() { return new MoniLoglevelAction(); }});
        }

        @Override public void execute() throws Jsi.JsiException {
            // Zunächst Parameter prüfen
            throwIfWrongParameterCount(0, 1);
            int log = -1;
            if (getParameterCount() > 0) {
                ActionParameter ap = getParameter(0);
                int n = 0;
                for (Level l : Level.values()) {
                    if (l.toString().equalsIgnoreCase(ap.value)) {
                        log = n;
                        break;
                    }
                    n++;
                }
                if (log == -1)
                    log = ap.toInt();
            }
            if (isJustCheckParameters()) return;

            if (log >= 0) {
                Level level = Level.values()[log];
                LogLevelHelper.setLoggerRootLevel(level);
                out.println("Set loglevel to " + log + " = " + level);
            } else {
                Level level = LogLevelHelper.getLoggerRootLevel();
                out.println("Root-Loglevel is " + level.ordinal() + " = " + level);
            }
        }

        @Override public String getHelp() {
            return "Sets the global loglevel";
        }

        @Override public String getUsage() {
            String ret = "<Loglevel (0..6)>";
            int n = 0;
            for (Level l : Level.values()) {
                ret += "\n  " + n + " = " + l;
                n++;
            }
            return ret;
        }
    }*/

}
