import de.oryfox.genius.Genius2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.util.Objects;

public class Sidebar extends JPanel {

    static Sidebar panel;
    static boolean sortByTitle = false;
    static JTextField searchField;

    public Sidebar() {
        super(null);
        this.setBackground(Color.white);
        this.setMinimumSize(new Dimension(200, 0));
        this.setPreferredSize(new Dimension(200,0));
        this.setMaximumSize(new Dimension(200,Integer.MAX_VALUE));

        this.add(getSearchField());

        this.add(collection());
        this.add(new SorterPanel());
    }

    @Override
    protected void paintComponent(Graphics gr) {
        super.paintComponent(gr);
        gr.setColor(OryColors.GREEN);
        ((Graphics2D) gr).fill(new RoundRectangle2D.Double(10,10,this.getWidth() - 20,this.getHeight() - 20,25,25));
    }

    //Returns the searchField for the sidebar
    private static JComponent getSearchField() {
        JRoundedTextField textField = new JRoundedTextField(10);
        new GhostText(textField, Vinyls.bundle.getString("sidebar.search"), Color.gray);
        textField.setBounds(20, 20, 160, 30);
        textField.setPreferredSize(new Dimension(Integer.MAX_VALUE,30));
        textField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                if ((int) e.getKeyChar() == 27) panel.requestFocus();
                Record.search(searchField.getText());
            }
        });
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(Vinyls.class.getResourceAsStream("fonts/AvenirLTProMedium.otf")));
            textField.setFont(font.deriveFont(Font.PLAIN,15));
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
        return searchField = textField;
    }

    private static JLabel collection() {
        JLabel label = new JLabel(Vinyls.bundle.getString("sidebar.collection"));
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(Vinyls.class.getResourceAsStream("fonts/Sofia Pro Regular Az.otf")));
            label.setFont(font.deriveFont(Font.PLAIN, 13));
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
        label.setBounds(20, 90, 150, 20);
        label.setForeground(new Color(0x054C29));

        return label;
    }

    @SuppressWarnings("BusyWait")
    //SorterPanel holds the elements to change UI
    public static class SorterPanel extends JPanel {
        static JPanel[] others = new JPanel[5];

        public SorterPanel() {
            super(null);
            this.setOpaque(false);
            this.setBounds(10, 100, 180, 570);

            this.add(others[0] = firstAdded());
            this.add(others[1] = artist());
            this.add(others[2] = title());
            this.add(others[3] = songs());
            if (Genius2.genius != null) this.add(others[4] = genius());
        }

        private static JPanel firstAdded() {
            JPanel self = new JPanel(null) {
                @Override
                protected void paintComponent(Graphics gr) {
                    super.paintComponent(gr);
                    if (isOpaque()) {
                        gr.setColor(Color.white);
                        ((Graphics2D)gr).fill(new RoundRectangle2D.Double(10,16,this.getWidth() + 15, this.getHeight() - 32, 25,25));
                        gr.fillRect(164,0,16,16);
                        gr.fillRect(164,54,16,16);
                        gr.setColor(OryColors.GREEN);
                        gr.fillOval(148,-16,32,32);
                        gr.fillOval(148,54,32,32);
                    }
                }
            };
            self.setBackground(new Color(0x05F5F5F, true));
            self.setBounds(0, 0, 180, 70);

            JLabel label = new JLabel(Vinyls.bundle.getString("sidebar.recentlyAdded"),SwingConstants.CENTER);
            label.setBounds(0, 0, 180, 70);
            label.setFont(new Font("Arial", Font.BOLD, 16));

            self.add(label);

            self.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {

                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    while(!Vinyls.initialized) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                    }

                    for (JPanel panel : others) {
                        if (panel != null) panel.setOpaque(false);
                    }

                    self.setOpaque(true);
                    searchField.setFocusable(true);
                    sortByTitle = false;
                    Record.sort(-1);
                    MainFrame.basePanel.remove(MainFrame.panel);
                    MainFrame.basePanel.remove(MainFrame.contentPaneHolder);
                    MainFrame.contentPaneHolder.remove(MainFrame.panel);
                    MainFrame.contentPaneHolder.add(MainFrame.panel = new ScrollPane(true), MainFrame.innerContentPaneConstraints);
                    MainFrame.basePanel.add(MainFrame.contentPaneHolder, MainFrame.contentPaneConstraints);

                    SwingUtilities.updateComponentTreeUI(MainFrame.frame);
                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });

            return self;
        }

        private static JPanel artist() {
            JPanel self = new JPanel(null) {
                @Override
                protected void paintComponent(Graphics gr) {
                    super.paintComponent(gr);
                    if (isOpaque()) {
                        gr.setColor(Color.white);
                        ((Graphics2D)gr).fill(new RoundRectangle2D.Double(10,16,this.getWidth() + 15, this.getHeight() - 32, 25,25));
                        gr.fillRect(164,0,16,16);
                        gr.fillRect(164,54,16,16);
                        gr.setColor(OryColors.GREEN);
                        gr.fillOval(148,-16,32,32);
                        gr.fillOval(148,54,32,32);
                    }
                }
            };
            self.setBackground(new Color(0x05F5F5F, true));
            self.setBounds(0, 70, 180, 70);
            self.setOpaque(false);

            JLabel label = new JLabel(Vinyls.bundle.getString("record.artist"),SwingConstants.CENTER);
            label.setBounds(0, 0, 180, 70);
            label.setFont(new Font("Arial", Font.BOLD, 16));

            self.add(label);

            self.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {

                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    while(!Vinyls.initialized) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                    }

                    for (JPanel panel : others) {
                        if (panel != null) panel.setOpaque(false);
                    }

                    self.setOpaque(true);
                    searchField.setFocusable(false);
                    Artist.build();
                    MainFrame.contentPaneHolder.remove(MainFrame.panel);
                    MainFrame.basePanel.remove(MainFrame.panel);
                    MainFrame.basePanel.remove(MainFrame.contentPaneHolder);
                    MainFrame.contentPaneHolder.add(MainFrame.panel = new ArtistPanel(), MainFrame.innerContentPaneConstraints);
                    MainFrame.basePanel.add(MainFrame.contentPaneHolder, MainFrame.contentPaneConstraints);

                    SwingUtilities.updateComponentTreeUI(MainFrame.frame);
                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });

            return self;
        }

        private static JPanel title() {
            JPanel self = new JPanel(null) {
                @Override
                protected void paintComponent(Graphics gr) {
                    super.paintComponent(gr);
                    if (isOpaque()) {
                        gr.setColor(Color.white);
                        ((Graphics2D)gr).fill(new RoundRectangle2D.Double(10,16,this.getWidth() + 15, this.getHeight() - 32, 25,25));
                        gr.fillRect(164,0,16,16);
                        gr.fillRect(164,54,16,16);
                        gr.setColor(OryColors.GREEN);
                        gr.fillOval(148,-16,32,32);
                        gr.fillOval(148,54,32,32);
                    }
                }
            };
            self.setBackground(new Color(0x05F5F5F, true));
            self.setBounds(0, 140, 180, 70);
            self.setOpaque(false);

            JLabel label = new JLabel(Vinyls.bundle.getString("record.title"), SwingConstants.CENTER);
            label.setBounds(0, 0, 180, 70);
            label.setFont(new Font("Arial", Font.BOLD, 16));

            self.add(label);

            self.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {

                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    while(!Vinyls.initialized) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                    }

                    for (JPanel panel : others) {
                        if (panel != null) panel.setOpaque(false);
                    }

                    sortByTitle = true;
                    self.setOpaque(true);
                    searchField.setFocusable(true);
                    Record.sort(1);
                    MainFrame.basePanel.remove(MainFrame.panel);
                    MainFrame.basePanel.remove(MainFrame.contentPaneHolder);
                    MainFrame.contentPaneHolder.remove(MainFrame.panel);
                    MainFrame.contentPaneHolder.add(MainFrame.panel = new ScrollPane(), MainFrame.innerContentPaneConstraints);
                    MainFrame.basePanel.add(MainFrame.contentPaneHolder, MainFrame.contentPaneConstraints);

                    SwingUtilities.updateComponentTreeUI(MainFrame.frame);
                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });

            return self;
        }

        private static JPanel songs() {
            JPanel self = new JPanel(null) {
                @Override
                protected void paintComponent(Graphics gr) {
                    super.paintComponent(gr);
                    if (isOpaque()) {
                        gr.setColor(Color.white);
                        ((Graphics2D)gr).fill(new RoundRectangle2D.Double(10,16,this.getWidth() + 15, this.getHeight() - 32, 25,25));
                        gr.fillRect(164,0,16,16);
                        gr.fillRect(164,54,16,16);
                        gr.setColor(OryColors.GREEN);
                        gr.fillOval(148,-16,32,32);
                        gr.fillOval(148,54,32,32);
                    }
                }
            };
            self.setBackground(new Color(0x05F5F5F, true));
            self.setBounds(0, 210, 180, 70);
            self.setOpaque(false);

            JLabel label = new JLabel(Vinyls.bundle.getString("edit.songs"), SwingConstants.CENTER);
            label.setBounds(0, 0, 180, 70);
            label.setFont(new Font("Arial", Font.BOLD, 16));

            self.add(label);

            self.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {

                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    while(!Vinyls.initialized) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                    }

                    for (JPanel panel : others) {
                        if (panel != null) panel.setOpaque(false);
                    }

                    self.setOpaque(true);
                    searchField.setFocusable(false);
                    MainFrame.contentPaneHolder.remove(MainFrame.panel);
                    MainFrame.basePanel.remove(MainFrame.panel);
                    MainFrame.basePanel.remove(MainFrame.contentPaneHolder);
                    MainFrame.contentPaneHolder.add(MainFrame.panel = new SongTable(), MainFrame.innerContentPaneConstraints);
                    MainFrame.basePanel.add(MainFrame.contentPaneHolder, MainFrame.contentPaneConstraints);

                    SwingUtilities.updateComponentTreeUI(MainFrame.frame);
                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });

            return self;
        }

        private static JPanel genius() {
            JPanel self = new JPanel(null) {
                @Override
                protected void paintComponent(Graphics gr) {
                    super.paintComponent(gr);
                    if (isOpaque()) {
                        gr.setColor(Color.white);
                        ((Graphics2D)gr).fill(new RoundRectangle2D.Double(10,16,this.getWidth() + 15, this.getHeight() - 32, 25,25));
                        gr.fillRect(164,0,16,16);
                        gr.fillRect(164,54,16,16);
                        gr.setColor(OryColors.GREEN);
                        gr.fillOval(148,-16,32,32);
                        gr.fillOval(148,54,32,32);
                    }
                }
            };
            self.setBackground(new Color(0x05F5F5F, true));
            self.setBounds(0, 280, 180, 70);
            self.setOpaque(false);

            JLabel label = new JLabel("Genius", SwingConstants.CENTER);
            label.setBounds(0, 0, 180, 70);
            label.setFont(new Font("Arial", Font.BOLD, 16));

            self.add(label);

            self.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {

                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    while(!Vinyls.initialized) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException interruptedException) {
                            interruptedException.printStackTrace();
                        }
                    }

                    for (JPanel panel : others) {
                        if (panel != null) panel.setOpaque(false);
                    }

                    self.setOpaque(true);
                    searchField.setFocusable(false);
                    MainFrame.basePanel.remove(MainFrame.panel);
                    MainFrame.basePanel.remove(MainFrame.contentPaneHolder);
                    MainFrame.contentPaneHolder.remove(MainFrame.panel);
                    MainFrame.contentPaneHolder.add(MainFrame.panel = new GeniusPanel(), MainFrame.innerContentPaneConstraints);
                    MainFrame.basePanel.add(MainFrame.contentPaneHolder, MainFrame.contentPaneConstraints);

                    SwingUtilities.updateComponentTreeUI(MainFrame.frame);
                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });

            return self;
        }
    }
}
