package de.jme.jsi;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.jme.jsi.Jsi.JsiAction;

/**
 * Für die ersten Entwicklertests mit Jsi & Debugmonitor
 */
public class Sample {

    static final Logger logger = LogManager.getLogger(Sample.class);

    public Sample() {
        // TODO Auto-generated constructor stub
    }

    static class MoniSampleAction extends Jsi.JsiAction {
        static void registerAction(Jsi jsi) {
            jsi.registerAction(new Jsi.JsiActionEntry("MoniFunc", "Sample", "s") {
                @Override public JsiAction createAction() { return new MoniSampleAction(); }});
        }

        @Override public void execute() throws Jsi.JsiException {
            if (isJustCheckParameters()) return;

            out.println("Hey, it's me, the sample debug monitor command!");
            out.println("You have provided " + getParameterCount() + " arguments");
            int i = 0;
            for (Jsi.ActionParameter ap : getParameters()) {
                out.printf("%3d: %s\n", i, ap.value);
                i++;
            }
        }

        @Override public String getHelp() {
            return "Sample implementation of a Jsi registered debug monitor command";
        }

        @Override public String getUsage() {
            return "\n" +
                    "Usage hints for the sample debug monitor command.\n" +
                    "Just call me with or without some parameters,\n" +
                    "I'll echo them back to you.\n";
                    /*"When giving a 'q' or 'Q' as 1st parameter,\n" +
                    "I'll query you interactive for some input.";*/
        }
    }

    static class MoniSampleAction2 extends Jsi.JsiAction {
        static void registerAction(Jsi jsi) {
            jsi.registerAction(new Jsi.JsiActionEntry("MoniFunc", "SampleBla", "bla") {
                @Override public JsiAction createAction() { return new MoniSampleAction2(); }});
        }

        @Override public void execute() throws Jsi.JsiException {
            if (isJustCheckParameters()) return;
            out.println("Hello World nummer 2.");
        }

        @Override public String getHelp() {
            return "Noch ein test";
        }

        @Override public String getUsage() {
            return "Tri tra tralala";
        }
    }

    public static void main(String[] args) {
        Jsi jsi = Jsi.instance;
        MoniSampleAction.registerAction(jsi);
        MoniSampleAction2.registerAction(jsi);
        MoniStd.register(jsi);

        Thread t;
        try {
            t = new Thread(new Monitor(), "Monitor");
            t.start();
            t.join();
        } catch (InterruptedException e) {
            logger.error("Thread interrupted", e);
        } catch (IOException e) {
            logger.error("main() with", e);
        }
    }

}
