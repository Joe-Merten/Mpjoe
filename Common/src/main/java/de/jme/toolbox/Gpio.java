package de.jme.toolbox;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Klasse zum Zugriff auf die GPIOs über das Kernel-Interface /sys/class/gpio,
 * daher werden auf dem aktuellen Buildroot-System Root-Rechte benötigt.
 *
 * Von der Klasse darf nur eine Instanz angelegt werden, beim Beenden sollte close()
 * gerufen werden.
 *
 * Wir verwenden einen ShutdownHook, damit bei Programmende, Ctrl-C oder
 * anderen Programmabbrüchen die Gpios aufgeräumt werden.
 *
 * @author sven
 */
public class Gpio implements Closeable {

    static Logger logger = LoggerFactory.getLogger(Gpio.class);

    private static Map<Integer, Gpio> allExportedInstances = new LinkedHashMap<Integer,Gpio>();

//    public Gpio(int gnum) {  // create a GPIO object that controls GPIOx, where x is passed to this constructor
//        gpioNumStr = Integer.toString(gnum);        // Instatiate GPIOClass object for GPIO pin number "gnum"
//    }

    /** Richtung des Gpio: rein oder raus **/
    public enum Direction {IN, OUT};

    public static Gpio createOrGet(int gnum, Gpio.Direction dir, boolean allowReuse) throws IOException {
        synchronized (allExportedInstances) {
            Gpio ret = allExportedInstances.get(gnum);
            if (ret != null) {  // für diesen Gpio gibt es bereits eine Instanz
                if (!allowReuse || !ret.allowReuse)
                    throw new IllegalArgumentException("Gpio " + gnum + " is already in use and reuse is not allowed " + ret);
                if (dir != Direction.IN || ret.direction != Direction.IN)
                    throw new IllegalArgumentException("Gpio " + gnum + " is already in use with different or OUT direction " + ret);
            } else {
                ret = new Gpio(gnum, dir, allowReuse);
            }
            return ret;
        }
    }

    public static boolean isCreated(int gnum, Gpio.Direction dir) {
        synchronized (allExportedInstances) {
            Gpio ret = allExportedInstances.get(gnum);
            return (ret != null && ret.direction == dir);
        }
    }

    private Gpio(int gnum, Gpio.Direction dir, boolean allowReuse) throws IOException {  // create a GPIO object, export it and set direction
        this.gnum = gnum;
        gpioNumStr = Integer.toString(gnum);        // Instatiate GPIOClass object for GPIO pin number "gnum"
        this.allowReuse = allowReuse;
        try {
            export();
        } catch (IOException e) {
            logger.warn("Can't export Gpio " + gpioNumStr + ". Perhaps unexport was missing because of program abort at last run.");
        }
        setDirection(dir);
    }

    private static void addInstance(Gpio gpio) {
        synchronized (allExportedInstances) {
            allExportedInstances.put(gpio.getNum(), gpio);
        }
    }

    private static void removeInstance(Gpio gpio) {
        synchronized (allExportedInstances) {
            allExportedInstances.remove(gpio.getNum());
        }
    }

    @Override public void close() throws IOException {  // Cleanup: close() is a synonym to unexport, unexport Gpio, so it can later be exportted again
        synchronized (allExportedInstances) {
            if (allExportedInstances.containsKey(gnum)) {
                logger.trace("Closing " + getName());
                unexport();
            } else {
                logger.trace("Note: " + getName() + " was already closed.");
            }
        }
    }

    public void setDirection(Gpio.Direction dir) throws IOException  { // Set GPIO Direction
        String setdirFnam ="/sys/class/gpio/gpio" + gpioNumStr + "/direction";
        FileOutputStream fos = new FileOutputStream(setdirFnam); // open direction file for gpio
        try {
            String s = (dir == Direction.IN) ? "in" : "out";
            fos.write(s.getBytes()); //write direction to direction file
            direction = dir;
        } finally {
            fos.close(); // close direction file
        }
    }

    public Direction getDirection() { // return the GPIO direction
        return direction;
    }

    public void setVal(boolean val) throws IOException { // Set GPIO Value (putput pins)
        String setvalFnam = "/sys/class/gpio/gpio" + gpioNumStr + "/value";
        FileOutputStream fos = new FileOutputStream(setvalFnam); // open value file for gpio
        try {
            fos.write(val ? '1' : '0'); //write value to value file
        } finally {
            fos.close();// close value file
        }
    }

