import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;

public class Settings extends JFrame {

    public Settings() {
        super(Vinyls.bundle.getString("settings"));
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.setBounds(100,100,600,500);
        this.setBackground(Color.white);

        JPanel contentPane = new JPanel(new GridLayout(0,1)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(OryColors.PURPLE);
                ((Graphics2D)g).fill(new RoundRectangle2D.Double(10,10,this.getWidth()-20,this.getHeight()-20,25,25));
            }
        };

        JPanel nonGenius = new JPanel(new GridLayout(0,1));
        nonGenius.setOpaque(false);
        nonGenius.add(vinylOfTheDayCheckbox());
        nonGenius.add(experimentalFeatures());
        nonGenius.add(youtubeApiField());
        nonGenius.add(lastFMApiField());
        nonGenius.add(ffmpegbinaryButtons());
        contentPane.add(nonGenius);

        contentPane.add(geniusSet());
        contentPane.setBackground(Color.white);
        contentPane.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));

        this.add(contentPane);

        this.setVisible(true);
    }

    private JCheckBox vinylOfTheDayCheckbox() {
        JCheckBox checkBox = new JCheckBox(Vinyls.bundle.getString("menubar.vinylOfTheDay"), VinylOfTheDay.enabled);
        checkBox.addActionListener(e -> {
            VinylOfTheDay.enabled = !VinylOfTheDay.enabled;
            Vinyls.saveJSONData();
        });
        checkBox.setOpaque(false);
        return checkBox;
    }

    private JCheckBox experimentalFeatures() {
        JCheckBox checkBox = new JCheckBox(Vinyls.bundle.getString("experimental"), Vinyls.betaFeatures);
        checkBox.addActionListener(e -> {
            Vinyls.betaFeatures = !Vinyls.betaFeatures;
            Vinyls.saveJSONData();
        });
        checkBox.setOpaque(false);
        return checkBox;
    }

    private JPanel youtubeApiField() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        ActionListener help = e -> {
            try {
                Desktop.getDesktop().open(Vinyls.help);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        };

        JButton button = new JButton(Vinyls.bundle.getString("help"));
        button.addActionListener(help);

        JPasswordField textField = new JPasswordField(Vinyls.youtubeApiKey);
        Border border = new TitledBorder(new LineBorder(OryColors.PURPLE.darker(), 1, true), "YouTube Data API Key");
        textField.setBorder(border);
        textField.setBackground(OryColors.PURPLE);
        textField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                button.removeActionListener(help);
                button.setText(Vinyls.bundle.getString("save"));
                button.addActionListener(ex -> {
                    Vinyls.setYoutubeApiKey(new String(textField.getPassword()));
                    Vinyls.saveJSONData();
                    button.removeActionListener(button.getActionListeners()[0]);
                    button.setText("Help");
                    button.addActionListener(help);
                });
            }
        });

        panel.add(textField, BorderLayout.CENTER);
        panel.add(button,  BorderLayout.EAST);

        return panel;
    }

    private JPanel lastFMApiField() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);

        ActionListener help = e -> {
            try {
                Desktop.getDesktop().open(Vinyls.help);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        };

        JButton button = new JButton(Vinyls.bundle.getString("help"));
        button.addActionListener(help);

        JPasswordField textField = new JPasswordField(Vinyls.lastFMApiKey);
        Border border = new TitledBorder(new LineBorder(OryColors.PURPLE.darker(), 1, true), "LastFM API Key");
        textField.setBorder(border);
        textField.setBackground(OryColors.PURPLE);
        textField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                button.removeActionListener(help);
                button.setText(Vinyls.bundle.getString("save"));
                button.addActionListener(ex -> {
                    Vinyls.setLastFMApiKey(new String(textField.getPassword()));
                    Vinyls.saveJSONData();
                    button.removeActionListener(button.getActionListeners()[0]);
                    button.setText("Help");
                    button.addActionListener(help);
                });
            }
        });

        panel.add(textField, BorderLayout.CENTER);
        panel.add(button,  BorderLayout.EAST);

        return panel;
    }

    private JPanel ffmpegbinaryButtons() {
        JPanel panel = new JPanel(new GridLayout(1,0));

        panel.setOpaque(false);

        panel.add(new RoundedButton(Vinyls.bundle.getString("ffmpeg.add"), evt -> addFFmpegLibrary(), OryColors.GREEN,16));
        panel.add(new RoundedButton(Vinyls.bundle.getString("ffmpeg.remove"), evt -> removeFFmpegLibrary(), OryColors.RED,16));
        panel.add(new RoundedButton(Vinyls.bundle.getString("ffmpeg.get"), evt -> {
            try {
                Desktop.getDesktop().browse(URI.create("http://ffmpeg.org/download.html#get-packages"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, OryColors.YELLOW,16));

        return panel;
    }

    private JPanel geniusSet() {
        final String[] clientID = {Vinyls.geniusClientID};
        final String[] clientSecret = {Vinyls.geniusClientSecret};
        final String[] clientAccessToken = {Vinyls.geniusClientAccessToken};

        JPanel panel = new JPanel(new GridLayout(0,1)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(OryColors.YELLOW);
                ((Graphics2D)g).fill(new RoundRectangle2D.Double(10,10,this.getWidth()-20,this.getHeight()-20,25,25));
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

        JButton buttonHelp = new JButton(Vinyls.bundle.getString("help"));
        buttonHelp.addActionListener(e -> {
            try {
                Desktop.getDesktop().open(Vinyls.help);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });

        {
            JPasswordField textField = new JPasswordField(clientID[0]);
            Border border = new TitledBorder(new LineBorder(OryColors.YELLOW.darker(), 1, true), "Genius Client ID");
            textField.setBorder(border);
            textField.setBackground(OryColors.YELLOW);
            textField.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {

                }

                @Override
                public void keyPressed(KeyEvent e) {

                }

                @Override
                public void keyReleased(KeyEvent e) {
                    clientID[0] = new String(textField.getPassword());
                    buttonHelp.removeActionListener(buttonHelp.getActionListeners()[0]);
                    buttonHelp.setText(Vinyls.bundle.getString("save"));
                    buttonHelp.addActionListener(ex -> {
                        Vinyls.setGeniusApiCredentials(clientID[0], clientSecret[0], clientAccessToken[0]);
                        Vinyls.saveJSONData();
                        buttonHelp.removeActionListener(buttonHelp.getActionListeners()[0]);
                        buttonHelp.setText(Vinyls.bundle.getString("help"));
                        buttonHelp.addActionListener(evt -> {
                            try {
                                Desktop.getDesktop().open(Vinyls.help);
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        });
                    });
                }
            });

            panel.add(textField);
        } //ClientID

        {
            JPasswordField textField = new JPasswordField(clientSecret[0]);
            Border border = new TitledBorder(new LineBorder(OryColors.YELLOW.darker(), 1, true), "Genius Client Secret");
            textField.setBorder(border);
            textField.setBackground(OryColors.YELLOW);
            textField.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {

                }

                @Override
                public void keyPressed(KeyEvent e) {

                }

                @Override
                public void keyReleased(KeyEvent e) {
                    clientSecret[0] = new String(textField.getPassword());
                    buttonHelp.removeActionListener(buttonHelp.getActionListeners()[0]);
                    buttonHelp.setText(Vinyls.bundle.getString("save"));
                    buttonHelp.addActionListener(ex -> {
                        Vinyls.setGeniusApiCredentials(clientID[0], clientSecret[0], clientAccessToken[0]);
                        Vinyls.saveJSONData();
                        buttonHelp.removeActionListener(buttonHelp.getActionListeners()[0]);
                        buttonHelp.setText(Vinyls.bundle.getString("help"));
                        buttonHelp.addActionListener(evt -> {
                            try {
                                Desktop.getDesktop().open(Vinyls.help);
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        });
                    });
                }
            });

            panel.add(textField);
        } //ClientSecret

        {
            JPasswordField textField = new JPasswordField(clientAccessToken[0]);
            Border border = new TitledBorder(new LineBorder(OryColors.YELLOW.darker(), 1, true), "Genius ClientAccessToken");
            textField.setBorder(border);
            textField.setBackground(OryColors.YELLOW);
            textField.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {

                }

                @Override
                public void keyPressed(KeyEvent e) {

                }

                @Override
                public void keyReleased(KeyEvent e) {
                    clientAccessToken[0] = new String(textField.getPassword());
                    buttonHelp.removeActionListener(buttonHelp.getActionListeners()[0]);
                    buttonHelp.setText(Vinyls.bundle.getString("save"));
                    buttonHelp.addActionListener(ex -> {
                        Vinyls.setGeniusApiCredentials(clientID[0], clientSecret[0], clientAccessToken[0]);
                        Vinyls.saveJSONData();
                        buttonHelp.removeActionListener(buttonHelp.getActionListeners()[0]);
                        buttonHelp.setText(Vinyls.bundle.getString("help"));
                        buttonHelp.addActionListener(evt -> {
                            try {
                                Desktop.getDesktop().open(Vinyls.help);
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        });
                    });
                }
            });

            panel.add(textField);
        } //ClientAccessToken

        panel.add(buttonHelp);

        return panel;
    }

    private void addFFmpegLibrary() {
        FileDialog fileDialog = new FileDialog(this, "Select FFmpeg binary");
        fileDialog.setMultipleMode(false);
        fileDialog.setDirectory(System.getProperty("user.home") + "/Downloads/");
        fileDialog.setVisible(true);
        try {
            Files.copy(fileDialog.getFiles()[0].toPath(), Vinyls.ffmpeg.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        SongActions.ffmpegExists = true;
        this.setVisible(false);
    }

    private void removeFFmpegLibrary() {
        int code = JOptionPane.showConfirmDialog(this, Vinyls.bundle.getString("ffmpeg.confirm"), Vinyls.bundle.getString("ffmpeg.confirm"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (code == JOptionPane.YES_OPTION) {
            try {
                Files.deleteIfExists(Vinyls.ffmpeg.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            SongActions.ffmpegExists = false;
            this.setVisible(false);
        }
    }
}
