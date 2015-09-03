package de.jme.mpjoe.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Hmm, offenbar soll man sich nicht von AbstractButton ableiten sondern von JButton
//   http://stackoverflow.com/questions/5751311/creating-a-custom-button-in-java-with-jbutton
//
// JToggleButton
//   das selectedIcon zeigt an, wenn die Action selected ist
//
// JButton
//   das selectedIcon zeigt NICHT an, wenn die Action selected ist
//   aber mit JButton.seSelected(true) wird das selectedIcon angezeigt
//   ist also offenbar bzgl. selected nicht mit der Action gekoppelt
//
// AbstractButton
//   hat scheinbar sehr viel an Bord, was ich nicht brauche
//
// JComponent
//   wird an diversen Stellen als Basisklasse für eigene Buttons vorgeschlagen
//   - http://stackoverflow.com/questions/2158/creating-a-custom-button-in-java      (hier gibt's aber auch ein Beispiel mit Ableitung von JButton)
//   - http://da2i.univ-lille1.fr/doc/tutorial-java/uiswing/painting/practice.html

public class MpjButton extends JComponent implements MouseListener {
    private static final long serialVersionUID = 1L;
    static final Logger logger = LoggerFactory.getLogger(MpjButton.class);

    private Dimension      size;
    private AbstractAction action;
    private boolean        mouseInside;
    private boolean        mouseDown;
    private static List<MpjButton> buttonList = new ArrayList<MpjButton>(100);

    public MpjButton(AbstractAction action) {
        this(action, new Dimension(24,24));
    }

    public MpjButton(AbstractAction action, Dimension size) {
        this.action = action;
        this.size = size;
        setBorder(BorderFactory.createEmptyBorder());
        setFocusable(false);
        enableInputMethods(true);
        addMouseListener(this);

        if (action != null) {
            action.addPropertyChangeListener(new PropertyChangeListener() {
                @Override public void propertyChange(PropertyChangeEvent evt) {
                    // TODO: evtl. optimieren, also repaint nur bei für mich relevanten Änderungen
                    repaint();
                    startBlinkTimer();
                }
            });
        }

        synchronized (buttonList) {
            buttonList.add(this);
            // TOOD: mpjButton mit AutoClose und dann wieder von der Listen entfernen?
        }
    }

    @Override public Dimension getPreferredSize() {
        return size;
    }

    @Override public Dimension getMinimumSize() {
        return size;
    }

    @Override public Dimension getMaximumSize() {
        return size;
    }

    @Override public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D antiAlias = (Graphics2D)g;
        antiAlias.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int xOfs = 0;
        int yOfs = 0;
        if (mouseInside && mouseDown) {
            xOfs += 1;
            yOfs += 1;
        }

        Icon defaultIcon = null;
        Icon prefIcon = null;

        if (action != null && action instanceof MpjAction) {
            MpjAction mpjAction = (MpjAction)action;
            defaultIcon = mpjAction.getIcon();
            if (mpjAction.isSelected())
                prefIcon = mpjAction.getSelectedIcon();
            if (mpjAction.isThirdstate())
                prefIcon = mpjAction.getThirdstateIcon();
            if (mpjAction.isBlinking() && blinkOn) prefIcon = null;
            if (!mpjAction.isEnabled()) {
                prefIcon = mpjAction.getDisabledIcon();
                if (prefIcon != null) {
                    // TODO: Disabled Icon generieren?
                }
            }
        } else if (action != null) {
            defaultIcon = (Icon)action.getValue(AbstractAction.SMALL_ICON);
            if (! (defaultIcon instanceof Icon)) defaultIcon = null;
        }

        Icon icon = prefIcon;
        if (icon == null) icon = defaultIcon;

        if (icon != null) {
            int x = (getWidth() - icon.getIconWidth()) / 2;
            int y = (getHeight() - icon.getIconHeight()) / 2;
            icon.paintIcon(this, g, xOfs + x, yOfs + y);
        }
        else {
            // Rückfallebene, falls keine Action gesetzt ist oder die Action kein Icon hat
            g.setColor(getBackground());  // TODO: Farbe aus Look & Feel ermitteln
            g.drawRoundRect(xOfs +1, yOfs +1, getWidth() -2, getHeight() -2, 4, 4);
            g.setColor(getForeground());
            g.drawRoundRect(xOfs , yOfs , getWidth() -2, getHeight() -2, 4, 4);
        }
    }

    @Override public void mouseEntered(MouseEvent evt) {
        //logger.trace("mouseEntered " + evt);
        mouseInside = true;
        repaint();
    }

    @Override public void mouseExited(MouseEvent evt) {
        //logger.trace("mouseExited " + evt);
        mouseInside = false;
        repaint();
    }

    @Override public void mousePressed(MouseEvent evt) {
        //logger.trace("mousePressed " + evt);
        mouseDown = true;
        repaint();
        if (action != null) {
            ActionEvent ae = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, new String(), evt.getWhen(), evt.getModifiers());
            action.actionPerformed(ae);
        }
    }

    @Override public void mouseReleased(MouseEvent evt) {
        //logger.trace("mouseReleased " + evt);
        mouseDown = false;
        repaint();
    }

    @Override public void mouseClicked(MouseEvent evt) {
        //logger.trace("mouseClicked " + evt);
    }

    /**
     * Timer to realize the blinking buttons
     * All blinking buttons are in sync
     */
    private static javax.swing.Timer blinkTimer;
    private static ButtonBlinker buttonBlinker;
    private static boolean blinkOn = false;
    private static void startBlinkTimer() {
        synchronized (buttonList) {
            if (blinkTimer == null) {
                buttonBlinker = new ButtonBlinker();
                blinkTimer = new javax.swing.Timer(500, buttonBlinker);
            }
            blinkTimer.start();
        }
    }
    private static class ButtonBlinker implements ActionListener {
        @Override public void actionPerformed(ActionEvent evt) {
            synchronized (buttonList) {
                if (buttonList != null) {
                    blinkOn = !blinkOn;
                    boolean isAnyBlinking = false;
                    for (MpjButton btn : buttonList) {
                        if (btn.action != null && btn.action instanceof MpjAction && ((MpjAction)btn.action).isBlinking()) {
                            btn.repaint();
                            isAnyBlinking = true;
                        }
                    }
                    if (!isAnyBlinking)
                        // Aktuell blinkt nichts, also kann ich den Timer anhalten
                        blinkTimer.stop();
                }
            }
        }
    };

}
