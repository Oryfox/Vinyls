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

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class MusicPlayer extends JDialog {

    static boolean alreadyPlayingSomething = false;

    String videoID;
    Music music;

    static ImageIcon springImg = new ImageIcon(Vinyls.class.getResource("gui/seasons/spring.png"));
    static ImageIcon summerImg = new ImageIcon(Vinyls.class.getResource("gui/seasons/summer.png"));
    static ImageIcon fallImg = new ImageIcon(Vinyls.class.getResource("gui/seasons/fall.png"));
    static ImageIcon winterImg = new ImageIcon(Vinyls.class.getResource("gui/seasons/winter.png"));

    public MusicPlayer(String videoID) {
        if (alreadyPlayingSomething) return;
        this.setLayout(null);
        this.setUndecorated(true);
        this.setSize(new Dimension(330, 300));
        this.setBackground(new Color(0x0000000, true));
        this.videoID = videoID;

        JPanel background = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                BufferedImage rounded = new BufferedImage(springImg.getIconWidth(), springImg.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
                Graphics2D gr = rounded.createGraphics();
                RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                gr.setRenderingHints(qualityHints);
                gr.setClip(new Ellipse2D.Double(0, 0, 300, 300));
                gr.drawImage(springImg.getImage(), 0, 0, null);
                gr.dispose();
                g.drawImage(rounded, 0, 0, null);

                rounded = new BufferedImage(summerImg.getIconWidth(), summerImg.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
                gr = rounded.createGraphics();
                qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                gr.setRenderingHints(qualityHints);
                gr.setClip(new Ellipse2D.Double(-150, 0, 300, 300));
                gr.drawImage(summerImg.getImage(), 0, 0, null);
                gr.dispose();
                g.drawImage(rounded, 150, 0, null);

                rounded = new BufferedImage(fallImg.getIconWidth(), fallImg.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
                gr = rounded.createGraphics();
                qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                gr.setRenderingHints(qualityHints);
                gr.setClip(new Ellipse2D.Double(0, -150, 300, 300));
                gr.drawImage(fallImg.getImage(), 0, 0, null);
                gr.dispose();
                g.drawImage(rounded, 0, 150, null);

                rounded = new BufferedImage(winterImg.getIconWidth(), winterImg.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
                gr = rounded.createGraphics();
                qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                gr.setRenderingHints(qualityHints);
                gr.setClip(new Ellipse2D.Double(-150, -150, 300, 300));
                gr.drawImage(winterImg.getImage(), 0, 0, null);
                gr.dispose();
                g.drawImage(rounded, 150, 150, null);

                g.setColor(new Color(0x61FFFFFF, true));
                g.fillOval(0, 0, 300, 300);

                g.setColor(Color.BLACK);
                g.drawLine(150, 0, 150, 300);
                g.drawLine(151, 0, 151, 300);
                g.drawLine(0, 150, 300, 150);
                g.drawLine(0, 151, 300, 151);
            }
        };
        background.setOpaque(false);
        background.setBounds(0, 0, 300, 300);

        new Thread(() -> {
            try {
                YouTubeDL.downloadWavByID(videoID, Vinyls.home.getAbsolutePath() + "/cache/" + videoID + ".wav");
                music = new Music(Vinyls.home.getAbsolutePath() + "/cache/" + videoID + ".wav");

                this.add(ControlButtons.createPlayPause(music), 0);
                this.add(ControlButtons.createStop(music, this), 0);
                this.add(ControlButtons.createForward(music), 0);
                this.add(ControlButtons.createBackward(music), 0);
                this.add(ControlButtons.createVolumeControl(music), 0);
                SwingUtilities.updateComponentTreeUI(this);
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        this.add(background);
        this.setVisible(true);
        alreadyPlayingSomething = true;
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (!b) {
            try {
                music.stop();
                Files.deleteIfExists(new File(Vinyls.home.getAbsolutePath() + "/cache/" + videoID + ".wav").toPath());
                alreadyPlayingSomething = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        super.setVisible(false);
    }

    public static class Music {
        // to store current position
        Long currentFrame;
        Clip clip;

        // current status of clip
        String status;

        AudioInputStream audioInputStream;
        String filePath;
        FloatControl control;

        // constructor to initialize streams and clip
        public Music(String filePath) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
            this.filePath = filePath;
            status = "";
            // create AudioInputStream object
            audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());

            // create clip reference
            clip = AudioSystem.getClip();

            // open audioInputStream to the clip
            clip.open(audioInputStream);
            control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            control.setValue(20f * (float) Math.log10(0.1f));
        }

        // Method to play the audio
        public void play() {
            //start the clip
            ((FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN)).setValue(control.getValue());
            clip.start();

            status = "play";
        }

        // Method to pause the audio
        public void pause() {
            if (status.equals("paused")) {
                System.out.println("audio is already paused");
                return;
            }
            this.currentFrame = this.clip.getMicrosecondPosition();
            clip.stop();
            status = "paused";
        }

        // Method to resume the audio
        public void resumeAudio() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
            if (status.equals("play")) {
                System.out.println("Audio is already " + "being played");
                return;
            }
            clip.close();
            resetAudioStream();
            clip.setMicrosecondPosition(currentFrame);
            this.play();
        }

        // Method to stop the audio
        public void stop() {
            currentFrame = 0L;
            clip.stop();
            clip.close();
            try {
                audioInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            clip = null;
            audioInputStream = null;
        }

        // Method to jump over a specific part
        public void jump(long c) throws UnsupportedAudioFileException, IOException, LineUnavailableException {
            if (c > 0 && c < clip.getMicrosecondLength()) {
                clip.stop();
                clip.close();
                resetAudioStream();
                currentFrame = c;
                clip.setMicrosecondPosition(c);
                this.play();
            }
        }

        public void forwardByTenSeconds() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
            jump(clip.getMicrosecondPosition() + 10000000);
        }

        public void backwardByTenSeconds() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
            jump(clip.getMicrosecondPosition() - 10000000);
        }

        // Method to reset audio stream
        public void resetAudioStream() throws UnsupportedAudioFileException, IOException, LineUnavailableException {
            audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
            clip.open(audioInputStream);
        }
    }

    public static class ControlButtons {
        public static JPanel createPlayPause(Music music) {
            final boolean[] hover = {false};
            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);

                    switch (music.status) {
                        case "":
                            g.fillPolygon(new int[]{50, 120, 50}, new int[]{50, 85, 120}, 3);

                            if (hover[0]) {
                                g.setColor(Color.white);
                                g.drawPolygon(new int[]{50, 120, 50}, new int[]{50, 85, 120}, 3);
                            }
                            break;
                        case "play":
                            g.fillRect(50, 50, 30, 70);
                            g.fillRect(90, 50, 30, 70);

                            if (hover[0]) {
                                g.setColor(Color.white);
                                g.drawRect(50, 50, 30, 70);
                                g.drawRect(90, 50, 30, 70);
                            }
                            break;
                        case "paused":
                            g.drawOval(50, 50, 69, 69);
                            g.drawOval(50, 50, 70, 70);
                            g.drawOval(50, 50, 71, 71);
                            if (hover[0]) {
                                g.setColor(Color.white);
                                g.drawOval(50, 50, 69, 69);
                            }

                            BufferedImage bufferedImage = new BufferedImage(springImg.getIconWidth(), springImg.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
                            Graphics gr = bufferedImage.createGraphics();
                            gr.drawImage(springImg.getImage(), 0, 0, null);
                            gr.dispose();
                            g.drawImage(bufferedImage.getSubimage(85, 85, 35, 35), 85, 85, null);
                            g.setColor(new Color(0x61FFFFFF, true));
                            g.fillRect(85, 85, 35, 35);

                            g.setColor(Color.black);
                            g.fillPolygon(new int[]{110, 130, 120}, new int[]{85, 85, 105}, 3);

                            if (hover[0]) {
                                g.setColor(Color.white);
                                g.drawPolygon(new int[]{110, 130, 120}, new int[]{85, 85, 105}, 3);
                            }
                            break;
                    }
                }
            };
            panel.setBounds(0, 0, 150, 150);
            panel.setOpaque(false);

            panel.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    switch (music.status) {
                        case "":
                            music.play();
                        case "paused":
                            try {
                                music.resumeAudio();
                            } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                                ex.printStackTrace();
                            }
                            break;
                        case "play":
                            music.pause();
                            break;
                    }
                    panel.repaint();
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

        public static JPanel createStop(Music music, MusicPlayer parent) {
            final boolean[] hover = {false};
            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.fillRect(30, 50, 70, 70);

                    if (hover[0]) {
                        g.setColor(Color.white);
                        g.drawRect(30, 50, 70, 70);
                    }
                }
            };
            panel.setBounds(150, 0, 150, 150);
            panel.setOpaque(false);
            panel.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    music.stop();
                    try {
                        parent.close();
                        alreadyPlayingSomething = false;
                        Files.deleteIfExists(new File(Vinyls.home.getAbsolutePath() + "/cache/" + parent.videoID + ".wav").toPath());
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

        public static JPanel createForward(Music music) {
            final boolean[] hover = {false};
            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.fillPolygon(new int[]{30, 65, 30}, new int[]{30, 65, 100}, 3);
                    g.fillPolygon(new int[]{65, 100, 65}, new int[]{30, 65, 100}, 3);

                    if (hover[0]) {
                        g.setColor(Color.white);
                        g.drawPolygon(new int[]{30, 65, 30}, new int[]{30, 65, 100}, 3);
                        g.drawPolygon(new int[]{65, 100, 65}, new int[]{30, 65, 100}, 3);
                    }
                }
            };
            panel.setBounds(150, 150, 150, 150);
            panel.setOpaque(false);
            panel.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        music.forwardByTenSeconds();
                    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException unsupportedAudioFileException) {
                        unsupportedAudioFileException.printStackTrace();
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

        public static JPanel createBackward(Music music) {
            final boolean[] hover = {false};
            JPanel panel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.fillPolygon(new int[]{120, 120, 85}, new int[]{30, 100, 65}, 3);
                    g.fillPolygon(new int[]{85, 85, 50}, new int[]{30, 100, 65}, 3);

                    if (hover[0]) {
                        g.setColor(Color.white);
                        g.drawPolygon(new int[]{120, 120, 85}, new int[]{30, 100, 65}, 3);
                        g.drawPolygon(new int[]{85, 85, 50}, new int[]{30, 100, 65}, 3);
                    }
                }
            };
            panel.setBounds(0, 150, 150, 150);
            panel.setOpaque(false);
            panel.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        music.backwardByTenSeconds();
                    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException unsupportedAudioFileException) {
                        unsupportedAudioFileException.printStackTrace();
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

        public static JSlider createVolumeControl(Music music) {
            JSlider slider = new JSlider(JSlider.VERTICAL,0,100,10);
            slider.addChangeListener(changeEvent -> music.control.setValue(20f * (float) Math.log10((float)slider.getValue() / 100)));
            slider.setBounds(300,0,30,300);
            return slider;
        }
    }
}
