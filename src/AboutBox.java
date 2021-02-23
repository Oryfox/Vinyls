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

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;
import java.io.*;

public class AboutBox extends JFrame {
    static boolean opened = false;

    Font font;

    public AboutBox() {
        if (opened) return;
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, Vinyls.class.getResourceAsStream("fonts/airstrike.ttf"));
            this.font = font.deriveFont(Font.PLAIN, 20);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }
        this.setIconImage(Vinyls.icon);
        this.setTitle(Vinyls.bundle.getString("about") + " Vinyls");

        JPanel panel = new JPanel(new GridLayout(0, 1)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(OryColors.BLUE);
                ((Graphics2D) g).fill(new RoundRectangle2D.Double(10, 10, this.getWidth() - 20, this.getHeight() - 20, 25, 25));
            }
        };
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.white);

        panel.add(version());
        panel.add(copyright());
        panel.add(ffmpeg());
        panel.add(information());

        JScrollPane scrollPane = new JScrollPane(panel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(6);
        this.add(scrollPane);

        this.setSize(1000, 300);
        this.setVisible(true);
    }

    public JLabel version() {
        JLabel label = new JLabel("Version: " + Vinyls.version);
        label.setFont(font);
        return label;
    }

    public JLabel copyright() {
        JLabel label = new JLabel("Copyright (C) 2021  Semih Kaiser");
        label.setFont(font);
        return label;
    }

    public JLabel ffmpeg() {
        JLabel label = new JLabel(Vinyls.bundle.getString("about.FFmpeg"));
        label.setFont(font);
        return label;
    }

    public JLabel information() {
        JLabel label = new JLabel(Vinyls.bundle.getString("about.box"));
        label.setFont(font);
        label.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    FileOutputStream out = new FileOutputStream(Vinyls.cache.getAbsolutePath() + "/README.md");
                    InputStream in = Vinyls.class.getResourceAsStream("README.md");

                    byte[] bytes = new byte[16 * 1024];
                    int count;
                    while ((count = in.read(bytes)) > 0) {
                        out.write(bytes, 0, count);
                    }
                    in.close();
                    out.close();
                    Desktop.getDesktop().open(new File(Vinyls.cache.getAbsoluteFile() + "/README.md"));
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

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        });
        return label;
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        opened = b;
    }
}