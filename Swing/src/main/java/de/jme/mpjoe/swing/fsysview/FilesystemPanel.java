// Achtung, ist nur eine ganz simple Testimplementation.
// Nicht für Produktivcoode geeignet
// - Kein Refresh zur Laufzeit
// - Verzeichnis wird komplett (inkl. aller Unterverzeichnisse) eingelesen
// - ...

package de.jme.mpjoe.swing.fsysview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class FilesystemPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    // Event bei Enter oder Doppelklick
    public interface AcceptEventListner {
        void selectionAccepted(FilesystemPanel filesystemPanel);
    }
    private List<AcceptEventListner> listeners = new ArrayList<AcceptEventListner>();

    JTree tree;
    JScrollPane scrollpane;

    public FilesystemPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.GREEN);
        setPreferredSize(new Dimension(220, 400));
        setMinimumSize(new Dimension(0, 0));

        JLabel lblHeading = new JLabel("Filesystem");
        lblHeading.setForeground(Color.DARK_GRAY);
        lblHeading.setBackground(Color.CYAN);
        lblHeading.setOpaque(true);
        lblHeading.setFont(new Font("Dialog", Font.BOLD, 24));
        lblHeading.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblHeading, BorderLayout.NORTH);

        // Lastly, put the JTree into a JScrollPane.
        scrollpane = new JScrollPane();
        add(BorderLayout.CENTER, scrollpane);
    }

    public void addAcceptEventListner(AcceptEventListner listener) {
        listeners.add(listener);
    }

    public void removeAcceptEventListner(AcceptEventListner listener) {
        listeners.remove(listener);
    }

    private void sendAcceptEvent() {
        if (tree.getSelectionCount() < 0) return;
        final File[] files = getSelectedFiles();
        // Wenn nur Verzeichnisse selektiert sind, dann erst mal kein Event
        // Das ist nämlich genau der Fall, wenn z.B. per Doppelklick ein Node Expanded oder Collapsed wird
        boolean regularFileFound = false;
        for (File f : files) {
            if (f.isFile()) {
                regularFileFound = true;
                break;
            }
        }
        if (!regularFileFound) return;

        for (AcceptEventListner l : listeners) {
            try {
                l.selectionAccepted(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setRootDirectory(String dir) {
        JTree oldTree = tree;
        JTree newTree = null;
        if (dir != null && !dir.isEmpty()) newTree = new JTree(addNodes(null, new File(dir), dir));
        if (oldTree != null) scrollpane.getViewport().remove(oldTree);
        tree = newTree;
        if (tree != null) {
            scrollpane.getViewport().add(tree);
            // Add a listener
            /*tree.addTreeSelectionListener(new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent e) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
                    System.out.println("You selected " + node + " (" + e.toString() + ")");
                }
            });*/
            tree.addMouseListener(new MouseListener() {
                @Override public void mouseReleased(MouseEvent evt) {}
                @Override public void mousePressed(MouseEvent evt) {}
                @Override public void mouseExited(MouseEvent evt) {}
                @Override public void mouseEntered(MouseEvent evt) {}
                @Override public void mouseClicked(MouseEvent evt) {
                    //System.out.println("Mouse Event " + evt + " consumed=" + evt.isConsumed());
                    if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2) {
                        sendAcceptEvent();
                        evt.consume();
                    }
                }
            });
            tree.addKeyListener(new KeyListener() {
                @Override public void keyTyped(KeyEvent evt) {}
                @Override public void keyReleased(KeyEvent evt) {}
                @Override public void keyPressed(KeyEvent evt) {
                    // keyTyped geht nicht für VK_ENTER, deshalb keyPressed
                    //System.out.println("KeyPressed " + evt);
                    if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                        sendAcceptEvent();
                        evt.consume();
                    }
                }
            });
        }
    }

    // Rekursives Füllen des Verzeichnisbaums
    // Achtung, hier wird das gesamte Verzeichnis rekursiv eingelesen, bei "/" kann das also sehr lange dauern!
    // TODO: Files.newDirectoryStream verwenden anstelle von dir.list und Unterverzeichnisse erst beim Aufklappen lesen
    TreeNode addNodes(DefaultMutableTreeNode curTop, File dir, String nodeName) {
        String curPath = dir.getPath();
        DefaultMutableTreeNode curDir = new DefaultMutableTreeNode(nodeName);
        if (curTop != null) // ist null bei unserem Root, also erster Call vor Rekursion
            curTop.add(curDir);
        Vector<String> ol = new Vector<String>();
        final String[] tmp = dir.list();  // hier wird das Verzeichnis eingelesen
        for (String element : tmp)
            ol.addElement(element);
        Collections.sort(ol, String.CASE_INSENSITIVE_ORDER);
        Vector<String> files = new Vector<String>();

        // Verzeichnisse zufügen
        for (String element : ol) {
            String newPath;
            if (curPath.equals(".")) newPath = element;
            else newPath = curPath + File.separator + element;
            File f = new File(newPath);
            if (f.isDirectory())
                // Nur beim Wurzelknoten volle Pfadangabe. Alle Unververzeichnisse nur mit ihrem eigenen Namen eintragen
                addNodes(curDir, f, element);
            else
                files.addElement(element);
        }

        // Und jetzt die Dateien
        for (String fnam : files)
            curDir.add(new DefaultMutableTreeNode(fnam));
        return curDir;
    }

    public File[] getSelectedFiles() {
        //int count = tree.getSelectionCount();
        final TreePath[] treePaths = tree.getSelectionPaths();
        int count = treePaths.length;
        File[] files = new File[count];
        int num = 0;
        for (TreePath tp : treePaths) {
            //System.out.println(": " + tp);
            int n = tp.getPathCount();
            String fnam = "";
            for (int i = 0; i < n; i++)
                fnam = fnam + File.separator + tp.getPathComponent(i);
            files[num] = new File(fnam);
            num++;
        }
        return files;
    }

}
