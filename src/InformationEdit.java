import com.thizzer.jtouchbar.JTouchBar;
import com.thizzer.jtouchbar.item.TouchBarItem;
import com.thizzer.jtouchbar.item.view.TouchBarButton;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Objects;

public class InformationEdit extends JFrame {

    static InformationEdit self;
    static ItemPanel itemPanel;
    static boolean offen = false;
    static JPanel panel;
    static JScrollPane songScroll;
    static InformationItem[] values = new InformationItem[6];
    static File imageFile;
    static boolean coverDidChange;

    static String[] panelOptions = new String[] {Vinyls.bundle.getString("edit.details"), Vinyls.bundle.getString("edit.cover"), Vinyls.bundle.getString("edit.songs")};
    final static int WIDTH = 700;
    final static int HEIGHT = 650;
    final static Font FRONTFONT = new Font("Arial", Font.PLAIN, 14);

    public InformationEdit(ItemPanel itemPanel)
    {
        InformationEdit.itemPanel = new ItemPanel(new Record(itemPanel.record.id,itemPanel.record.title,itemPanel.record.artist,itemPanel.record.releaseYear,itemPanel.record.color,itemPanel.record.limited,itemPanel.record.bootleg, itemPanel.record.favorite, itemPanel.record.songs));
        InformationEdit.itemPanel.record.cover = itemPanel.record.cover;
        imageFile = null;
        coverDidChange = false;
        Arrays.fill(values, null);
        if (itemPanel.record.songs != null) {
            SongsPanel.songItems = new SongItem[itemPanel.record.songs.length];

            for (int i = 0; i < itemPanel.record.songs.length; i++) {
                SongsPanel.songItems[i] = new SongItem(itemPanel.record.songs[i],i);
            }
        } else {
            SongsPanel.songItems = new SongItem[1];
            SongsPanel.songItems[0] = new SongItem("", 0);
        }
        InformationEdit.itemPanel.record.cover = itemPanel.record.cover;

        this.setLayout(null);
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.setAlwaysOnTop(true);
        this.setResizable(false);
        this.setBounds((Toolkit.getDefaultToolkit().getScreenSize().width / 2) - (WIDTH / 2), (Toolkit.getDefaultToolkit().getScreenSize().height / 2) - (HEIGHT / 2), WIDTH, HEIGHT);
        this.setUndecorated(true);

        this.add(new HeaderPanel());
        this.add(panel = new DetailsPanel());
        this.add(new ButtomControls());

        this.setVisible(true);
        self = this;

        if (Vinyls.mac) {
            JTouchBar jTouchBar = getJTouchBar();
            jTouchBar.show(self);
        }
    }

    private JTouchBar getJTouchBar() {
        JTouchBar jTouchBar = new JTouchBar();
        jTouchBar.setCustomizationIdentifier("Borealis Touchbar");

        TouchBarButton cancel = new TouchBarButton();
        cancel.setTitle(Vinyls.bundle.getString("cancel"));
        cancel.setAction(touchBarView -> self.setVisible(false));
        jTouchBar.addItem(new TouchBarItem("cancel", cancel, true));

        TouchBarButton ok = new TouchBarButton();
        ok.setTitle("OK");
        ok.setAction(touchBarView -> new Thread(() -> {
            redo();
            closeEdit();
        }).start());
        jTouchBar.addItem(new TouchBarItem("ok", ok, true));

        return jTouchBar;
    }

    public static class HeaderPanel extends JPanel {

        public HeaderPanel()
        {
            super(null);
            this.setBackground(new Color(0xDEDEDE));
            this.setBounds(0,0,700, 110);

            JLabel artist = new JLabel(itemPanel.record.artist);
            artist.setFont(new Font("Gill Sans", Font.PLAIN, 23));
            artist.setBounds(160,30,700-110, 25);

            JLabel title = new JLabel(itemPanel.record.title);
            title.setFont(new Font("Chalkboard", Font.BOLD, 15));
            title.setBounds(160,55,700-110, 25);

            JComboBox<String> dropDown = new JComboBox<>(panelOptions);
            dropDown.setBounds(550,15,130,30);
            dropDown.addActionListener(e -> {
                redo();
                applySongs();
                switch(dropDown.getSelectedIndex()) {
                    case 0: //Details
                        self.remove(panel);
                        self.add(panel = new DetailsPanel());
                        break;
                    case 1: //Cover
                        self.remove(panel);
                        self.add(panel = new CoverPanel());
                        break;
                    case 2: //Songs
                        self.remove(panel);
                        JPanel background = new JPanel(null);
                        background.setBackground(Color.white);
                        background.setBounds(0,110,700, 470);
                        JButton plus = new JButton("+");
                        plus.setBounds(12,420,50,50);
                        plus.setFont(new Font("Comic Sans MS", Font.BOLD, 28));
                        plus.addActionListener(ex -> SongsPanel.addSong());
                        background.add(plus);
                        songScroll = new JScrollPane(new SongsPanel());
                        songScroll.setBounds(75,0,550, 470);
                        songScroll.getVerticalScrollBar().setUnitIncrement(15);
                        songScroll.getVerticalScrollBar().setPreferredSize(new Dimension(0,songScroll.getBounds().height));
                        songScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
                        background.add(songScroll);
                        self.add(panel = background);
                        break;
                }
                SwingUtilities.updateComponentTreeUI(self);
            });

            title.setForeground(Color.darkGray);

            this.add(artist);
            this.add(title);
            this.add(dropDown);
        }

