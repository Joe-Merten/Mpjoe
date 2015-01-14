package de.jme.mpjoe.swing.playlist;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import de.jme.mpj.MpjPlaylist;
import de.jme.mpj.MpjPlaylistEntry;
import de.jme.mpj.MpjTrack;
import de.jme.toolbox.JTableColumnFitAdapter;
import de.jme.toolbox.SystemInfo;
import de.jme.toolbox.SystemInfo.MachineType;

public class PlaylistPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    MpjPlaylist   playlist;
    JTable        table;
    JScrollPane   scrollPane;
    TableModel    model;
    TableRenderer renderer;

    // Event bei Enter oder Doppelklick
    public interface AcceptEventListner {
        void selectionAccepted(PlaylistPanel playlistPanel);
    }
    private List<AcceptEventListner> listeners = new ArrayList<AcceptEventListner>();

    private static class ColumnSpec {
        public String    name;
        public Class<?>  classs;
        public int       alignment;
        public int       initWidth;
        public ColumnSpec(String name, Class<?> classs, int alignment, int initWidth) {
            this.name      = name;
            this.classs    = classs;
            this.alignment = alignment;
            this.initWidth = initWidth;
        }
    }
    private final static ColumnSpec[] columnSpecs = {
        new ColumnSpec( "State", String.class , JLabel.CENTER,  40 ),
        new ColumnSpec( "Votes", Integer.class, JLabel.CENTER,  40 ),
        new ColumnSpec( "Name" , String.class , JLabel.LEFT  , 300 ),
        new ColumnSpec( "File" , String.class , JLabel.LEFT  , 400 ),
        new ColumnSpec( "Uri"  , String.class , JLabel.LEFT  , 400 ),
    };

    public PlaylistPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.GREEN);
        setPreferredSize(new Dimension(800, 400));
        setMinimumSize(new Dimension(0, 0));

        JLabel lblHeading = new JLabel("Playlist");
        lblHeading.setForeground(Color.BLACK);
        lblHeading.setBackground(Color.CYAN);
        lblHeading.setOpaque(true);
        lblHeading.setFont(new Font("Dialog", Font.BOLD, 24));
        lblHeading.setHorizontalAlignment(SwingConstants.CENTER);
        add(lblHeading, BorderLayout.NORTH);

        playlist = new MpjPlaylist();
        addSomeSampleTracks();

        // Table
        model = new TableModel(playlist);
        table = new JTable(model);
        // setPreferredWidth() wirkt nur mit AUTO_RESIZE_OFF, sonst müsste ich hier setMaxWidth() verwenden
        int columnCount = model.getColumnCount();
        TableRenderer tr = new TableRenderer();
        for (int i = 0; i < columnCount; i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            if (columnSpecs[i].initWidth >= 0)
                column.setPreferredWidth(columnSpecs[i].initWidth);
            System.out.println(column.getMinWidth());
            column.setMinWidth(0); // Damit erlaube ich, dass Spalten z.B. per Maus auf Breite 0 reduziert (also "ausgeblendet") werden können. Per Default steht die Breite offenbar auf 15.
            column.setCellRenderer(tr);
        }
        JTableHeader header = table.getTableHeader();
        // header.setReorderingAllowed(false); ... falls ich das mal unterbinden möchte
        header.addMouseListener(new JTableColumnFitAdapter());

        scrollPane = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); // erforderlich, damit wir einen horizontalen Scrollbar bekommen
        add(scrollPane, BorderLayout.CENTER);

        table.addMouseListener(new MouseListener() {
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
        table.addKeyListener(new KeyListener() {
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

    // Debug-/Testfunktionen
    void addSampleTrack(String name) {
        try {
            if (name.startsWith("/D/")) {
                if (SystemInfo.getMachineType() == MachineType.PcWindows) name = name.replace("/D/", "D:/");
                if (SystemInfo.getMachineType() == MachineType.PcOsx) name = name.replace("/D/MP3/", "/Users/joe.merten/Development/");
            }

            URI uri;
            if (name.startsWith("http://") || name.startsWith("https://")) {
                uri = URI.create(name);
            } else {
                // URI.create(nam) geht nicht, weil da sind dann keine Leerzeichen in Dateinamen erlaubt
                File f = new File(name);
                uri = f.toURI();
            }

            playlist.add(new MpjPlaylistEntry(new MpjTrack(uri)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void addSomeSampleTracks() {
        addSampleTrack("/D/MP3/OGG-WMA-RM-Test/Testfiles/America - The Last Unicorn.mp3");
        playlist.get(playlist.size()-1).setVotes(1);
        addSampleTrack("/D/MP3/OGG-WMA-RM-Test/Testfiles/Safri Dou - Played-A-Live.avi");
        playlist.get(playlist.size()-1).setVotes(2);
        addSampleTrack("/D/MP3/Carsten/The Boss Hoss/Stallion Battalion/12 High.mp3");
        playlist.get(playlist.size()-1).setVotes(3);
        addSampleTrack("/D/MP3/Elisa iPod/Basta - Gimme Hope Joachim - der Jogi Löw a Cappella WM Song 2010.wav");
        addSampleTrack("/D/MP3/Nora de Mar - For your Beauty and Soul/Ogg Vorbis/02 - Island of Hope.ogg");
        addSampleTrack("/D/MP3/Nora de Mar - For your Beauty and Soul/Flac/02 - Island of Hope.flac");

        addSampleTrack("https://www.youtube.com/watch?v=0w1mP3oFXRU");
        addSampleTrack("http://www.youtube.com/watch?v=0w1mP3oFXRU");
        addSampleTrack("http://youtu.be/0w1mP3oFXRU");
        addSampleTrack("https://www.youtube.com/watch?v=5oGXmvy0--w");
        addSampleTrack("https://www.youtube.com/watch?v=oJh-jusiDvU");

        addSampleTrack("http://www.myvideo.de/watch/7880291/Joe_AFF_Level_V_VII");
    }

    public void addAcceptEventListner(AcceptEventListner listener) {
        listeners.add(listener);
    }

    public void removeAcceptEventListner(AcceptEventListner listener) {
        listeners.remove(listener);
    }

    private void sendAcceptEvent() {
        if (table.getSelectedRowCount() < 0) return;
        for (AcceptEventListner l : listeners) {
            try {
                l.selectionAccepted(this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    static class TableModel extends AbstractTableModel {
        private static final long serialVersionUID = -6517236943261069942L;

        private MpjPlaylist playlist;

        TableModel(MpjPlaylist playlist) {
            this.playlist = playlist;
        }

        void setPlaylist(MpjPlaylist playlist) {
            this.playlist = playlist;
            fireTableDataChanged();
        }

        @Override public int getRowCount() {
            return (playlist != null) ? playlist.size() : 0;
        }

        @Override public int getColumnCount() {
            return columnSpecs.length;
        }

        @Override public String getColumnName(int columnIndex) {
            return columnSpecs[columnIndex].name;
        }

        @Override public Object getValueAt(int rowIndex, int columnIndex) {
            MpjPlaylistEntry ple = playlist.get(rowIndex);
            MpjTrack track = ple.getTrack();
            URI uri = track.getUri();
            switch (columnIndex) {
                case 0: return ple.getState();
                case 1: return ple.getVotes();
                case 2: return track.getName();
                case 3: return uri.getSchemeSpecificPart(); // TODO: Mal gucken, was das hier liefert
                case 4: return uri.toString();
                default: return null;
            }
        }

        @Override public boolean isCellEditable(int rowIndex, int columnIndex) {
            // Editieren gibt's hier vorerst noch nicht
            return false;
        }

        @Override public Class<?> getColumnClass(int columnIndex) {
            return columnSpecs[columnIndex].classs;
        }

        @Override public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            // Editieren gibt's hier vorerst noch nicht
        }

        // TODO ...
        //public void insertRow(int index, CfgDictionaryEntry element) {
        //    content.add(index, element);
        //    fireTableDataChanged();         // TODO: Benachrichtigung besser eingrenzen (fireTableRowsInserted()?)
        //}
        //
        //public void addRow(CfgDictionaryEntry element) {
        //    content.add(element);
        //    fireTableDataChanged();         // TODO: Benachrichtigung besser eingrenzen
        //}
        //
        //// TODO: Verschieben mehrerer Zeilen erlauben, dafür ggf. Code von DefaultTableModel.moveRow(int start, int end, int to) übernehmen
        //public void moveRow(int index, boolean up) {
        //    if (index < 0 || index >= content.size())
        //        return;     // Blödsinniger Index -> ignorieren
        //    CfgDictionaryEntry moved = content.remove(index);
        //    int newIdx = index;
        //    if (up) {
        //        if (newIdx > 0) {
        //            newIdx --;
        //        }
        //    } else {
        //        if (newIdx < content.size()) {
        //            newIdx ++;
        //        }
        //    }
        //    if (newIdx < content.size())
        //        content.add(newIdx, moved);
        //    else
        //        content.add(moved);
        //    fireTableDataChanged();         // TODO: Benachrichtigung besser eingrenzen
        //}
        //
        //public void removeRow(int index) {
        //    content.remove(index);
        //    fireTableDataChanged();         // TODO: Benachrichtigung besser eingrenzen
        //}

    } // class TableModel

    static class TableRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 5868109978606386673L;

        @Override public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            TableModel tm = (TableModel) table.getModel();
            MpjPlaylistEntry ple = tm.playlist.get(row);
            MpjTrack track = ple.getTrack();
            URI uri = track.getUri();
            String scheme = uri.getScheme();
            if (!isSelected) {
                if (scheme.equals("http") || scheme.equals("https")) {
                    setForeground(Color.darkGray); // Nur Spielerei
                } else {
                    setForeground(Color.black);
                }
            }
            // Wandlung von column nach columnIndex erforderlich, falls die Spalten umsortiert wurden (z.B. interaktiv via header.setReorderingAllowed(true))
            int columnIndex = table.convertColumnIndexToModel(column);
            setHorizontalAlignment(columnSpecs[columnIndex].alignment);
            if (columnIndex == 0) {
                if (ple.getState() == MpjPlaylistEntry.State.NONE) setText("");
            } else if (columnIndex == 1) {
                if (ple.getVotes() == 0) setText("");
            }

            return this;
        }
    } // class TableRenderer

    public void addTrack(MpjTrack track) {
        // TODO: Change-Listener an der Playlist?
        playlist.add(new MpjPlaylistEntry(track));
        model.fireTableDataChanged();
    }

    public MpjPlaylistEntry[] getSelectedEntries() {
        final int[] rows = table.getSelectedRows();
        int count = rows.length;
        MpjPlaylistEntry[] entries = new MpjPlaylistEntry[count];
        int num = 0;
        for (int row : rows)
            entries[num] = playlist.get(row);
        return entries;
    }
}
