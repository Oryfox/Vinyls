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

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

public class Stats extends JFrame implements WindowFocusListener {

    static int WIDTH = 350;
    static int HEIGHT = 150;

    Stats fenster;

    public Stats()
    {
        this.setUndecorated(true);
        this.setDefaultCloseOperation(HIDE_ON_CLOSE);
        this.setBounds((Toolkit.getDefaultToolkit().getScreenSize().width / 2) - (WIDTH / 2),(Toolkit.getDefaultToolkit().getScreenSize().height / 2) - (HEIGHT / 2),WIDTH,HEIGHT);
        this.addWindowFocusListener(this);
        this.add(new Panel());
        this.setVisible(true);
        fenster = this;

        if(Vinyls.mac) {
            getJTouchBar().show(fenster);
        }
    }

    private JTouchBar getJTouchBar() {
        JTouchBar jTouchBar = new JTouchBar();
        jTouchBar.setCustomizationIdentifier("Borealis Touchbar");

        TouchBarButton cancel = new TouchBarButton();
        cancel.setTitle(Vinyls.bundle.getString("close"));
        cancel.setAction(touchBarView -> fenster.setVisible(false));
        jTouchBar.addItem(new TouchBarItem("cancel", cancel, true));

        return jTouchBar;
    }

    public static class Panel extends JPanel {
        public Panel()
        {
            super(new GridLayout(0,1));
            this.setBackground(Color.white);
            this.add(albumCount());
            this.add(artistCount());
            this.add(songCount());
        }

        private static JPanel albumCount()
        {
            JPanel panel = new JPanel();
            panel.setOpaque(false);
            panel.setBounds(0,0,350,100);

            JLabel label = new JLabel(Vinyls.bundle.getString("stats.recordCount"));
            label.setBounds(5,0,350,50);
            label.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
            label.setForeground(Color.black);

            JTextField textField = new JTextField(Integer.toString(Record.getCount()));
            textField.setEditable(false);
            textField.setBounds(5,55,340,40);
            textField.setBackground(Color.lightGray);
            textField.setHorizontalAlignment(JTextField.CENTER);

            panel.add(label, BorderLayout.NORTH);
            panel.add(textField, BorderLayout.SOUTH);

            return panel;
        }

        private static JPanel artistCount()
        {
            JPanel panel = new JPanel();
            panel.setOpaque(false);
            panel.setBounds(0,100,350,100);

            JLabel label = new JLabel(Vinyls.bundle.getString("stats.artistCount"));
            label.setBounds(5,0,350,50);
            label.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
            label.setForeground(Color.black);

            JTextField textField = new JTextField(Integer.toString(Record.getArtistCount()));
            textField.setEditable(false);
            textField.setBounds(5,55,340,40);
            textField.setBackground(Color.lightGray);
            textField.setHorizontalAlignment(JTextField.CENTER);

            panel.add(label, BorderLayout.NORTH);
            panel.add(textField, BorderLayout.SOUTH);

            return panel;
        }

        private static JPanel songCount()
        {
            JPanel panel = new JPanel();
            panel.setOpaque(false);
            panel.setBounds(0,200,350,100);

            JLabel label = new JLabel(Vinyls.bundle.getString("stats.songCount"));
            label.setBounds(5,0,350,50);
            label.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
            label.setForeground(Color.black);

            JTextField textField = new JTextField(Integer.toString(Record.getSongCount()));
            textField.setEditable(false);
            textField.setBounds(5,55,340,40);
            textField.setBackground(Color.lightGray);
            textField.setHorizontalAlignment(JTextField.CENTER);

            panel.add(label, BorderLayout.NORTH);
            panel.add(textField, BorderLayout.SOUTH);

            return panel;
        }
    }

    @Override
    public void windowGainedFocus(WindowEvent e) {

    }

    @Override
    public void windowLostFocus(WindowEvent e) {
        this.setVisible(false);
    }
}