        @Override
        protected void paintComponent(Graphics gr) {
            super.paintComponent(gr);
            new ImageIcon(itemPanel.record.miniCover.getImage().getScaledInstance(90,90, Image.SCALE_SMOOTH)).paintIcon(this,gr,50,10);
        }
    }

    public static class DetailsPanel extends JPanel {

        public DetailsPanel()
        {
            super(null);
            this.setBackground(Color.WHITE);
            this.setBounds(0,110,700, 470);

            values[0] = new InformationItem(Vinyls.bundle.getString("record.title"), itemPanel.record.title);
            values[1] = new InformationItem(Vinyls.bundle.getString("record.artist"), itemPanel.record.artist);
            values[2] = new InformationItem(Vinyls.bundle.getString("record.releaseYear"), Integer.toString(itemPanel.record.releaseYear));
            values[3] = new InformationItem(Vinyls.bundle.getString("record.color"), itemPanel.record.color);
            values[4] = new InformationItem(Vinyls.bundle.getString("record.limited"), itemPanel.record.limited);
            values[5] = new InformationItem(Vinyls.bundle.getString("record.bootleg"), itemPanel.record.bootleg);

            int y = 40;
            for (InformationItem item : values) {
                item.setBounds(0,y,item.getPreferredSize().width,item.getPreferredSize().height);
                this.add(item);
                y += 60;
            }
        }
    }

    public static class CoverPanel extends JPanel {

        static boolean hover;

