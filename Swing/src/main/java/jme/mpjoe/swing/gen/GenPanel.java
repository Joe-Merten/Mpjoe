package jme.mpjoe.swing.gen;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * UI-Komponente für den Ews Client bzg. der allgemeinen Funktionalitäten
 * @author Joe Merten
 */
public class GenPanel extends JPanel {
    private static final long serialVersionUID = -8872093423171922890L;

    public GenPanel() {
        setLayout(new BorderLayout(0, 0));
        JLabel l = new JLabel();
        l.setText("Ews General  … noch nichts implementiert …");
        add(l, BorderLayout.NORTH);
    }

}
