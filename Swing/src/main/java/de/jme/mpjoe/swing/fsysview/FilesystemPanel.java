package de.jme.mpjoe.swing.fsysview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

public class FilesystemPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    JFileChooser  fileChooser;
    MouseListener mouseListener;
    KeyListener   keyListener;

    // Event bei Enter oder Doppelklick
    public interface AcceptEventListner {
        void selectionAccepted(FilesystemPanel filesystemPanel);
    }
    private List<AcceptEventListner> listeners = new ArrayList<AcceptEventListner>();

    public FilesystemPanel() {
        setLayout(new BorderLayout());
        //setBackground(Color.GREEN);
        setPreferredSize(new Dimension(220, 400));
        setMinimumSize(new Dimension(0, 0));

        JLabel lblHeading = new JLabel("Filesystem");
        lblHeading.setForeground(Color.DARK_GRAY);
        lblHeading.setBackground(new Color(0,128,128));
        lblHeading.setOpaque(true);
        lblHeading.setFont(new Font("Dialog", Font.BOLD, 24));
        lblHeading.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblHeading, BorderLayout.NORTH);

        // Filechooser soll kein Umbenennen von Dateien zulassen.
        // -> http://stackoverflow.com/a/8188635/2880699
        // -> https://community.oracle.com/message/9935325?#9933325
        UIManager.put("FileChooser.readOnly", Boolean.TRUE);

        fileChooser = new JFileChooser();
        add(BorderLayout.CENTER, fileChooser);

        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        //fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setApproveButtonText("Add");
        fileChooser.setControlButtonsAreShown(false); // ausblenden der Buttons "Öffnen" / "Abbruch", sofern Look & Feel das gestattet
        fileChooser.setDragEnabled(true);
        fileChooser.setMultiSelectionEnabled(true);
        //fileChooser.setSelectedFiles(selectedFiles);

        addListeners();
    }


    // Abfangen von Enter und Doppelklick ist beim FileChooser etwas tricky
    // Hier evtl. eine Alternative Lösung:
    //   http://docs.oracle.com/javase/tutorial/uiswing/misc/keybinding.html
    private void addListeners() {
        // addActionListener ruft mich leider nicht bei Enter-Key
        // fileChooser.addActionListener(new ActionListener() {
        //     @Override public void actionPerformed(ActionEvent e) {
        //         System.out.println("Action = " + e.getActionCommand());
        //         if (e.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)) {
        //             System.out.println("File selected: " + fileChooser.getSelectedFile());
        //         }
        //     }
        // });

        if (mouseListener == null) {
            // fileChooser.addAncestorListener(new AncestorListener() {
            //     @Override public void ancestorRemoved(AncestorEvent evt) { System.out.println("ancestorRemoved" ); }
            //     @Override public void ancestorMoved  (AncestorEvent evt) { System.out.println("ancestorMoved"  ); }
            //     @Override public void ancestorAdded  (AncestorEvent evt) { System.out.println("ancestorAdded"  ); }
            // });
            //
            // fileChooser.addComponentListener(new ComponentListener() {
            //     @Override public void componentHidden (ComponentEvent evt) { System.out.println("componentHidde"  ); }
            //     @Override public void componentMoved  (ComponentEvent evt) { System.out.println("componentMoved"  ); }
            //     @Override public void componentResized(ComponentEvent evt) { System.out.println("componentResized"); }
            //     @Override public void componentShown  (ComponentEvent evt) { System.out.println("componentShown"  ); }
            // });
            //
            // fileChooser.addContainerListener(new ContainerListener() {
            //     @Override public void componentAdded  (ContainerEvent evt) { System.out.println("componentAdded"  ); }
            //     @Override public void componentRemoved(ContainerEvent evt) { System.out.println("componentRemoved"); }
            // });
            //
            // fileChooser.addPropertyChangeListener(new PropertyChangeListener() {
            //     @Override public void propertyChange(PropertyChangeEvent evt) {
            //         System.out.println("propertyChange " + evt.getPropertyName());
            //         if (evt.getPropertyName().equals("SelectedFilesChangedProperty")) {
            //             addListeners();
            //         }
            // }});

            // Wenn ich im FileChooser zwischen den Ansichten wechsle, dann gehen offenbar meine registierten Maus- und KeyListener verloren
            // deshalb muss ich sie zuweilen neu registrieren. Dies tue ich bei jeder Änderung der "SelectedFiles".
            fileChooser.addPropertyChangeListener("SelectedFilesChangedProperty", new PropertyChangeListener() {
                @Override public void propertyChange(PropertyChangeEvent evt) {
                    addListeners();
            }});
        }

        if (mouseListener == null) {
            mouseListener = new MouseListener() {
                @Override public void mouseReleased(MouseEvent evt) {}
                @Override public void mousePressed(MouseEvent evt) {}
                @Override public void mouseExited(MouseEvent evt) {}
                @Override public void mouseEntered(MouseEvent evt) {}
                @Override public void mouseClicked(MouseEvent evt) {
                    //System.out.println("Mouse Event " + evt + " consumed=" + evt.isConsumed());
                    if (evt.getButton() == MouseEvent.BUTTON1 && evt.getClickCount() == 2)
                        if (sendAcceptEvent())
                            evt.consume();
                }
            };
        }

        if (keyListener == null) {
            keyListener = new KeyListener() {
                @Override public void keyTyped(KeyEvent evt) {}
                @Override public void keyReleased(KeyEvent evt) {}
                @Override public void keyPressed(KeyEvent evt) {
                    // keyTyped geht nicht für VK_ENTER, deshalb keyPressed
                    //System.out.println("KeyPressed " + evt);
                    if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                        final File[] files = fileChooser.getSelectedFiles();
                        if (files.length == 1 && files[0].isDirectory()) {
                            // Verzeichniswechsel via Enter ermöglichen
                            // geht aber nur, wenn JFileChooser.FILES_AND_DIRECTORIES gesetzt ist
                            fileChooser.setCurrentDirectory(files[0]);
                        } else {
                            if (sendAcceptEvent())
                                evt.consume();
                        }
                    }
                }
            };
        }

        // Unter Ubuntu und Windows finde ich ein sun.swing.FilePane
        JList<?> fileList = findFileList(fileChooser, "");
        if (fileList != null) {
            if (!hasMouseListener(fileList, mouseListener)) fileList.addMouseListener(mouseListener);
            if (!hasKeyListener(fileList, keyListener)) fileList.addKeyListener(keyListener);
        }

        // Unter Osx finde ich ein JTableExtension / com.apple.laf.AquaFileChooserUI$TableExtension
        JTable fileTable = findFileTable(fileChooser, "");
        if (fileTable != null) {
            if (!hasMouseListener(fileTable, mouseListener)) fileTable.addMouseListener(mouseListener);
            if (!hasKeyListener(fileTable, keyListener)) fileTable.addKeyListener(keyListener);

            // Tab soll nicht zur nächsten »Zelle« der Tabelle, sondern zum nächsten Control!
            // -> http://stackoverflow.com/questions/12154734/change-focus-to-next-component-in-jtable-using-tab
            fileTable.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
            fileTable.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);
        }
    }

    private boolean hasMouseListener(Component comp, MouseListener listener) {
        final MouseListener[] listeners = comp.getMouseListeners();
        for (MouseListener l : listeners)
            if (l == listener)
                return true;
        return false;
    }

    private boolean hasKeyListener(Component comp, KeyListener listener) {
        final KeyListener[] listeners = comp.getKeyListeners();
        for (KeyListener l : listeners)
            if (l == listener)
                return true;
        return false;
    }

    private JList<?> findFileList(Component comp, String indent) {
        //System.out.println("Checking   " + indent + comp.getClass().getSimpleName() + "  (" + comp.getClass().getName() + ")" + "  (" + comp.getClass().getCanonicalName() + ")");
        if (comp instanceof JList) {
            //System.out.println("Gotcha!");
            return (JList<?>)comp;
        }
        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                JList<?> ret = findFileList(child, indent + "  ");
                if (ret != null)
                    return ret;
            }
        }
        return null;
    }

    private JTable findFileTable(Component comp, String indent) {
        //System.out.println("Checking   " + indent + comp.getClass().getSimpleName() + "  (" + comp.getClass().getName() + ")" + "  (x=" + comp.getX() + " y=" + comp.getX()+ " size=" + comp.getSize() + ")");
        if (comp instanceof JTable) {
            //System.out.println("Gotcha!");
            return (JTable)comp;
        }
        if (comp instanceof Container) {
            for (Component child : ((Container) comp).getComponents()) {
                JTable ret = findFileTable(child, indent + "  ");
                if (ret != null)
                    return ret;
            }
        }
        return null;
    }

    public void addAcceptEventListner(AcceptEventListner listener) {
        listeners.add(listener);
    }

    public void removeAcceptEventListner(AcceptEventListner listener) {
        listeners.remove(listener);
    }

    private boolean sendAcceptEvent() {
        final File[] files = getSelectedFiles();
        if (files.length <= 0) return false;
        // Wenn nur Verzeichnisse selektiert sind, dann erst mal kein Event
        // Das ist nämlich genau der Fall, wenn z.B. per Doppelklick ein Node Expanded oder Collapsed wird
        boolean regularFileFound = false;
        for (File f : files) {
            if (f.isFile()) {
                regularFileFound = true;
                break;
            }
        }
        if (!regularFileFound) return false;

        for (AcceptEventListner l : listeners) {
            try {
                l.selectionAccepted(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public void setRootDirectory(String dir) {
        fileChooser.setCurrentDirectory(new File(dir));
    }

    public File[] getSelectedFiles() {
        return fileChooser.getSelectedFiles();
    }

}
