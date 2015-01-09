package de.jme.mpjoe.swing.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

/**
 * UI-Komponente für die Statuszeile des Mpjoe Java Swing Client
 * @author Joe Merten
 */
public class Statusbar extends JPanel {
    private static final long serialVersionUID = 6606317104430515856L;
    private JLabel lblStatustextA;
    private JLabel lblStatustextB;
    private JLabel lblStatustextC;

    public Statusbar() {
        //setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        setLayout(new BorderLayout(0, 0));

        lblStatustextA = new JLabel();
        add(lblStatustextA, BorderLayout.WEST);
        lblStatustextA.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        lblStatustextA.setPreferredSize(new Dimension(200, 0));

        lblStatustextB = new JLabel();
        add(lblStatustextB, BorderLayout.CENTER);
        lblStatustextB.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        //lblStatustextB.setPreferredSize(paneSize);

        lblStatustextC = new JLabel();
        add(lblStatustextC, BorderLayout.EAST);
        lblStatustextC.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
        //lblStatustextC.setPreferredSize(new Dimension(300, 0));
    }

    public void setStatusTextA(String text) {
        lblStatustextA.setText(text);
        //paintAll(getGraphics());
    }
    public void setStatusTextB(String text) {
        lblStatustextB.setText(text);
        //paintAll(getGraphics());
    }
    public void setStatusTextC(String text) {
        if (text != lblStatustextC.getText()) {
            lblStatustextC.setText(text);
            paintAll(getGraphics()); // Zum sofortigen Update der Statusbar - ok, vermutlich ist das unsauber...
        }
    }
}
