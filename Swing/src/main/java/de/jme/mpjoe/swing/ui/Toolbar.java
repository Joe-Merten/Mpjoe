package de.jme.mpjoe.swing.ui;

import java.awt.Font;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;

/**
 * UI-Komponente für die Toolbar des Ews Client
 * @author Joe Merten
 */
public class Toolbar extends JToolBar {
    private static final long serialVersionUID = 8666505291919314337L;

    public Toolbar() {
        setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        setFloatable(false);
        setRollover(true); // Angeblich soll hierdurch die Umrandung der Buttons nur bei MouseOver gezeichnet werden - geht aber offenbar nicht (zumindestens unter Kubuntu 12.04).
    }

    public void add(AbstractAction action) {
        JButton btn = new JButton(action);
        btn.setFont(new Font("Dialog", Font.PLAIN, 12));  // Buttonbeschriftung nicht Fett und auch nicht so gross
        btn.setFocusable(false);                          // Nicht mit der Tabulatortaste auf die Toolbar Buttons
        btn.setBorder(new CompoundBorder());              // Hierdurch entfernen wir die Umrandung von den Buttons
        add(btn);
    }
}
