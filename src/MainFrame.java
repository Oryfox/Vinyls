import com.thizzer.jtouchbar.JTouchBar;
import com.thizzer.jtouchbar.item.TouchBarItem;
import com.thizzer.jtouchbar.item.view.TouchBarButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Objects;

public class MainFrame extends JFrame implements ComponentListener {

    public static MainFrame frame;
    protected static JPanel basePanel;
    protected static JComponent panel;
    protected static JPanel contentPaneHolder;

    static GridBagConstraints sidebarConstraints = getSidebarConstraints();
    static GridBagConstraints contentPaneConstraints = getContentPaneConstraints();
    static GridBagConstraints innerContentPaneConstraints = getInnerContentPaneConstraints();
    static GridBagConstraints artistPanelConstraints = getArtistPanelConstraints();
    static GridBagConstraints playerPanelConstraints = getPlayerPanelConstraints();

    public static JTouchBar jTouchBar;

    public MainFrame(int width, int height) {
        super("Vinyls");
        frame = this;

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setBounds(Toolkit.getDefaultToolkit().getScreenSize().width / 2 - width / 2, Toolkit.getDefaultToolkit().getScreenSize().height / 2 - height / 2, width, height);
        this.setMinimumSize(new Dimension(450, 340));
        this.setJMenuBar(new MenuBar());
        this.setIconImage(Vinyls.icon = Toolkit.getDefaultToolkit().getImage(Vinyls.class.getClassLoader().getResource("icons/app.png")));

        this.setBackground(Color.white);
        this.getContentPane().setBackground(Color.white);

        basePanel = new JPanel(new GridBagLayout());
        basePanel.setBackground(Color.white);
        basePanel.add(Sidebar.panel = new Sidebar(), sidebarConstraints);

        contentPaneHolder = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setClip(new RoundRectangle2D.Double(0, 0, this.getWidth(), this.getHeight(), 25, 25));
                g.setColor(OryColors.RED);
                g.fillRect(0, 0, this.getWidth(), this.getHeight());
            }
        };
        contentPaneHolder.setBackground(Color.white);
        contentPaneHolder.add(panel = new ScrollPane(true), innerContentPaneConstraints);

        basePanel.add(contentPaneHolder, contentPaneConstraints);

        this.add(basePanel);

        this.setVisible(true);
        this.addComponentListener(this);

        new FileDrop(this, files -> {
            if (files[0].getName().toLowerCase().contains("jpg") | files[0].getName().toLowerCase().contains("png")) {
                new RecordCreation(files[0].getAbsolutePath());
            }
        });

        if (Vinyls.mac) {
            jTouchBar = getTouchBar();
            jTouchBar.show(frame);
        }
    }

    public static void update() {
        if (panel instanceof ScrollPane) {
            if (Sidebar.SorterPanel.others[0].isOpaque()) Record.sort(-1);
            else Record.sort(1);
            JViewport viewport = new JViewport();
            viewport.setOpaque(false);
            viewport.setView((Sidebar.SorterPanel.others[0].isOpaque()) ? new AlbumPanel(true) : new AlbumPanel(false));
            ((ScrollPane) MainFrame.panel).setViewport(viewport);
            SwingUtilities.updateComponentTreeUI(frame);
        } else if (panel instanceof ArtistPanel) {
            Artist.build();
            MainFrame.contentPaneHolder.remove(MainFrame.panel);
            MainFrame.basePanel.remove(MainFrame.panel);
            MainFrame.contentPaneHolder.add(MainFrame.panel = new ArtistPanel(), innerContentPaneConstraints);
            MainFrame.basePanel.add(contentPaneHolder, MainFrame.artistPanelConstraints);
            SwingUtilities.updateComponentTreeUI(MainFrame.frame);
        }
    }

    public static JTouchBar getTouchBar() {
        JTouchBar jTouchBar = new JTouchBar();
        jTouchBar.setCustomizationIdentifier("Borealis Touchbar");

        TouchBarButton addNew = new TouchBarButton();
        addNew.setTitle(Vinyls.bundle.getString("newRecord"));
        addNew.setAction(touchBarView -> new RecordCreation());
        jTouchBar.addItem(new TouchBarItem("addNew", addNew, true));

        TouchBarButton stats = new TouchBarButton();
        stats.setTitle(Vinyls.bundle.getString("menubar.stats"));
        stats.setAction(touchBarView -> new Stats());
        jTouchBar.addItem(new TouchBarItem("menubar.stats", stats, true));

        return jTouchBar;
    }

    private static GridBagConstraints getSidebarConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.weighty = 0.9;
        return c;
    }

    private static GridBagConstraints getContentPaneConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 0.9;
        c.insets = new Insets(10, 0, 10, 10);
        return c;
    }

    private static GridBagConstraints getInnerContentPaneConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(20, 20, 20, 20);
        return c;
    }

    private static GridBagConstraints getArtistPanelConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 1;
        return c;
    }

    private static GridBagConstraints getPlayerPanelConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1;
        c.weighty = 0.03;
        c.gridwidth = 2;
        c.insets = new Insets(0,10,10,10);
        return c;
    }

    @Override
    public void componentResized(ComponentEvent e) {
        if (panel instanceof ScrollPane) {
            ((GridLayout) ((AlbumPanel) ((ScrollPane) panel).getViewport().getView()).getLayout()).setColumns((int) Math.floor((double) (this.getWidth() - 200) / 210));
        } else if (panel instanceof ArtistPanel) {
            ((GridLayout) ((AlbumPanel) ((JScrollPane) ArtistPanel.coverFlow).getViewport().getView()).getLayout()).setColumns((int) Math.floor((double) (this.getWidth() - 480) / 210));
        }
        SwingUtilities.updateComponentTreeUI(panel);
    }

    @Override
    public void componentMoved(ComponentEvent e) {

    }

    @Override
    public void componentShown(ComponentEvent e) {

    }

    @Override
    public void componentHidden(ComponentEvent e) {

    }

    public static class MenuBar extends JMenuBar {

        public MenuBar() {
            this.add(new File());
            this.add(new View());
            this.add(new About());

            this.setBorderPainted(false);
        }

        @SuppressWarnings("ResultOfMethodCallIgnored")
        public static class File extends JMenu {
            public File() {
                super(Vinyls.bundle.getString("menubar.file"));
                this.add(new Item(Vinyls.bundle.getString("newRecord"), e -> {
                    if (Detail.displayed) Detail.closeDetails();
                    if (RecordCreation.frame != null) RecordCreation.frame.setVisible(false);
                    new RecordCreation();
                }, Icons.plus));

                this.add(new JSeparator());

                this.add(new Item(Vinyls.bundle.getString("menubar.exportCSV"), e -> new SongTable.CSVExportWindow(), Icons.grid));

                if (Vinyls.mac) {
                    this.add(new Item(Vinyls.bundle.getString("menubar.saveBackup"), e -> {
                        String previousClassName = UIManager.getSystemLookAndFeelClassName();
                        JFileChooser chooser = new JFileChooser();
                        try {
                            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                            chooser = new JFileChooser();
                            chooser.setDialogTitle(Vinyls.bundle.getString("files.selectLocation"));
                            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                            chooser.setAcceptAllFileFilterUsed(false);
                            chooser.showOpenDialog(frame);
                            UIManager.setLookAndFeel(previousClassName);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        if (chooser.getSelectedFile() == null) {
                            JOptionPane.showMessageDialog(frame, Vinyls.bundle.getString("files.noSelection") + "!", "Fehler!", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
                            Calendar c = Calendar.getInstance();

                            java.io.File ziel = new java.io.File(chooser.getSelectedFile().getAbsolutePath() + "/" + sdf.format(c.getTime()) + ".ovmBackup");
                            ziel.mkdirs();
                            Speichern.backup(ziel);
                            JOptionPane.showMessageDialog(frame, Vinyls.bundle.getString("backup.successful"), Vinyls.bundle.getString("backup.success") + "!", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }));

                    this.add(new Item(Vinyls.bundle.getString("menubar.loadBackup"), e -> {
                        String previousClassName = UIManager.getSystemLookAndFeelClassName();
                        JFileChooser chooser = new JFileChooser();
                        try {
                            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
                            chooser = new JFileChooser();
                            chooser.setDialogTitle(Vinyls.bundle.getString("files.selectLocation"));
                            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                            chooser.setAcceptAllFileFilterUsed(false);
                            chooser.showOpenDialog(frame);
                            UIManager.setLookAndFeel(previousClassName);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        if (chooser.getSelectedFile() == null) {
                            JOptionPane.showMessageDialog(frame, Vinyls.bundle.getString("backup.noSourceSelected") + "!", Vinyls.bundle.getString("error") + "!", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            if (chooser.getSelectedFile().getName().toLowerCase().contains(".ovmbackup")) {
                                try {
                                    Files.deleteIfExists(Vinyls.contentsJSON.toPath());
                                    for (java.io.File f : Objects.requireNonNull(Vinyls.coverDownsized.listFiles())) {
                                        Files.deleteIfExists(f.toPath());
                                    }
                                    Files.deleteIfExists(Vinyls.coverDownsized.toPath());
                                    for (java.io.File f : Objects.requireNonNull(Vinyls.cover.listFiles())) {
                                        Files.deleteIfExists(f.toPath());
                                    }
                                    Files.deleteIfExists(Vinyls.cover.toPath());
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }

                                Vinyls.coverDownsized.mkdirs();

                                java.io.File backup = chooser.getSelectedFile();

                                try {
                                    Files.copy(new java.io.File(backup.getAbsolutePath() + "/contents.json").toPath(), Vinyls.contentsJSON.toPath());
                                    java.io.File[] covers = new java.io.File(backup.getAbsolutePath() + "/cover/downsized").listFiles();
                                    if (covers != null) {
                                        for (java.io.File file : covers) {
                                            Files.copy(file.toPath(), new java.io.File(Vinyls.coverDownsized.getAbsolutePath() + "/" + file.getName()).toPath());
                                        }
                                    }
                                    covers = new java.io.File(backup.getAbsolutePath() + "/cover").listFiles();
                                    if (covers != null) {
                                        for (java.io.File file : covers) {
                                            if (!file.getName().equals("downsized")) {
                                                Files.copy(file.toPath(), new java.io.File(Vinyls.cover.getAbsolutePath() + "/" + file.getName()).toPath());
                                            }
                                        }
                                    }

                                    frame.setVisible(false);
                                    JOptionPane.showMessageDialog(null, Vinyls.bundle.getString("backup.loadSuccessful") + "!", Vinyls.bundle.getString("backup.loadSuccess"), JOptionPane.INFORMATION_MESSAGE);
                                    Vinyls.preload();
                                    frame.remove(panel);
                                    frame.add(panel = new ScrollPane());
                                    Record.load();
                                    frame.setVisible(true);
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            } else {
                                JOptionPane.showMessageDialog(frame, Vinyls.bundle.getString("backup.selectedIsNoBackup") + "!", Vinyls.bundle.getString("error") + "!", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }
                    }));
                }

                this.add(new JSeparator());

                this.add(new Item(Vinyls.bundle.getString("quit"), e -> System.exit(0), Icons.exit));
            }
        }

        public static class View extends JMenu {
            public View() {
                super(Vinyls.bundle.getString("menubar.view"));

                this.add(new Item(Vinyls.bundle.getString("menubar.stats"), e -> new Stats(), Icons.stats));
                this.add(new Item(Vinyls.bundle.getString("menubar.vinylOfTheDay"), e -> VinylOfTheDay.fenster.setVisible(true), Icons.vinylOfTheDay));
            }
        }

        public static class About extends JMenu {
            public About() {
                super(Vinyls.bundle.getString("about"));

                this.add(new Item("Version: " + Vinyls.version, null, false));
                this.add(new Item("Copyright (C) 2021  Semih Kaiser", null, false));
                this.add(new Item(Vinyls.bundle.getString("about.usedSoftware"), e -> new DependencyOverview(), Icons.heart));
                this.add(new JSeparator());
                this.add(new Item(Vinyls.bundle.getString("settings"), e -> new Settings(), Icons.settings));
            }
        }

        public static class Item extends JMenuItem {
            public Item(String name, ActionListener action, boolean enabled) {
                super(name);
                this.addActionListener(action);
                this.setEnabled(enabled);
            }

            public Item(String name, ActionListener action) {
                this(name,action,true);
            }

            public Item(String name, ActionListener action, ImageIcon icon) {
                super(name, icon);
                this.addActionListener(action);
            }
        }
    }
}
