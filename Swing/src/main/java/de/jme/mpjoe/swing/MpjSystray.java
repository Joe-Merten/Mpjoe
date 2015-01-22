package de.jme.mpjoe.swing;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.Timer;

import de.jme.toolbox.SystemInfo;

public class MpjSystray {

    private TrayIcon   trayIcon;
    private Image      trayIconImage;
    private JPopupMenu trayPopup;
    private boolean    iconAnimation = false;

    public MpjSystray() {
        // TODO: Icons nur 1x laden und an verschiedenen Stellen verwenden, siehe auch MainWin, frame.setIconImages()
        final Image icon16  = new ImageIcon(System.class.getResource("/de/jme/mpj/Icon/Mpjoe-Icon-16.png")).getImage();
        final Image icon20  = new ImageIcon(System.class.getResource("/de/jme/mpj/Icon/Mpjoe-Icon-20.png")).getImage();
        final Image icon24  = new ImageIcon(System.class.getResource("/de/jme/mpj/Icon/Mpjoe-Icon-24.png")).getImage();
        final Image icon32  = new ImageIcon(System.class.getResource("/de/jme/mpj/Icon/Mpjoe-Icon-32.png")).getImage();
        final Image icon48  = new ImageIcon(System.class.getResource("/de/jme/mpj/Icon/Mpjoe-Icon-48.png")).getImage();
        final Image icon64  = new ImageIcon(System.class.getResource("/de/jme/mpj/Icon/Mpjoe-Icon-64.png")).getImage();
        final Image icon128 = new ImageIcon(System.class.getResource("/de/jme/mpj/Icon/Mpjoe-Icon-128.png")).getImage();
        final Image icon256 = new ImageIcon(System.class.getResource("/de/jme/mpj/Icon/Mpjoe-Icon-256.png")).getImage();

        //--------------------
        // Kleiner Test mit dem SystemTray
        if (SystemTray.isSupported()) {
            final SystemTray tray = SystemTray.getSystemTray();
            Dimension size = tray.getTrayIconSize();
            System.out.println("*** Yep, SystemTray found, trayIconSize = " + tray.getTrayIconSize());
            // TrayIconSize
            // - Kubuntu      24 x 24  - auf BBB korrekt, aber auf Ernie nicht diese Angabe ich nicht korrekt, es sind 48 x 48!
            //                           ein zu kleines Icon wird nicht skaliert, sondern links oben in die Ecke geklatscht
            // - Osx          20 x 20  - mein 16er Icon sient etwas mikrig aus, das 32er wird vermutlich etwas geschrumpft
            // - Windows XP   16 x 16  - das 16er sieht ok aus, von dem 32er ist aber nur 1/4 zu sehen
            int iconSize = Math.min((int)size.getWidth(), (int)size.getHeight());
            if (SystemInfo.isLinux() && iconSize == 24) {
                if (SystemInfo.getComputerName().toLowerCase().equals("ernie") && Toolkit.getDefaultToolkit().getScreenResolution() == 145) {
                    // Hack, weil bei meinem Kubuntu 14.04 eine zu kleine TrayIconSize geliefert wird
                    // getScreenResolution() liefert auf meinem Dell Notebook 145 dpi, auch wenn ich die Auflösung mit xrdb auf 100 gesetzt habe
                    System.out.println("*** SystemTray size corrected to 48 pixel");
                    iconSize = 48;
                }
            }

            Image trayIconImageTmp;
            if      (iconSize >= 256) trayIconImageTmp = icon256;
            else if (iconSize >= 128) trayIconImageTmp = icon128;
            else if (iconSize >=  64) trayIconImageTmp = icon64;
            else if (iconSize >=  48) trayIconImageTmp = icon48;
            else if (iconSize >=  32) trayIconImageTmp = icon32;
            else if (iconSize >=  24) trayIconImageTmp = icon24;
            else if (iconSize >=  20) trayIconImageTmp = icon20;
            else                      trayIconImageTmp = icon16;

            trayIconImage = trayIconImageTmp;
            trayIcon = new TrayIcon(trayIconImage, null/*"Hello Mpjoe"*/, null /*trayPopup*/);
            try {
                tray.add(trayIcon);
            } catch (AWTException e) {
                e.printStackTrace();
            }


            // TrayIcon und JPopupMenu vertragen sich leider nicht richtig
            // TrayIcon will lieber ein PopupMenu (also kein Swing)
            // Folgender kleiner Testcode verhält sich wie folgt:
            // - Kubuntu: Menü wird mit ButtonDown der rechten Maustaste angezeigt
            //   - aber unglücklich positioniert - der untere Teil des Menüs verschwindet unter der Bildschirmkante, was wohl an meinem nicht rechteckigen Desktop liegt
            // - Osx: Menü wird mit ButtonDown der linken Maustaste angezeigt
            // - WinXp: Menü wird mit ButtonUp der rechten Maustaste angezeigt
            /*PopupMenu popupMenu = new PopupMenu("Blabla");
            popupMenu.add(new MenuItem("Item 1"));
            popupMenu.add(new MenuItem("Item 2"));
            trayIcon.setPopupMenu(popupMenu);*/

            // Als Workaround wird häufig ein MouseListener empfohlen
            // - http://bugs.java.com/bugdatabase/view_bug.do?bug_id=6285881
            // - http://stackoverflow.com/questions/12667526/adding-jpopupmenu-to-the-trayicon
            // - https://weblogs.java.net/blog/ixmal/archive/2006/05/using_jpopupmen.html
            // Das Popupmenü wird dann als Swing angezeigt, also mit dem entsprechenden Farbschema etc.
            // - die Positionierung ist bei meinem nicht rechteckigen Desktop besser als bei Popup
            // Nachteile:
            // - wenn man das Popup aufklapt und nicht benutzt (also danach wieder auf das App Fenster klickt), dann wird es nicht geschlossen sondern bleibt geöffnet stehen
            //   - Esc macht es auch nicht zu
            //   - erst ein Klick mit der anderen Maustaste auf das Trayicon schliesst das Menü
            // - Bei Osx kommt das Popup nun auch mit ButtonDown der rechten Maustaste (war bei "PopupMenu" die linke Maustaste)
            trayPopup = new JPopupMenu("Mpjoe");
            //trayPopup.add(new JMenuItem(quitAction));
            //trayPopup.add(new JMenuItem(chooseFileAndPlayAction));
            // via addMouseListener sehe ich ein paar Mausaktionen auf dem TrayIcon allerdings kein Entered, Exited, Moved, Dragged, WheelMoved
            trayIcon.addMouseListener(new MouseAdapter() {
                private void checkPopup(MouseEvent evt) {
                    if (evt.isPopupTrigger()) {
                        //System.out.println("Trayicon popup triggered");
                        trayPopup.setLocation(evt.getX(), evt.getY());
                        trayPopup.setInvoker(trayPopup);
                        trayPopup.setVisible(true);
                    }
                }
                @Override public void mouseEntered(MouseEvent evt) {
                    System.out.println("Trayicon mouseEntered at " + evt.getX() + ", " + evt.getY());
                }
                @Override public void mouseExited(MouseEvent evt) {
                    System.out.println("Trayicon mouseExited at " + evt.getX() + ", " + evt.getY());
                }
                @Override public void mouseMoved(MouseEvent evt) {
                    System.out.println("Trayicon mouseMoved at " + evt.getX() + ", " + evt.getY());
                }
                @Override public void mouseDragged(MouseEvent evt) {
                    System.out.println("Trayicon mouseDragged at " + evt.getX() + ", " + evt.getY());
                }
                @Override public void mousePressed(MouseEvent evt) {
                    System.out.println("Trayicon mousePressed at " + evt.getX() + ", " + evt.getY());
                    checkPopup(evt);
                }
                @Override public void mouseReleased(MouseEvent evt) {
                    System.out.println("Trayicon mouseReleased at " + evt.getX() + ", " + evt.getY());
                    checkPopup(evt);
                }
                @Override public void mouseClicked(MouseEvent evt) {
                    System.out.println("Trayicon mouseClicked at " + evt.getX() + ", " + evt.getY());
                }
                @Override public void mouseWheelMoved(MouseWheelEvent evt) {
                    System.out.println("Trayicon mouseWheelMoved at " + evt.getX() + ", " + evt.getY() + ", " + evt.getWheelRotation());
                }
            });

            // Trayicon Action Event kommt bei Kubuntu und Windows auf Doppelklick (linke Maustaste) und bei Osx mit ButtonDown der rechten Maustaste
            trayIcon.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent evt) {
                    System.out.println("Trayicon ActionListener");
                }});


            // Rotierendes Icon
            // - bei Osx & WinXP sieht's ok aus
            // - TODO: bei Kubuntu flackert das noch recht stark
            Timer trayIconRotater = new Timer(100, new ActionListener() {
                final int deltaAngle = 5;
                int angle = 0;
                int angle0 = 0;
                @Override public void actionPerformed(ActionEvent evt) {
                    if (iconAnimation) {
                        angle += deltaAngle;
                        if (angle >= 360) angle -= 360;
                    } else {
                        angle = 0;
                    }

                    if (angle != angle0) {
                        final int size = trayIconImage.getWidth(null);
                        BufferedImage rotatedImage = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2d = rotatedImage.createGraphics();
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                        g2d.translate(size / 2, size / 2);
                        g2d.rotate((double)angle / 180f * Math.PI);
                        g2d.translate(-size / 2, -size / 2);
                        g2d.drawImage(trayIconImage, 0, 0, null);

                        //g2d.setPaint(Color.getHSBColor(0.5f, 1, 1));
                        //g2d.setStroke(new BasicStroke(size / 8));
                        //g2d.drawLine(0, size / 2, size, size / 2);
                        //g2d.drawLine(size / 2, 0, size / 2, size);

                        g2d.dispose();
                        trayIcon.setImage(rotatedImage);

                        angle0 = angle;
                    }
                }
            });
            trayIconRotater.start();

        } else {
            System.out.println("*** Sorry, no SystemTray");
            trayIcon = null;
            trayIconImage = null;
            trayPopup = null;
        }
    }

    public boolean isSupported() {
        return trayIcon != null;
    }

    public TrayIcon getIcon() {
        return trayIcon;
    }

    public JPopupMenu getPopup() {
        return trayPopup;
    }

    public void setIconAnimation(boolean b) {
        iconAnimation = b;
    }
}
