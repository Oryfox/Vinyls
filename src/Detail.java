/*Vinyls - Java software to manage vinyl records by collecting their attributes, cover arts and enjoying various other features.
    Copyright (C) 2021  Semih Kaiser

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.*/

import com.thizzer.jtouchbar.JTouchBar;
import com.thizzer.jtouchbar.item.TouchBarItem;
import com.thizzer.jtouchbar.item.view.TouchBarButton;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Detail extends JPanel {

    public static Detail panel;
    public static ItemPanel itemPanel;
    public static Record record;

    static boolean displayed = false;

    static JTouchBar jTouchBar;

    public Detail(ItemPanel item) {
        super(new GridBagLayout());
        if (displayed) closeDetails();

        itemPanel = item;
        try {
            itemPanel.record.cover = new ImageIcon(ImageIO.read(new File(Vinyls.cover.getAbsolutePath() + "/" + itemPanel.record.id + ".png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
        record = item.record;
        this.setBackground(Color.white);

        if (record.songs != null & record.getThisSongCount() > 0) {
            GridBagConstraints innerConstraints = new GridBagConstraints();

            this.addGB(backButton(), 0, 6, 1, 0.4,0); //Back button

            {
                innerConstraints.gridx = 0;
                innerConstraints.gridy = 0;
                innerConstraints.weightx = 1;
                innerConstraints.weighty = 1;
                innerConstraints.fill = GridBagConstraints.BOTH;
                innerConstraints.insets = new Insets(20, 20, 20, 20);
                JPanel scrollHolder = new JPanel(new GridBagLayout()) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        g.setColor(OryColors.RED);
                        ((Graphics2D) g).fill(new RoundRectangle2D.Double(10, 10, this.getWidth() - 20, this.getHeight() - 20, 25, 25));
                    }
                };
                scrollHolder.setOpaque(false);
                JScrollPane songScroller = new JScrollPane(new SongView());
                songScroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                songScroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
                songScroller.setBorder(BorderFactory.createEmptyBorder());
                songScroller.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
                songScroller.getVerticalScrollBar().setUnitIncrement(12);
                songScroller.setOpaque(false);
                songScroller.getViewport().setOpaque(false);

                scrollHolder.add(songScroller, innerConstraints);
                this.addGB(scrollHolder, 0, 1, 4, 0.4, 0.9);
            } //SongScroller

            {
                JPanel imageHolder = new JPanel(new GridBagLayout()) {
                    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        g.setColor(OryColors.RED);
                        if (this.getWidth() >= this.getHeight())
                            ((Graphics2D) g).fill(new RoundRectangle2D.Double(this.getWidth() / 2 - this.getHeight() / 2, 10, this.getHeight(), this.getHeight() - 20, 25, 25));
                        else
                            ((Graphics2D) g).fill(new RoundRectangle2D.Double(10, this.getHeight() / 2 - this.getWidth() / 2, this.getWidth() - 20, this.getWidth(), 25, 25));
                    }
                };
                imageHolder.add(new AlbumCoverGross(itemPanel.record.cover.getImage()), innerConstraints);
                imageHolder.setOpaque(false);
                this.addGB(imageHolder, 1, 0, 6, 0.6, 1);
            } //ImageHolder

            {
                innerConstraints.gridy = 0;
                innerConstraints.weighty = 0.4;
                innerConstraints.insets = new Insets(20, 20, 20, 20);
                JPanel informationHolder = new JPanel(new GridBagLayout()) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        g.setColor(OryColors.PURPLE);
                        ((Graphics2D) g).fill(new RoundRectangle2D.Double(10, 10, this.getWidth() - 20, this.getHeight() - 20, 25, 25));
                    }
                };
                JPanel innerInformation = new JPanel();
                innerInformation.setOpaque(false);
                innerInformation.setLayout(new BoxLayout(innerInformation, BoxLayout.Y_AXIS));
                innerInformation.add(title(record.title));
                innerInformation.add(artist(record.artist));
                innerInformation.add(information(record));
                informationHolder.setOpaque(false);
                informationHolder.add(innerInformation,innerConstraints);
                this.addGB(informationHolder, 1, 6, 1, 0.6,0);
            } //InformationHolder
            } //Album with at least one song
        else {
            GridBagConstraints innerConstraints = new GridBagConstraints();

            this.addGB(backButton(), 0, 7, 1, 1,0.05); //Back button

            {
                innerConstraints.gridx = 0;
                innerConstraints.gridy = 0;
                innerConstraints.weightx = 1;
                innerConstraints.weighty = 1;
                innerConstraints.fill = GridBagConstraints.BOTH;
                innerConstraints.insets = new Insets(20, 20, 20, 20);
                JPanel imageHolder = new JPanel(new GridBagLayout()) {
                    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        g.setColor(OryColors.RED);
                        if (this.getWidth() >= this.getHeight())
                            ((Graphics2D) g).fill(new RoundRectangle2D.Double(this.getWidth() / 2 - this.getHeight() / 2, 10, this.getHeight(), this.getHeight() - 20, 25, 25));
                        else
                            ((Graphics2D) g).fill(new RoundRectangle2D.Double(10, this.getHeight() / 2 - this.getWidth() / 2, this.getWidth() - 20, this.getWidth(), 25, 25));
                    }
                };
                imageHolder.add(new AlbumCoverGross(itemPanel.record.cover.getImage()), innerConstraints);
                imageHolder.setOpaque(false);
                this.addGB(imageHolder, 0, 0, 6, 1, 0.9);
            } //ImageHolder

            {
                innerConstraints.weighty = 0.4;
                JPanel informationHolder = new JPanel(new GridBagLayout()) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        g.setColor(OryColors.PURPLE);
                        ((Graphics2D) g).fill(new RoundRectangle2D.Double(10, 10, this.getWidth() - 20, this.getHeight() - 20, 25, 25));
                    }
                };
                JPanel innerInformation = new JPanel();
                innerInformation.setOpaque(false);
                innerInformation.setLayout(new BoxLayout(innerInformation, BoxLayout.Y_AXIS));
                innerInformation.add(title(record.title));
                innerInformation.add(artist(record.artist));
                innerInformation.add(information(record));
                informationHolder.setOpaque(false);
                informationHolder.add(innerInformation,innerConstraints);
                this.addGB(informationHolder, 0, 6, 1, 1,0.05);
            } //InformationHolder

        } //Album without songs

        MainFrame.frame.setJMenuBar(new MenuBar());

        MainFrame.frame.remove(MainFrame.basePanel);
        MainFrame.frame.add(this);
        displayed = true;
        SwingUtilities.updateComponentTreeUI(MainFrame.frame);
        if (Vinyls.mac) {
            MainFrame.jTouchBar.hide(MainFrame.frame);
            jTouchBar = getTouchBar();
            jTouchBar.show(MainFrame.frame);
        }
        panel = this;
    }

    private JTouchBar getTouchBar() {
        JTouchBar jTouchBar = new JTouchBar();
        jTouchBar.setCustomizationIdentifier("Borealis TouchBar");

        TouchBarButton close = new TouchBarButton();
        close.setTitle(Vinyls.bundle.getString("close"));
        close.setAction(touchBarView -> new Thread(Detail::closeDetails).start());
        jTouchBar.addItem(new TouchBarItem("close", close, true));

        TouchBarButton edit = new TouchBarButton();
        edit.setTitle(Vinyls.bundle.getString("edit"));
        edit.setAction(touchBarView -> {
            if (!InformationEdit.offen) new InformationEdit(itemPanel);
        });
        jTouchBar.addItem(new TouchBarItem("edit", edit, true));

        jTouchBar.addItem(new TouchBarItem(TouchBarItem.NSTouchBarItemIdentifierFlexibleSpace));
        jTouchBar.addItem(new TouchBarItem(TouchBarItem.NSTouchBarItemIdentifierFlexibleSpace));

        TouchBarButton delete = new TouchBarButton();
        delete.setTitle(Vinyls.bundle.getString("delete"));
        delete.setAction(touchBarView -> new DeleteWindow(itemPanel));
        jTouchBar.addItem(new TouchBarItem("delete", delete, true));

        jTouchBar.addItem(new TouchBarItem(TouchBarItem.NSTouchBarItemIdentifierFlexibleSpace));

        return jTouchBar;
    }

    public static void closeDetails() {
        if (!InformationEdit.offen) {
            if (Vinyls.mac) {
                jTouchBar.hide(MainFrame.frame);
                MainFrame.jTouchBar.show(MainFrame.frame);
            }
            MainFrame.frame.setJMenuBar(new MainFrame.MenuBar());
            MainFrame.frame.remove(panel);
            MainFrame.frame.add(MainFrame.basePanel);
            displayed = false;
            SwingUtilities.updateComponentTreeUI(MainFrame.frame);
            itemPanel.record.cover = null;
        }
    }

    public static JPanel backButton() {
        final boolean[] hover = {false};
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(OryColors.YELLOW);
                ((Graphics2D) g).fill(new RoundRectangle2D.Double(10, 10, this.getWidth() - 20, this.getHeight() - 20, 25, 25));
                if (hover[0]) {
                    g.setColor(new Color(0x658D8D8D, true));
                    ((Graphics2D) g).fill(new RoundRectangle2D.Double(10, 10, this.getWidth() - 20, this.getHeight() - 20, 25, 25));
                }
            }
        };
        panel.setOpaque(false);

        JLabel label = new JLabel(Vinyls.bundle.getString("actions.goBack"), SwingConstants.CENTER);
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, Vinyls.class.getResourceAsStream("fonts/AvenirLTProMedium.otf"));
            label.setFont(font.deriveFont(Font.PLAIN, 20));
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.CENTER;
        panel.add(label, c);

        panel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                closeDetails();
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                hover[0] = true;
                panel.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover[0] = false;
                panel.repaint();
            }
        });

        return panel;
    }

    private void addGB(Component component, int gridx, int gridy, int gridheight, double weightx, double weighty) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = gridx;
        constraints.gridy = gridy;
        constraints.gridwidth = 1;
        constraints.gridheight = gridheight;
        constraints.fill = 1;
        constraints.weightx = weightx;
        constraints.weighty = weighty;
        add(component, constraints);
    }

    public static JLabel title(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 25));

        return label;
    }

    public static JLabel artist(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 25));
        label.setForeground(new Color(0x157EFB));
        label.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Detail.closeDetails();
                Sidebar.SorterPanel.others[1].getMouseListeners()[0].mouseClicked(null);
                for (ArtistPanel.AllArtists.ArtistPane.ArtistItem item : ArtistPanel.AllArtists.ArtistPane.items) {
                    if (item.label.getText().equals(text)) {
                        item.getMouseListeners()[0].mouseClicked(null);
                        break;
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                label.setForeground(new Color(0x1465CA));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                label.setForeground(new Color(0x157EFB));
            }
        });

        return label;
    }

    public static JLabel information(Record record) {
        JLabel label = new JLabel(Vinyls.bundle.getString("record.color").toUpperCase() + ": " + record.color.toUpperCase() + " / " + record.releaseYear);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        label.setForeground(Color.darkGray);

        if (record.limited)
            label.setText(label.getText() + " / " + Vinyls.bundle.getString("record.limited").toUpperCase());
        if (record.bootleg)
            label.setText(label.getText() + " / " + Vinyls.bundle.getString("record.bootleg").toUpperCase());

        return label;
    }

    public static class AlbumCoverGross extends JPanel {

        AlbumCoverGross self;
        Image cover;
        boolean hover;

        public AlbumCoverGross(Image cover) {
            this.cover = cover;
            this.setOpaque(false);
            this.setToolTipText(Vinyls.bundle.getString("openNative"));
            self = this;

            this.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        Desktop.getDesktop().open(new java.io.File(Vinyls.cover.getAbsolutePath() + "/" + record.id + ".png"));
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    repaint();
                }
            });
        }

        @SuppressWarnings("IntegerDivisionInFloatingPointContext")
        @Override
        protected void paintComponent(Graphics gr) {
            super.paintComponent(gr);

            gr.setColor(new Color(0xB2DBDBDB, true));
            if (this.getWidth() >= this.getHeight()) {
                gr.setClip(new RoundRectangle2D.Double(this.getWidth() / 2 - this.getHeight() / 2, 0, this.getHeight(), this.getHeight(), 25, 25));
                gr.drawImage(cover.getScaledInstance(this.getHeight(), this.getHeight(), Vinyls.lowSpecMode ? Image.SCALE_FAST : Image.SCALE_SMOOTH), this.getWidth() / 2 - this.getHeight() / 2, 0, null);
            } else {
                gr.setClip(new RoundRectangle2D.Double(0, this.getHeight() / 2 - this.getWidth() / 2, this.getWidth(), this.getWidth(), 25, 25));
                gr.drawImage(cover.getScaledInstance(this.getWidth(), this.getWidth(), Vinyls.lowSpecMode ? Image.SCALE_FAST : Image.SCALE_SMOOTH), 0, this.getHeight() / 2 - this.getWidth() / 2, this.getWidth(), this.getWidth(), null);
            }
            if (hover) gr.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    public static class DeleteWindow extends JFrame {

        public static JFrame frame;
        public static boolean offen = false;
        static ItemPanel itemPanel;

        public DeleteWindow(ItemPanel itemPanel) {
            super(itemPanel.record.title + " " + Vinyls.bundle.getString("delete").toLowerCase() + "?");
            this.setDefaultCloseOperation(HIDE_ON_CLOSE);
            this.setResizable(false);
            this.setBounds((Toolkit.getDefaultToolkit().getScreenSize().width / 2) - 150, (Toolkit.getDefaultToolkit().getScreenSize().height / 2) - 100, 300, 177);
            this.setLayout(null);

            DeleteWindow.itemPanel = itemPanel;

            this.add(label());
            this.add(yesButton());
            this.add(noButton());

            this.setAlwaysOnTop(true);
            this.setVisible(true);
            offen = true;

            this.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    super.windowClosed(e);
                    offen = false;
                }
            });
            this.addWindowFocusListener(new WindowFocusListener() {
                @Override
                public void windowGainedFocus(WindowEvent e) {

                }

                @Override
                public void windowLostFocus(WindowEvent e) {
                    if (offen) toFront();
                }
            });

            frame = this;

            if (Vinyls.mac) {
                JTouchBar jTouchBar = getTouchBar();
                jTouchBar.show(frame);
            }
        }

        private JTouchBar getTouchBar() {
            JTouchBar jTouchBar = new JTouchBar();
            jTouchBar.setCustomizationIdentifier("Borealis TouchBar");

            jTouchBar.addItem(new TouchBarItem(TouchBarItem.NSTouchBarItemIdentifierFlexibleSpace));

            TouchBarButton no = new TouchBarButton();
            no.setTitle(Vinyls.bundle.getString("no"));
            no.setAction(touchBarView -> {
                offen = false;
                frame.setVisible(false);
            });
            jTouchBar.addItem(new TouchBarItem("no", no, true));

            TouchBarButton yes = new TouchBarButton();
            yes.setTitle(Vinyls.bundle.getString("yes"));
            yes.setAction(touchBarView -> {
                offen = false;
                Detail.closeDetails();
                frame.setVisible(false);
                java.io.File cover = new java.io.File(Vinyls.cover.getAbsolutePath() + "/" + itemPanel.id + ".png");
                java.io.File coverKlein = new java.io.File(Vinyls.coverDownsized.getAbsolutePath() + "/" + itemPanel.id + ".png");
                Record.remove(itemPanel.id);
                if (MainFrame.panel instanceof ScrollPane) {
                    ((AlbumPanel) ((ScrollPane) MainFrame.panel).getViewport().getView()).remove(itemPanel);
                    SwingUtilities.updateComponentTreeUI(((ScrollPane) MainFrame.panel).getViewport().getView());
                } else {
                    MainFrame.update();
                }
                Vinyls.saveJSONData();
                try {
                    Files.delete(cover.toPath());
                    Files.delete(coverKlein.toPath());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
            jTouchBar.addItem(new TouchBarItem("yes", yes, true));

            jTouchBar.addItem(new TouchBarItem(TouchBarItem.NSTouchBarItemIdentifierFlexibleSpace));

            return jTouchBar;
        }

        private static JLabel label() {
            JLabel label = new JLabel(Vinyls.bundle.getString("detail.sureToDelete"), SwingConstants.CENTER);
            label.setBounds(5, 5, 290, 50);
            label.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
            label.setForeground(new Color(0x8C0001));

            return label;
        }

        private static JButton yesButton() {
            JButton button = new JButton(Vinyls.bundle.getString("yes"));
            button.setBounds(5, 70, 145, 80);
            button.setFont(new Font("Arial", Font.BOLD, 18));
            button.addActionListener(e -> {
                offen = false;
                Detail.closeDetails();
                frame.setVisible(false);
                java.io.File cover = new java.io.File(Vinyls.cover.getAbsolutePath() + "/" + itemPanel.id + ".png");
                java.io.File coverKlein = new java.io.File(Vinyls.coverDownsized.getAbsolutePath() + "/" + itemPanel.id + ".png");
                Record.remove(itemPanel.id);
                if (MainFrame.panel instanceof ScrollPane) {
                    ((AlbumPanel) ((ScrollPane) MainFrame.panel).getViewport().getView()).remove(itemPanel);
                    SwingUtilities.updateComponentTreeUI(((ScrollPane) MainFrame.panel).getViewport().getView());
                } else {
                    MainFrame.update();
                }
                Vinyls.saveJSONData();
                try {
                    Files.delete(cover.toPath());
                    Files.delete(coverKlein.toPath());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });

            return button;
        }

        private static JButton noButton() {
            JButton button = new JButton(Vinyls.bundle.getString("no"));
            button.setFont(new Font("Arial", Font.BOLD, 18));
            button.setBounds(150, 70, 145, 80);
            button.addActionListener(e -> {
                offen = false;
                frame.setVisible(false);
            });

            return button;
        }
    }

    public static class MenuBar extends JMenuBar {

        public MenuBar() {
            this.add(new File());
            this.add(new MenuBar.Edit());
            this.add(new View());
        }

        public static class File extends JMenu {
            public File() {
                super(Vinyls.bundle.getString("menubar.file"));
                this.add(new MainFrame.MenuBar.Item(Vinyls.bundle.getString("newRecord"), e -> {
                    closeDetails();
                    if (RecordCreation.frame != null) RecordCreation.frame.setVisible(false);
                    new RecordCreation();
                }));

                this.add(new JSeparator());

                this.add(new MainFrame.MenuBar.Item(Vinyls.bundle.getString("menubar.exportCSV"), e -> new SongTable.CSVExportWindow()));

                this.add(new JSeparator());

                this.add(new MainFrame.MenuBar.Item(Vinyls.bundle.getString("quit"), e -> System.exit(0)));
            }
        }

        public static class Edit extends JMenu {
            public Edit() {
                super(Vinyls.bundle.getString("edit"));
                this.add(new MainFrame.MenuBar.Item("Favorite", e -> {
                    record.favorite = !record.favorite;
                    Vinyls.saveJSONData();
                }));

                this.add(new MainFrame.MenuBar.Item(Vinyls.bundle.getString("edit"), e -> {
                    if (!InformationEdit.offen) new InformationEdit(itemPanel);
                }));

                this.add(new MainFrame.MenuBar.Item(Vinyls.bundle.getString("delete"), e -> new DeleteWindow(itemPanel)));

                if (Vinyls.mac) this.add(new MainFrame.MenuBar.Item("Download ", e -> {
                    if (record.songs != null & record.getThisSongCount() > 0) {
                        DownloadItem[] downloadItems = new DownloadItem[record.songs.length];

                        for (int i = 0; i < record.songs.length; i++) {
                            if (record.songs[i] != null) {
                                downloadItems[i] = new DownloadItem(YouTube.searchID(record.artist + " " + record.songs[i]), (Integer.toString(i + 1).length() == 1 ? "0" + (i + 1) : i + 1) + " " + record.songs[i] + " untagged.mp3");
                            }
                        }

                        try {
                            YouTubeDL.downloadMultiple(new java.io.File(System.getProperty("user.home") + "/Downloads/" + record.artist + " - " + record.title), downloadItems);

                            java.io.File[] files = new java.io.File(System.getProperty("user.home") + "/Downloads/" + record.artist + " - " + record.title).listFiles();
                            String[] splitted;
                            MusicTag musicTag;

                            if (files != null) {
                                for (java.io.File f : files) {
                                    splitted = f.getName().split("\\s");

                                    try {
                                        musicTag = new MusicTag(f.getAbsolutePath());

                                        musicTag.setArtist(record.artist);
                                        musicTag.setTitle(splitted[1]);
                                        musicTag.setTrack(splitted[0]);
                                        musicTag.setYear(Integer.toString(record.releaseYear));
                                        musicTag.setCoverArt(Vinyls.cover.getAbsolutePath() + "/" + record.id + ".png");

                                        musicTag.save();

                                        Files.delete(f.toPath());
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }

                            Files.copy(new java.io.File(Vinyls.cover.getAbsolutePath() + "/" + record.id + ".png").toPath(), new java.io.File(System.getProperty("user.home") + "/Downloads/" + record.artist + " - " + record.title + "/cover.png").toPath());
                        } catch (IOException | InterruptedException ioException) {
                            ioException.printStackTrace();
                        }
                    } else {
                        JOptionPane.showMessageDialog(MainFrame.frame, Vinyls.bundle.getString("noRegisteredSongs"), Vinyls.bundle.getString("noRegisteredSongs.title"), JOptionPane.WARNING_MESSAGE);
                    }
                }));
            }
        }

        public static class View extends JMenu {
            public View() {
                super(Vinyls.bundle.getString("menubar.view"));

                this.add(new MainFrame.MenuBar.Item(Vinyls.bundle.getString("menubar.stats"), e -> new Stats()));
            }
        }
    }
}