        public CoverPanel()
        {
            super(null);
            this.setBackground(Color.white);
            this.setBounds(0,110,700, 470);
            hover = false;

            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics gr) {
                    super.paintComponent(gr);

                    new ImageIcon(itemPanel.record.cover.getImage().getScaledInstance(450,450,Image.SCALE_SMOOTH)).paintIcon(this,gr,0,0);
                    if (hover) new ImageIcon(Objects.requireNonNull(Vinyls.class.getResource("gui/overlayEDIT.png"))).paintIcon(this,gr,0,0);
                }
            };
            panel.setOpaque(false);
            panel.setBounds(125,20,450,450);
            panel.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {

                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    FileDialog dialog = new FileDialog(self);
                    dialog.setMultipleMode(false);
                    dialog.setDirectory(System.getProperty("user.home") + "/Desktop");
                    dialog.setVisible(true);
                    imageFile = dialog.getFiles()[0];

                    if (imageFile != null) {
                        coverDidChange = true;
                        itemPanel.record.miniCover = new ImageIcon(Toolkit.getDefaultToolkit().getImage(imageFile.getAbsolutePath()).getScaledInstance(180,180,Image.SCALE_SMOOTH));
                        if (Toolkit.getDefaultToolkit().getImage(imageFile.getAbsolutePath()).getWidth(null) > 600) {
                            itemPanel.record.cover = new ImageIcon(Toolkit.getDefaultToolkit().getImage(imageFile.getAbsolutePath()).getScaledInstance(600,600,Image.SCALE_SMOOTH));
                        } else itemPanel.record.cover = new ImageIcon(imageFile.getAbsolutePath());
                        SwingUtilities.updateComponentTreeUI(self);
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    hover = true;
                    panel.repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    hover = false;
                    panel.repaint();
                }
            });
            this.add(panel);
        }
    }

    public static class SongsPanel extends JPanel {

        static SongItem[] songItems;

        public SongsPanel()
        {
            super(null);
            this.setBackground(Color.white);
            if (itemPanel.record.songs == null) this.setPreferredSize(new Dimension(450,0));
            else this.setPreferredSize(new Dimension(550,30 * songItems.length));

            for (SongItem songItem : songItems) {
                this.add(songItem);
            }
        }

        public static void addSong() {
            applySongs();
            songItems = new SongItem[itemPanel.record.songs.length + 1];
            for (int i = 0; i < itemPanel.record.songs.length; i++) {
                SongsPanel.songItems[i] = new SongItem(itemPanel.record.songs[i],i);
            }
            songItems[songItems.length - 1] = new SongItem("", songItems.length - 1);

            JViewport viewport = new JViewport();
            viewport.setView(new SongsPanel());
            songScroll.setViewport(viewport);
            SwingUtilities.updateComponentTreeUI(panel);
        }
    }

    public static class SongItem extends JPanel {

        JRoundedTextField value;
        int index;

        public SongItem(String text, int index)
        {
            super(null);
            this.setPreferredSize(new Dimension(550,30));
            this.setOpaque(false);
            this.setBounds(0,index * 30,550,30);
            this.index = index;

            value = new JRoundedTextField(text);
            value.setBounds(0,2,460,26);
            this.add(value);

            if (this.index > 0) {
                JButton up = new JButton("\u2191");
                up.setBounds(470,5,20,20);
                up.addActionListener(e -> {
                    SongItem buffer = new SongItem(this.value.getText(),this.index -1);
                    SongsPanel.songItems[index] = new SongItem(SongsPanel.songItems[index - 1].value.getText(),index);
                    SongsPanel.songItems[index - 1] = buffer;
                    applySongs();
                });
                this.add(up);
            }

            if (this.index < SongsPanel.songItems.length - 1) {
                JButton down = new JButton("\u2193");
                down.setBounds(495,5,20,20);
                down.addActionListener(e -> {
                    SongItem buffer = new SongItem(this.value.getText(),this.index + 1);
                    SongsPanel.songItems[index] = new SongItem(SongsPanel.songItems[index + 1].value.getText(),index);
                    SongsPanel.songItems[index + 1] = buffer;
                    applySongs();
                });
                this.add(down);
            }

            JButton remove = new JButton("-");
            remove.setBounds(520,5,20,20);
            remove.addActionListener(e -> {
                SongsPanel.songItems[index].value.setText("");
                applySongs(true);
            });
            this.add(remove);

            value.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                    if (e.getKeyChar() == '\n') {
                        SongsPanel.addSong();
                        SongsPanel.songItems[SongsPanel.songItems.length - 1].value.requestFocus();
                    }
                }

                @Override
                public void keyPressed(KeyEvent e) {

                }

                @Override
                public void keyReleased(KeyEvent e) {

                }
            });
        }
    }

    public static class ButtomControls extends JPanel {
        public ButtomControls()
        {
            super(null);
            this.setBounds(0,580,700,70);
            this.setBackground(Color.WHITE);

            JButton abbrechen = new JButton(Vinyls.bundle.getString("cancel"));
            abbrechen.setBounds(450,20, 100, 30);
            abbrechen.addActionListener(e -> self.setVisible(false));

            JButton speichern = new JButton("OK");
            speichern.setBounds(560,20, 100, 30);
            speichern.addActionListener(e -> {
                redo();
                closeEdit();
            });

            this.add(abbrechen);
            this.add(speichern);
        }
    }

    public static class InformationItem extends JPanel {

        JLabel what;
        JTextField value;
        JCheckBox checkBox;

        public InformationItem(String type, String inhalt) {
            super(null);
            this.setOpaque(false);
            this.setPreferredSize(new Dimension(700,40));
            what = new JLabel(type, SwingConstants.RIGHT);
            what.setBounds(0,0,130,30);
            what.setFont(FRONTFONT);

            value = new JTextField(inhalt);
            value.setBounds(140,0,500, 30);
            if (type.equals(Vinyls.bundle.getString("record.releaseYear"))) {
                value.setBounds(140,0,45, 30);
                value.addKeyListener(new KeyListener() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        if (value.getText().length() >= 4) value.setText(value.getText().substring(0,3));
                    }

                    @Override
                    public void keyPressed(KeyEvent e) {

                    }

                    @Override
                    public void keyReleased(KeyEvent e) {

                    }
                });
            }

            what.setForeground(Color.darkGray);

            this.add(what);
            this.add(value);
        }

        public InformationItem(String type, boolean inhalt) {
            super(null);
            this.setPreferredSize(new Dimension(700,30));
            this.setOpaque(false);
            what = new JLabel(type, SwingConstants.RIGHT);
            what.setBounds(0,0,130,30);
            what.setFont(FRONTFONT);

            checkBox = new JCheckBox();
            checkBox.setSelected(inhalt);
            checkBox.setBounds(140, 0, 30,30);

            what.setForeground(Color.darkGray);

            this.add(what);
            this.add(checkBox);
        }
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);

        offen = visible;
    }

    public static void closeEdit() {
        String regColor = "((\\w|\\s)+)";

        String color = itemPanel.record.color;
        int id = itemPanel.record.id;

        if (color.matches(regColor)) {
            if (coverDidChange) {
                try {
                    Files.delete(new File(Vinyls.cover.getAbsolutePath() + "/" + id + ".png").toPath());
                    Files.delete(new File(Vinyls.coverDownsized.getAbsolutePath() + "/" + id + ".png").toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    //Read image
                    BufferedImage image = ImageIO.read(imageFile);

                    if (image.getWidth() >= 600) {
                        Image shrinked = image.getScaledInstance(600, 600, Image.SCALE_SMOOTH);
                        image = new BufferedImage(shrinked.getWidth(null), shrinked.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g = image.createGraphics();
                        g.drawImage(shrinked, 0, 0, null);
                        g.dispose();
                    }//Shrink image to save space
                    ImageIO.write(image, "png", new File(Vinyls.cover.getAbsolutePath() + "/" + id + ".png"));

                    {
                        Image shrinked = image.getScaledInstance(180,180,Image.SCALE_SMOOTH);
                        image = new BufferedImage(shrinked.getWidth(null), shrinked.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g = image.createGraphics();
                        g.drawImage(shrinked,0,0,null);
                        g.dispose();
                        ImageIO.write(image, "png", new File(Vinyls.coverDownsized.getAbsolutePath() + "/" + id + ".png"));
                    }//Creating smaller cover image for flow. Saves ram
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Record.edit(itemPanel.id, itemPanel.record);
            Vinyls.saveJSONData();

            Record.search(""); //Um von Schallplatten Array in Anzeigen Array zu packen
            Detail.itemPanel = itemPanel;
            self.setVisible(false);
            Detail.closeDetails();
            MainFrame.update();
            new Detail(Detail.itemPanel);
        } else {
            JOptionPane.showMessageDialog(null,
                    Vinyls.bundle.getString("edit.formatIncorrect"),
                    Vinyls.bundle.getString("error"),
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void redo() {
        itemPanel.record.title = values[0].value.getText();
        itemPanel.record.artist = values[1].value.getText();
        itemPanel.record.releaseYear = Integer.parseInt(values[2].value.getText());
        itemPanel.record.color = values[3].value.getText();
        itemPanel.record.limited = values[4].checkBox.isSelected();
        itemPanel.record.bootleg = values[5].checkBox.isSelected();
        applySongs();
    }

    public static void applySongs()
    {
        //Not Nulls in den TextFields
        int notNull = 0;
        for (SongItem field : SongsPanel.songItems) {
            if (!field.value.getText().equals("") & !(field.value.getText() == null)) notNull++;
        }

        if (notNull != 0) {
            itemPanel.record.songs = new String[notNull];
            for (SongItem item : SongsPanel.songItems) {
                if (!item.value.getText().equals("")) {
                    for (int i = 0; i < itemPanel.record.songs.length; i++) {
                        if (itemPanel.record.songs[i] == null) {
                            itemPanel.record.songs[i] = item.value.getText();
                            break;
                        }
                    }
                }
            }
        }
        if (songScroll != null) {
            JViewport viewport = new JViewport();
            viewport.setView(new SongsPanel());
            songScroll.setViewport(viewport);
            SwingUtilities.updateComponentTreeUI(panel);
        }
    }

    public static void applySongs(boolean fromRemove)
    {
        //Not Nulls in den TextFields
        int notNull = 0;
        for (SongItem field : SongsPanel.songItems) {
            if (!field.value.getText().equals("") & !(field.value.getText() == null)) notNull++;
        }

        if (notNull != 0) {
            itemPanel.record.songs = new String[notNull];
            for (SongItem item : SongsPanel.songItems) {
                if (!item.value.getText().equals("")) {
                    for (int i = 0; i < itemPanel.record.songs.length; i++) {
                        if (itemPanel.record.songs[i] == null) {
                            itemPanel.record.songs[i] = item.value.getText();
                            break;
                        }
                    }
                }
            }
        } else {
            itemPanel.record.songs = null;
        }

        if(fromRemove) {
            SongsPanel.songItems = new SongItem[SongsPanel.songItems.length - 1];
            if (itemPanel.record.songs != null) {
                for (int i = 0; i < itemPanel.record.songs.length; i++) {
                    SongsPanel.songItems[i] = new SongItem(itemPanel.record.songs[i],i);
                }
            }
        }
        if (songScroll != null) {
            JViewport viewport = new JViewport();
            viewport.setView(new SongsPanel());
            songScroll.setViewport(viewport);
            SwingUtilities.updateComponentTreeUI(panel);
        }
    }
}
