package de.jme.mpjoe.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * @see http://stackoverflow.com/questions/3405799/how-to-rotate-an-image-gradually-in-swing/3420651#3420651
 * @see http://stackoverflow.com/questions/3371227
 * @see http://stackoverflow.com/questions/3405799
 */

@SuppressWarnings("serial")
class RotatePanel extends JPanel implements ActionListener {

    private static final int N = 3;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                JFrame frame = new JFrame();
                frame.setLayout(new GridLayout(N, N, N, N));
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                for (int i = 0; i < N * N; i++)
                    frame.add(new RotatePanel());
                frame.pack();
                frame.setVisible(true);
            }
        });
    }

    private static final int SIZE = 256;
    private static double DELTA_THETA = Math.PI / 90;
    private final Timer timer = new Timer(25, this);
    private Image image = createSampleImage(SIZE);
    private double dt = DELTA_THETA;
    private double theta;

    // Constructor
    public RotatePanel() {
        this.setBackground(Color.lightGray);
        this.setPreferredSize(new Dimension(image.getWidth(null), image.getHeight(null)));
        this.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                image = createSampleImage(SIZE);
                dt = -dt;
            }
        });
        timer.start();
    }

    //
    @Override public void paintComponent(Graphics g) {
        super.paintComponent(g); // zeichnet den grauen Hintergrund des Panel neu
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(this.getWidth() / 2, this.getHeight() / 2);
        g2d.rotate(theta);
        g2d.translate(-image.getWidth(this) / 2, -image.getHeight(this) / 2);
        g2d.drawImage(image, 0, 0, null);
    }

    // Callback für Timer Events
    @Override public void actionPerformed(ActionEvent e) {
        theta += dt;
        repaint();
    }

    @Override public Dimension getPreferredSize() {
        return new Dimension(SIZE, SIZE);
    }

    // Erzeugen eines Beispielimage (farbiges Kreuz)
    public Image createSampleImage(int size) {
        //return new ImageIcon(getClass().getResource("/de/jme/mpj/Icon/Mpjoe-Icon-256.png")).getImage();
        final Random r = new Random();
        BufferedImage bi = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bi.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setPaint(Color.getHSBColor(r.nextFloat(), 1, 1));
        g2d.setStroke(new BasicStroke(size / 8));
        g2d.drawLine(0, size / 2, size, size / 2);
        g2d.drawLine(size / 2, 0, size / 2, size);
        g2d.dispose();
        return bi;
    }
}
