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
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class RecordCreation extends JFrame {

    static WindowFocusListener listener = new WindowFocusListener() {
        @Override
        public void windowGainedFocus(WindowEvent e) {

        }

        @Override
        public void windowLostFocus(WindowEvent e) {
            frame.setVisible(false);
        }
    };

    protected static RecordCreation frame;

    private final ImageHolder imageHolder;

    private final JRoundedTextField tTitle;
    private final JRoundedTextField tArtist;
    private final JRoundedTextField tReleaseYear;
    private final JRoundedTextField tColor;
    private final JCheckBox cLimited;
    private final JCheckBox cBootleg;
    private BufferedImage image;
    private ArrayList<String> songList;

    protected static ImageIcon cloudIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Vinyls.class.getResource("gui/icons/cloud.png")).getScaledInstance(33, 33, Image.SCALE_SMOOTH));

    public RecordCreation() {
        super(Vinyls.bundle.getString("newRecord.addNewRecord"));
        if (Vinyls.mac) this.setUndecorated(true);
        this.setIconImage(Vinyls.icon);
        this.setBounds(Toolkit.getDefaultToolkit().getScreenSize().width / 2 - 500, MainFrame.frame.getY() + (Vinyls.mac ? 21 : 0), 1000, 521);

        JPanel basePanel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(OryColors.YELLOW);
                ((Graphics2D) g).fill(new RoundRectangle2D.Double(10, 10, this.getWidth() - 20, this.getHeight() - 20, 25, 25));
            }
        };
        basePanel.setBackground(Color.white);

        JPanel textFieldHolder = new JPanel(new GridLayout(0, 1));
        textFieldHolder.setOpaque(false);
        ((GridLayout)textFieldHolder.getLayout()).setVgap(10);
        textFieldHolder.add(tTitle = tTitle());
        textFieldHolder.add(tArtist = tArtist());
        textFieldHolder.add(tReleaseYear = tReleaseYear());
        textFieldHolder.add(tColor = tColor());
        textFieldHolder.add(cLimited = cLimited());
        textFieldHolder.add(cBootleg = cBootleg());
        if (LastFM.apiKey != null && !LastFM.apiKey.equals("")) {
            JPanel buttonHolder = new JPanel(new BorderLayout());
            buttonHolder.setOpaque(false);
            buttonHolder.add(selectCover(), BorderLayout.CENTER);
            buttonHolder.add(loadInformationFromLastFMButton(), BorderLayout.EAST);

            textFieldHolder.add(buttonHolder);
        } else {
            textFieldHolder.add(selectCover());
        }
        textFieldHolder.add(saveButton());
        addGB(basePanel, textFieldHolder, 0, 0.4, new Insets(20, 20, 20, 0));

        imageHolder = new ImageHolder();
        addGB(basePanel, imageHolder, 1, 0.6, new Insets(20,20,20,20));

        this.add(basePanel);
        this.setVisible(true);
        frame = this;

        if (Vinyls.mac) {
            JTouchBar jTouchBar = getTouchBar();
            jTouchBar.show(frame);
        }

        if (Vinyls.mac) {
            addWindowFocusListener(listener);
        }
        basePanel.requestFocus();
    }

    public RecordCreation(String input) {
        this();
        try {
            image = ImageIO.read(new File(input));
            imageHolder.repaint();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JTouchBar getTouchBar() {
        JTouchBar jTouchBar = new JTouchBar();
        jTouchBar.setCustomizationIdentifier("Borealis TouchBar");

        TouchBarButton close = new TouchBarButton();
        close.setTitle(Vinyls.bundle.getString("close"));
        close.setAction(touchBarView -> frame.setVisible(false));
        jTouchBar.addItem(new TouchBarItem("close", close, true));

        TouchBarButton search = new TouchBarButton();
        search.setTitle(Vinyls.bundle.getString("searchImageOnLastFM"));
        search.setAction(touchBarView -> {
            try {
                image = LastFM.getCoverImage(tArtist.getText(), tTitle.getText());
                ImageIO.write(image, "png", new File("lastfm-image.png"));
                songList = LastFM.getSongsForAlbum(tArtist.getText(), tTitle.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        jTouchBar.addItem(new TouchBarItem("search", search, true));

        TouchBarButton save = new TouchBarButton();
        save.setTitle(Vinyls.bundle.getString("save"));
        save.setAction(touchBarView -> {
            if (image != null) {
                if (!tArtist.getText().equals("") & !tTitle.getText().equals("") & !tReleaseYear.getText().equals("") & !tColor.getText().equals("")
                        & tColor.getText().matches("((\\w|\\s)+)")) {
                    save();
                }
            }
        });
        jTouchBar.addItem(new TouchBarItem("save", save, true));

        return jTouchBar;
    }

    protected JRoundedTextField tTitle() {
        JRoundedTextField textField = new JRoundedTextField();
        new GhostText(textField, Vinyls.bundle.getString("record.title"));

        return textField;
    }

    protected JRoundedTextField tArtist() {
        JRoundedTextField textField = new JRoundedTextField();
        new GhostText(textField, Vinyls.bundle.getString("record.artist"));

        return textField;
    }

    protected JRoundedTextField tReleaseYear() {
        JRoundedTextField textField = new JRoundedTextField();
        new GhostText(textField, Vinyls.bundle.getString("record.releaseYear"));

        return textField;
    }

    protected JRoundedTextField tColor() {
        JRoundedTextField textField = new JRoundedTextField();
        new GhostText(textField, Vinyls.bundle.getString("record.color"));

        return textField;
    }

    protected JCheckBox cLimited() {
        JCheckBox checkBox = new JCheckBox(Vinyls.bundle.getString("record.limited"));
        checkBox.setOpaque(false);

        return checkBox;
    }

    protected JCheckBox cBootleg() {
        JCheckBox checkBox = new JCheckBox(Vinyls.bundle.getString("record.bootleg"));
        checkBox.setOpaque(false);

        return checkBox;
    }

    protected JButton selectCover() {
        JButton button = new JButton(Vinyls.bundle.getString("edit.selectCover"));
        button.setPreferredSize(new Dimension(250,50));
        button.addActionListener(e -> {
            if (Vinyls.mac) frame.removeWindowFocusListener(listener);
            FileDialog dialog = new FileDialog(frame);
            dialog.setMultipleMode(false);
            dialog.setDirectory(System.getProperty("user.home") + "/Desktop");
            dialog.setVisible(true);
            try {
                image = ImageIO.read(dialog.getFiles()[0]);
                imageHolder.repaint();
            } catch (ArrayIndexOutOfBoundsException | IOException ex) {
                ex.printStackTrace();
            }
            if (Vinyls.mac) frame.addWindowFocusListener(listener);
        });

        return button;
    }

    protected JButton loadInformationFromLastFMButton() {
        JButton button = new JButton(cloudIcon);
        button.setPreferredSize(new Dimension(50, 50));
        button.setMaximumSize(new Dimension(50, 50));
        button.addActionListener(e -> {
            image = LastFM.getCoverImage(tArtist.getText(), tTitle.getText());
            songList = LastFM.getSongsForAlbum(tArtist.getText(), tTitle.getText());
            imageHolder.repaint();
        });

        return button;
    }

    protected JButton saveButton() {
        JButton button = new JButton(Vinyls.bundle.getString("save"));
        button.addActionListener(e -> {
            if (image != null) {
                save();
            } else {
                JOptionPane.showMessageDialog(this,Vinyls.bundle.getString("newRecord.missingImage"));
            }
        });

        return button;
    }

    private void save() {
        int id = Vinyls.getNextID();

        try {
            if (image.getWidth() > 600) {
                Image shrinked = image.getScaledInstance(600, 600, Image.SCALE_SMOOTH);
                image = new BufferedImage(shrinked.getWidth(null), shrinked.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = image.createGraphics();
                g.drawImage(shrinked, 0, 0, null);
                g.dispose();
            }//Shrink image to save space
            ImageIO.write(image, "png", new File(Vinyls.cover.getAbsolutePath() + "/" + id + ".png"));
            {
                Image shrinked = image.getScaledInstance(180, 180, Image.SCALE_SMOOTH);
                image = new BufferedImage(shrinked.getWidth(null), shrinked.getHeight(null), BufferedImage.TYPE_INT_ARGB);
                Graphics2D g = image.createGraphics();
                g.drawImage(shrinked, 0, 0, null);
                g.dispose();
                ImageIO.write(image, "png", new File(Vinyls.coverDownsized.getAbsolutePath() + "/" + id + ".png"));
            }//Creating smaller cover image for flow. Saves ram
        } catch (IOException e) {
            e.printStackTrace();
        }
        Record newRecord = new Record(id, tTitle.getText(), tArtist.getText(), Integer.parseInt(tReleaseYear.getText().equals("") || tReleaseYear.getText().equals(Vinyls.bundle.getString("record.releaseYear")) ? "0" : tReleaseYear.getText()), tColor.getText(), cLimited.isSelected(), cBootleg.isSelected(), false, songList == null ? null : toArray(songList));

        //Adds records to ArrayList
        Record.add(newRecord);
        Record.visibleRecords.add(newRecord);

        Vinyls.saveJSONData();
        this.setVisible(false);

        if (MainFrame.panel instanceof ScrollPane) {
            if (((ScrollPane) MainFrame.panel).inverted) {
                ((AlbumPanel) ((ScrollPane) MainFrame.panel).getViewport().getView()).add(new ItemPanel(newRecord), 0);
                SwingUtilities.updateComponentTreeUI(((ScrollPane) MainFrame.panel).getViewport().getView());
            } else MainFrame.update();
        } else {
            MainFrame.update();
        } //Update UI
    }

    public static String[] toArray(ArrayList<String> list) {
        String[] array = new String[list.size()];
        for (int i = 0; i < array.length; i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    private static void addGB(JComponent parent, Component component, int gridx, double weightx, Insets insets) {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = gridx;
        constraints.gridy = 0;
        constraints.gridwidth = 1;
        constraints.gridheight = 1;
        constraints.fill = 1;
        constraints.weightx = weightx;
        constraints.weighty = 0.9;
        constraints.insets = insets;
        parent.add(component, constraints);
    }

    public class ImageHolder extends JPanel {
        public ImageHolder() {
            this.setOpaque(false);
        }

        @SuppressWarnings("IntegerDivisionInFloatingPointContext")
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(OryColors.YELLOW.darker());
            if (this.getWidth() > this.getHeight()) {
                g.setClip(new RoundRectangle2D.Double(this.getWidth() / 2 - this.getHeight() / 2, 0,this.getHeight(), this.getHeight(),25,25));
                g.fillRect(0,0,this.getWidth(),this.getHeight());
                if (image != null) g.drawImage(image.getScaledInstance(this.getHeight(), this.getHeight(), Image.SCALE_DEFAULT), this.getWidth() / 2 - this.getHeight() / 2, 0, null);
            } else {
                g.setClip(new RoundRectangle2D.Double(0, this.getHeight() / 2 - this.getWidth() / 2,this.getWidth(), this.getWidth(),25,25));
                g.fillRect(0,0,this.getWidth(),this.getHeight());
                if (image != null) g.drawImage(image.getScaledInstance(this.getWidth(), this.getWidth(), Image.SCALE_DEFAULT), 0, this.getHeight() / 2 - this.getWidth() / 2, null);
            }
        }
    }
}
