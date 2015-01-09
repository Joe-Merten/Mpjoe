package de.jme.mpjoe.swing.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import de.jme.toolbox.SystemInfo;

/**
 * UI-Komponente zur Anzeige einer Warnung beim Programmstart
 * @author Joe Merten
 */
public class DauWarning extends JDialog {
    private static final long serialVersionUID = -5606495406800428307L;
    private final JPanel contentPanel = new JPanel();

    public DauWarning() {
        setModal(true);
        setBounds(100, 100, 668, 386);
        setTitle("Ews Client");

        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout(0, 0));

        JLabel lblHeading = new JLabel("!!! Achtung, Achtung !!!");
        lblHeading.setForeground(Color.RED);
        lblHeading.setFont(new Font("Dialog", Font.BOLD, 24));
        lblHeading.setHorizontalAlignment(SwingConstants.CENTER);
        getContentPane().add(lblHeading, BorderLayout.NORTH);

        String msg = "\n";
        msg += "Bei dieser Applikation handelt es sich um ein Entwicklertool.\n";
        msg += "Sie ist somit:\n";
        msg += "- nicht DAU - tauglich (also nicht narrensicher)\n";
        msg += "- nicht ausgereift, da ständig in Entwicklung\n";
        msg += "- nicht intensiv getestet\n";
        msg += "Bei der Verwendung dieser Applikation sollte man also wissen was man tut.\n";

        JTextArea txtBody = new JTextArea(msg);
        txtBody.setFont(new Font("Dialog", Font.BOLD, 16));
        //txtBody.setHorizontalAlignment(SwingConstants.CENTER);
        getContentPane().add(txtBody, BorderLayout.CENTER);


        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);

        JButton okButton = new JButton("OK, das habe ich verstanden");
        buttonPane.add(okButton);
        getRootPane().setDefaultButton(okButton);

        JButton cancelButton = new JButton("Nein, ich weiss nicht was das bedeutet");
        cancelButton.setActionCommand("Cancel");
        buttonPane.add(cancelButton);
    }

    static public boolean isWarningNeeded() {
        boolean ret = true;

        String username = SystemInfo.getUserName().toLowerCase();
        //String cpuname = SystemInfo.getComputerName().toLowerCase();

        if (username.equals("joe"   )) ret = false;
        if (username.equals("britta")) ret = false;

        return ret;
    }
}