    public boolean isHigh() throws IOException { // Get GPIO Value (input/ output pins)
        String getvalFnam = "/sys/class/gpio/gpio" + gpioNumStr + "/value";
        FileInputStream getvalFis = new FileInputStream(getvalFnam);// open value file for gpio
        try {
            byte val = (byte) getvalFis.read();  //read gpio value
            return (val != '0');
        } finally {
            getvalFis.close(); //close the value file
        }
    }

    void enableEdgeTrigger(boolean on) throws IOException {
        String enableTriggerFnam ="/sys/class/gpio/gpio" + gpioNumStr + "/edge";
        FileOutputStream fos = new FileOutputStream(enableTriggerFnam);
        try {
            fos.write(on ? "both".getBytes() : "none".getBytes());   // enable/disable edge triggerig
        } finally {
            fos.close();  // close trigger file
        }
    }
    public static boolean waitUntilAnyButtonChanges(Gpio first, Gpio second, int sampleRateMs, int maxDurationMs) throws IOException, InterruptedException {
//        first.enableEdgeTrigger(true);
//        second.enableEdgeTrigger(true);
//
//        Selector selector = Selector.open();
//
//        String getvalFnam = "/sys/class/gpio/gpio" + first.gpioNumStr + "/value";
//        FileInputStream getvalFis = new FileInputStream(getvalFnam);// open value file for gpio
//        FileChannel input = new FileInputStream(getvalFnam).getChannel();
//        SelectableChannel sc = input;   // TODO: woher?
//        sc.configureBlocking(false);
//        SelectionKey key = sc.register(selector, SelectionKey.OP_READ);
//
        return false;
    }

    public int getNum() { // return the GPIO number associated with the instance of an object
        return Integer.parseInt(gpioNumStr);
    }

    public String getName() {
        return "Gpio " + gpioNumStr;
    }

    @Override public String toString() {
        String ret = getName();
        if (direction != null)
            ret += " (" + direction + ")";
        ret += ", allow reuse " + allowReuse;
        return ret;
    }

//    @Override protected void finalize() {  // Cleanup: unexport Gpio, so it can later be exportted again
//        try {
//            unexport();
//        } catch (IOException e) {
//            logger.warn("Cannot unexport " + this + " " + e);
//        }
//    }

    protected void export() throws IOException { // exports GPIO
        final String exportFnam = "/sys/class/gpio/export"; // Open "export" file.
        FileOutputStream fos = new FileOutputStream(exportFnam);
        try {
            addInstance(this);             // always add to list
            fos.write(gpioNumStr.getBytes()); //write GPIO number to export
        } finally {
            fos.close(); //close export file
        }
    }

    protected void unexport() throws IOException { // unexport GPIO
        unexportImpl();
        removeInstance(this);
    }


    protected void unexportImpl() throws IOException { // unexport GPIO, do not remove from global export list
        final String unexportFnam = "/sys/class/gpio/unexport";
        FileOutputStream fos = new FileOutputStream(unexportFnam); // Open unexport file
        try {
            fos.write(gpioNumStr.getBytes()); // write GPIO number to unexport
        } finally {
            fos.close(); //close unexport file
        }
    }

    private int gnum;                           // GPIO number associated with the instance of an object
    private String gpioNumStr;                  // GPIO number associated with the instance of an object as String
    private Gpio.Direction direction = null;    // Direction of the Port, (null) if unknown
    private boolean allowReuse;

    /**
     * Wir verwenden einen ShutdownHook, damit bei Programmende, Ctrl-C oder anderen Programmabbrüchen
     * die Gpios ent-exportiert werden, sonst gibt es beim nächsten Programmstart Warnungen,
     * da die Gpio-Exporte bis zum nächsten Reboot (oder unexport) bestehen bleiben.
     */
    private static class ShutdownHook extends Thread {

        synchronized public void run() {
            logger.debug("Running shutdownhook: unexporting " + allExportedInstances);
            for (Gpio gp : allExportedInstances.values()) {
                try {
                    gp.unexportImpl();
                } catch (IOException e) {
                    logger.warn("Cannot unexport " + gp + " at ShutdownHook " + e);
                }
            }
            allExportedInstances.clear();
        }

    }

    static {
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());
    }


}