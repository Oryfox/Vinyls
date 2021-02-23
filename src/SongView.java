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

public class SongView extends JPanel {

    public SongView() {
        super(new GridLayout(0,1));
        this.setOpaque(false);

        for (int i = 0; i < Detail.record.songs.length; i++) {
            this.add(new SongItem(i + 1, Detail.record.songs[i], Detail.record.artist));
        }
    }

    public static class SongItem extends JPanel {

        int number;

        public SongItem(int number, String songTitle, String artist) {
            this.setLayout(new BorderLayout());
            this.setMinimumSize(new Dimension(0,50));
            this.setOpaque(false);
            this.number = number;
            this.setBackground(OryColors.RED);

            this.add(songNumber(number), BorderLayout.WEST);
            this.add(songName(songTitle), BorderLayout.CENTER);

            this.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    new SongActions(songTitle, artist, e.getXOnScreen(), e.getYOnScreen());
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
        }

        public static JLabel songNumber(int number) {
            JLabel label = new JLabel(Integer.toString(number));
            label.setPreferredSize(new Dimension(50,50));
            label.setFont(new Font("Arial", Font.BOLD, 12));

            return label;
        }

        public static JLabel songName(String title) {
            JLabel label = new JLabel(title);
            label.setPreferredSize(new Dimension(Integer.MAX_VALUE,50));
            label.setFont(new Font("Arial", Font.BOLD, 12));

            return label;
        }
    }
}
