import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PlayerPanel extends JPanel {

    static PlayerPanel lastPlayer;
    private final JLabel loadingLabel;

    String title;
    String artist;

    Music music;

    JPanel imageLabel;

    public PlayerPanel(String videoID, Record record, String title, String artist) {
        if (lastPlayer != null && lastPlayer.music != null) {
            lastPlayer.music.stop();
            MainFrame.basePanel.remove(lastPlayer);
        }

        this.title = title;
        this.artist = artist;

        this.setMinimumSize(new Dimension(0, 70));
        this.setPreferredSize(new Dimension(0,70));

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setBackground(OryColors.BLUE);
        this.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

        imageLabel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                this.setPreferredSize(new Dimension(100,Integer.MAX_VALUE));
                this.setMaximumSize(new Dimension(100,Integer.MAX_VALUE));
                g.setClip(new RoundRectangle2D.Double(5,5,imageLabel.getHeight() - 10,imageLabel.getHeight() - 10,25,25));

                Image sized = record.miniCover.getImage().getScaledInstance(imageLabel.getHeight() - 10, imageLabel.getHeight() - 10, Image.SCALE_DEFAULT);
                MediaTracker tracker = new MediaTracker(new java.awt.Container());
                tracker.addImage(sized, 0);
                try {
                    tracker.waitForAll();
                } catch (InterruptedException ex) {
                    throw new RuntimeException("Image loading interrupted", ex);
                }
                g.drawImage(sized, 5, 5, null);
            }
        };
        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                new Detail(record.itemPanel);
            }
        });
        this.add(imageLabel);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, 20));
        titleLabel.setForeground(Color.darkGray);
        titleLabel.setVerticalAlignment(SwingConstants.BOTTOM);

        JLabel artistLabel = new JLabel(artist);
        artistLabel.setForeground(Color.gray);
        artistLabel.setVerticalAlignment(SwingConstants.TOP);

        JPanel labelHolder = new JPanel(new GridLayout(0,1));
        labelHolder.setOpaque(false);
        labelHolder.setPreferredSize(new Dimension(200, 0));
        labelHolder.setMaximumSize(new Dimension(200, 70));
        labelHolder.add(titleLabel);
        labelHolder.add(artistLabel);
        this.add(labelHolder);

        loadingLabel = new JLabel(Vinyls.bundle.getString("loading"));
        loadingLabel.setPreferredSize(new Dimension(200, 0));
        loadingLabel.setFont(new Font(loadingLabel.getFont().getName(), Font.BOLD, 26));
        this.add(loadingLabel);

        MainFrame.basePanel.add(this, MainFrame.playerPanelConstraints);
        MainFrame.basePanel.updateUI();

        new Thread(() -> {
            try {
                YouTubeDL.downloadWavByID(videoID, Vinyls.home.getAbsolutePath() + "/cache/" + videoID + ".wav");
                music = new Music(Vinyls.home.getAbsolutePath() + "/cache/" + videoID + ".wav");

                appendButtons();
                lastPlayer = this;
            } catch (UnsupportedAudioFileException | LineUnavailableException | IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void appendButtons() {
        this.remove(loadingLabel);

        JLabel backwardLabel = new JLabel(Icons.skip_backward);
        backwardLabel.setPreferredSize(new Dimension(120, 0));
        backwardLabel.setMaximumSize(new Dimension(120, 70));
        backwardLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                try {
                    music.backwardByTenSeconds();
                } catch (UnsupportedAudioFileException | IOException | LineUnavailableException unsupportedAudioFileException) {
                    unsupportedAudioFileException.printStackTrace();
                }
            }
        });
        this.add(backwardLabel);

        this.add(Box.createRigidArea(new Dimension(15,0)));

        JLabel playLabel = new JLabel(Icons.play);
        playLabel.setPreferredSize(new Dimension(70, 0));
        playLabel.setMaximumSize(new Dimension(70, 70));
        playLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (music.status.equals("")) {
                    music.play();
                } else if (music.status.equals("paused")) {
                    try {
                        music.resumeAudio();
                    } catch (UnsupportedAudioFileException | IOException | LineUnavailableException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        this.add(playLabel);

        JLabel pauseLabel = new JLabel(Icons.pause);
        pauseLabel.setPreferredSize(new Dimension(70, 0));
        pauseLabel.setMaximumSize(new Dimension(70, 70));
        pauseLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (music.status.equals("play")) {
                    music.pause();
                }
            }
        });
        this.add(pauseLabel);

        JLabel forwardLabel = new JLabel(Icons.skip_forward);
        forwardLabel.setPreferredSize(new Dimension(120, 0));
        forwardLabel.setMaximumSize(new Dimension(120, 70));
        forwardLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                try {
                    music.forwardByTenSeconds();
                } catch (UnsupportedAudioFileException | IOException | LineUnavailableException unsupportedAudioFileException) {
                    unsupportedAudioFileException.printStackTrace();
                }
            }
        });
        this.add(forwardLabel);

        this.add(Box.createHorizontalGlue());

        JSlider slider = new JSlider(JSlider.HORIZONTAL,0,100,10);
        slider.addChangeListener(changeEvent -> music.control.setValue(20f * (float) Math.log10((float)slider.getValue() / 100)));
        slider.setBounds(300,0,30,300);
        slider.setPreferredSize(new Dimension(300, 30));
        slider.setBorder(BorderFactory.createTitledBorder(Vinyls.bundle.getString("volume")));

        this.add(slider);

        this.add(Box.createHorizontalGlue());

        JLabel exitLabel = new JLabel(Icons.exit);
        exitLabel.setPreferredSize(new Dimension(30, 0));
        exitLabel.setMaximumSize(new Dimension(30, 30));
        exitLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                music.stop();
                MainFrame.basePanel.remove(lastPlayer);
                MainFrame.basePanel.updateUI();
                lastPlayer = null;
            }
        });
        this.add(exitLabel);

        this.updateUI();
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setClip(new RoundRectangle2D.Double(0,0,getWidth(),getHeight(),25,25));
        super.paintComponent(g);

        /*BufferedImage i = new BufferedImage(this.getHeight() - 10, this.getHeight() - 10, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gr = i.createGraphics();
        gr.setClip(new RoundRectangle2D.Double(0,0,i.getWidth(),i.getHeight(),25,25));

        Image sized = image.getScaledInstance(this.getHeight() - 10, this.getHeight() - 10, Image.SCALE_DEFAULT);
        MediaTracker tracker = new MediaTracker(new java.awt.Container());
        tracker.addImage(sized, 0);
        try {
            tracker.waitForAll();
        } catch (InterruptedException ex) {
            throw new RuntimeException("Image loading interrupted", ex);
        }

        gr.drawImage(sized,0,0,null);
        gr.dispose();

        g.drawImage(i, 5, 5, null);*/
    }
}

class Music {
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