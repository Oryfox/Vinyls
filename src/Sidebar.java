import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;

public class Sidebar extends JPanel {

    static Sidebar panel;
    static boolean sortByTitle = false;
    static JTextField searchField;

    static boolean extended = true;

    public Sidebar() {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(Color.white);

        int width = extended ? 200 : 100;
        this.setMinimumSize(new Dimension(width,Integer.MAX_VALUE));
        this.setPreferredSize(new Dimension(width,Integer.MAX_VALUE));
        this.setMaximumSize(new Dimension(width,Integer.MAX_VALUE));

        this.setBorder(BorderFactory.createEmptyBorder(0,10,0,7));

        this.add(getSearchField());

        {
            JPanel spacer = new JPanel();
            spacer.setOpaque(false);
            spacer.setPreferredSize(new Dimension(0,30));
            spacer.setMaximumSize(new Dimension(0,30));
            this.add(spacer);
        }

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
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20,10,10,20));
        panel.setPreferredSize(new Dimension(Integer.MAX_VALUE,65));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE,65));
        panel.setOpaque(false);
        JRoundedTextField textField = new JRoundedTextField(10);
        new GhostText(textField, Vinyls.bundle.getString("sidebar.search"), Color.gray);
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
            Font font = Font.createFont(Font.TRUETYPE_FONT, Vinyls.class.getResourceAsStream("fonts/AvenirLTProMedium.otf"));
            textField.setFont(font.deriveFont(Font.PLAIN,15));
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
        panel.add(searchField = textField);
        return panel;
    }

    private static JPanel collection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(0, 20));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(0,10,0,0));
        panel.setOpaque(false);

        JLabel label = new JLabel(Vinyls.bundle.getString("sidebar.collection"));
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, Vinyls.class.getResourceAsStream("fonts/Sofia Pro Regular Az.otf"));
            label.setFont(font.deriveFont(Font.PLAIN, 13));
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
        label.setForeground(new Color(0x054C29));

        panel.add(label);

        return panel;
    }

    @SuppressWarnings("BusyWait")
    //SorterPanel holds the elements to change UI
    public static class SorterPanel extends JPanel {
        static JPanel[] others = new JPanel[5];

        public SorterPanel() {
            this.setLayout(new GridLayout(0,1));
            this.setOpaque(false);
            this.setPreferredSize(new Dimension(180, 280 + (Genius2.genius == null ? 0 : 70)));
            this.setMaximumSize(new Dimension(180,280 + (Genius2.genius == null ? 0 : 70)));

            boolean empty = others[0] == null;
            int value = 0;
            if (!empty) {
                for (int i = 0; i < others.length; i++) {
                    if (others[i] != null && others[i].isOpaque()) value = i;
                }
            }

            {
                this.add(others[0] = sorterItem(Vinyls.bundle.getString("sidebar.recentlyAdded"), new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        while(!Vinyls.initialized) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException interruptedException) {
                                interruptedException.printStackTrace();
                            }
                        }

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
                }));
            } //Recently added
            {
                this.add(others[1] = sorterItem(Vinyls.bundle.getString("record.artist"), new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        searchField.setFocusable(false);
                        Artist.build();
                        MainFrame.contentPaneHolder.remove(MainFrame.panel);
                        MainFrame.basePanel.remove(MainFrame.panel);
                        MainFrame.basePanel.remove(MainFrame.contentPaneHolder);
                        MainFrame.contentPaneHolder.add(MainFrame.panel = new ArtistPanel(), MainFrame.innerContentPaneConstraints);
                        MainFrame.basePanel.add(MainFrame.contentPaneHolder, MainFrame.contentPaneConstraints);

                        SwingUtilities.updateComponentTreeUI(MainFrame.frame);
                    }
                }));
            } //Artists
            {
                this.add(others[2] = sorterItem(Vinyls.bundle.getString("record.title"), new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        sortByTitle = true;
                        searchField.setFocusable(true);
                        Record.sort(1);
                        MainFrame.basePanel.remove(MainFrame.panel);
                        MainFrame.basePanel.remove(MainFrame.contentPaneHolder);
                        MainFrame.contentPaneHolder.remove(MainFrame.panel);
                        MainFrame.contentPaneHolder.add(MainFrame.panel = new ScrollPane(), MainFrame.innerContentPaneConstraints);
                        MainFrame.basePanel.add(MainFrame.contentPaneHolder, MainFrame.contentPaneConstraints);

                        SwingUtilities.updateComponentTreeUI(MainFrame.frame);
                    }
                }));
            } //Title
            {
                this.add(others[3] = sorterItem(Vinyls.bundle.getString("edit.songs"), new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        searchField.setFocusable(false);
                        MainFrame.contentPaneHolder.remove(MainFrame.panel);
                        MainFrame.basePanel.remove(MainFrame.panel);
                        MainFrame.basePanel.remove(MainFrame.contentPaneHolder);
                        MainFrame.contentPaneHolder.add(MainFrame.panel = new SongTable(), MainFrame.innerContentPaneConstraints);
                        MainFrame.basePanel.add(MainFrame.contentPaneHolder, MainFrame.contentPaneConstraints);

                        SwingUtilities.updateComponentTreeUI(MainFrame.frame);
                    }
                }));
            } //SongTable
            {
                if (Genius2.genius != null) this.add(others[4] = sorterItem("Genius", new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        searchField.setFocusable(false);
                        MainFrame.basePanel.remove(MainFrame.panel);
                        MainFrame.basePanel.remove(MainFrame.contentPaneHolder);
                        MainFrame.contentPaneHolder.remove(MainFrame.panel);
                        MainFrame.contentPaneHolder.add(MainFrame.panel = new GeniusPanel(), MainFrame.innerContentPaneConstraints);
                        MainFrame.basePanel.add(MainFrame.contentPaneHolder, MainFrame.contentPaneConstraints);

                        SwingUtilities.updateComponentTreeUI(MainFrame.frame);
                    }
                }));
            } //Genius
            others[value].setOpaque(true);
        }

        public static JPanel sorterItem(String text, MouseAdapter listener) {
            JPanel panel = new JPanel(new BorderLayout()) {
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
            panel.setBackground(new Color(0x05F5F5F, true));
            panel.setPreferredSize(new Dimension(180,70));
            panel.setMaximumSize(new Dimension(180,70));
            panel.setOpaque(false);

            JLabel label = new JLabel(text,SwingConstants.CENTER);
            label.setFont(new Font("Arial", Font.BOLD, 16));

            panel.add(label);

            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    for (JPanel panel : others) {
                        if (panel != null) panel.setOpaque(false);
                    }

                    panel.setOpaque(true);
                }
            });
            panel.addMouseListener(listener);

            return panel;
        }
    }
}
