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

import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class SongActions extends JDialog implements WindowFocusListener {

    String songTitle;
    String songArtist;

    JScrollPane scrollPane;
    SongActions songActions;

    static boolean ffmpegExists;

    public SongActions(String songTitle, String songArtist, int x, int y) {
        this.songTitle = songTitle;
        this.songArtist = songArtist;
        this.setUndecorated(true);

        this.setTitle(songTitle);
        this.setMinimumSize(new Dimension(300,150));
        this.setBounds(x,y,300,300);
        this.setMaximumSize(new Dimension(500,300));
        this.setBackground(new Color(0x0000000, true));

        scrollPane = new JScrollPane(new ActionPanel());
        scrollPane.getVerticalScrollBar().setUnitIncrement(8);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        this.add(scrollPane);
        if (!(Genius2.genius == null && (YouTube.apiKey == null || YouTube.apiKey.equals("")))) this.setVisible(true);
        this.addWindowFocusListener(this);

        songActions = this;
    }

    @Override
    public void windowGainedFocus(WindowEvent e) {

    }

    @Override
    public void windowLostFocus(WindowEvent e) {
        this.setVisible(false);
        songActions = null;
    }

    public class ActionPanel extends JPanel {
        public ActionPanel() {
            super(new GridLayout(0,1));
            this.setOpaque(false);

            if (Genius2.genius != null) this.add(createLyrics());
            if (YouTube.apiKey != null && !YouTube.apiKey.equals("")) this.add(createWatchYoutube());
            if ((Vinyls.mac | ffmpegExists) && YouTube.apiKey != null && !YouTube.apiKey.equals("")) this.add(createListenHere());
        }

        public JPanel createLyrics() {
            final boolean[] hover = {false};
            Color normal = new Color(0xFFFFDEF3, true);
            Color highlighted = new Color(0xFFEFA4D4, true);
            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (hover[0]) g.setColor(highlighted);
                    else g.setColor(normal);
                    g.fillRect(0,0,1000,1000);
                }
            };
            panel.setOpaque(false);
            panel.add(new JLabel(Vinyls.bundle.getString("actions.lyrics")),BorderLayout.CENTER);

            panel.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    JSONObject json = new JSONObject(Genius2.makeClientRequest("https://api.genius.com/search?q=" + (songArtist + " " + songTitle).replaceAll(" ", "%20")));
                    try {
                        Desktop.getDesktop().browse(new URI(json.getJSONObject("response").getJSONArray("hits").getJSONObject(0).getJSONObject("result").getString("url")));
                    } catch (IOException | URISyntaxException ex) {
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

        public JPanel createWatchYoutube() {
            final boolean[] hover = {false};
            Color normal = new Color(0xFFFFFAD9, true);
            Color highlighted = new Color(0xFFFFFD83, true);
            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    if (hover[0]) g.setColor(highlighted);
                    else g.setColor(normal);
                    g.fillRect(0,0,1000,1000);
                }
            };
            panel.setOpaque(false);
            panel.add(new JLabel(Vinyls.bundle.getString("actions.musicVideo")),BorderLayout.CENTER);

            panel.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    scrollPane.setViewportView(new YouTubeSelector(true));
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

        public JPanel createListenHere() {
            Color normal = new Color(0xFFCFF1FF, true);
            Color highlighted = new Color(0xFF7DCDEF, true);
            JPanel panel = new JPanel();
            panel.add(new JLabel(Vinyls.bundle.getString("actions.playHere")),BorderLayout.CENTER);

            panel.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    scrollPane.setViewportView(new YouTubeSelector(false));
                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    panel.setBackground(highlighted);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    panel.setBackground(normal);
                }
            });

            return panel;
        }
    }

    public class YouTubeSelector extends JPanel {

        JLabel waitResponse;
        Thread responseThread;

        boolean justWatchOnYouTube;

        public YouTubeSelector(boolean justWatchOnYouTube) {
            super(new GridLayout(0,1));
            this.justWatchOnYouTube = justWatchOnYouTube;
            this.setOpaque(false);

            responseThread = new Thread(() -> {
                JSONObject response = YouTube.search(songArtist + "  " + songTitle);
                JSONArray items = response.getJSONArray("items");

                this.remove(waitResponse);

                for (int i = 0; i < items.length(); i++) {
                    if (i > 2) break; //Only top 3 results
                    this.add(videoElement(items.getJSONObject(i)));
                }
                this.updateUI();
            });
            responseThread.start();

            JButton button = new JButton(Vinyls.bundle.getString("actions.goBack"));
            button.addActionListener(e -> {
                if (responseThread.isAlive()) responseThread.interrupt();
                scrollPane.setViewportView(new ActionPanel());
            });
            this.add(button);
            this.add(waitResponse = new JLabel(Vinyls.bundle.getString("actions.waiting")));
            songActions.setSize(new Dimension(Math.min(songActions.getWidth()+200,600),songActions.getHeight()));
        }

        public JPanel videoElement(JSONObject item) {
            Color normal = new Color(0xFFF15252, true);
            Color highlighted = new Color(0xFFCF1E1E, true);
            JPanel panel = new JPanel(new BorderLayout());
            try {
                panel.add(new JLabel(new ImageIcon(ImageIO.read(new URL(item.getJSONObject("snippet").getJSONObject("thumbnails").getJSONObject("default").getString("url"))))), BorderLayout.WEST);
            } catch (IOException e) {
                e.printStackTrace();
            }
            JPanel title = new JPanel(new GridLayout(0,1));
            title.setOpaque(false);
            title.add(new JLabel(item.getJSONObject("snippet").getString("title")));
            title.add(new JLabel(item.getJSONObject("snippet").getString("channelTitle")));
            panel.add(title, BorderLayout.CENTER);

            panel.setBackground(normal);

            panel.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        if (justWatchOnYouTube) YouTube.watchById(item.getJSONObject("id").getString("videoId"));
                        else {
                            ((JPanel) scrollPane.getViewport().getView()).add(new JLabel("Another music already playing"),0);
                            ((JPanel) scrollPane.getViewport().getView()).updateUI();
                            new MusicPlayer(item.getJSONObject("id").getString("videoId"));
                        }
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
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
                    panel.setBackground(highlighted);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    panel.setBackground(normal);
                }
            });

            return panel;
        }
    }
}
