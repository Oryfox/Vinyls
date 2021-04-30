import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

public class GeniusPanel extends JPanel {

    static String userName;
    static String emailAddress;
    static String userID;
    static String iq;
    static Image userImage;

    static ProfilePanel profilePanel;
    static GeniusOnlineHandler onlinePanel;

    static GeniusOnlineHandler.GeniusSearchItem[] searchResults;

    public GeniusPanel() {
        super(new GridLayout(0, 1));
        profilePanel = null;
        this.setOpaque(false);

        new Thread(this::displayContent).start();
    }

    public static Image getImageFromURL(String urlString) {
        Image image = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.addRequestProperty("User-Agent", "");
            image = ImageIO.read(urlConnection.getInputStream());
            urlConnection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    public void displayContent() {
        if (onlinePanel != null) this.remove(onlinePanel);

        this.add(onlinePanel = new GeniusOnlineHandler());
        this.updateUI();

        if (profilePanel == null) this.add(profilePanel = new ProfilePanel(),0);

        this.updateUI();
    }

    public static class ProfilePanel extends JPanel {

        public ProfilePanel() {
            super(new GridBagLayout());
            this.setOpaque(false);

            if(Genius2.isLoggedIn()) {
                try {
                    String response = Genius2.makeRequest("https://api.genius.com/account");
                    System.out.println(Vinyls.bundle.getString("genius.responseForProfile") + " " + response);

                    JSONObject root = new JSONObject(response);
                    userName = root.getJSONObject("response").getJSONObject("user").getString("name");
                    emailAddress = root.getJSONObject("response").getJSONObject("user").getString("email");
                    userID = "" + root.getJSONObject("response").getJSONObject("user").getLong("id");
                    iq = "" + root.getJSONObject("response").getJSONObject("user").getLong("iq");

                    URL url = new URL(root.getJSONObject("response").getJSONObject("user").getJSONObject("avatar").getJSONObject("medium").getString("url"));
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.addRequestProperty("User-Agent", "");
                    userImage = ImageIO.read(urlConnection.getInputStream());
                    urlConnection.disconnect();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

                GridBagConstraints c = new GridBagConstraints();
                c.gridx = 0;
                c.gridy = 0;
                c.weightx = 0.5;
                c.weighty = 0.5;
                c.fill = 1;
                c.insets = new Insets(20, 20, 20, 20);
                c.gridwidth = 5;
                c.gridheight = 5;

                if (userImage != null) {
                    JLabel imageLabel = new JLabel(new ImageIcon(userImage));
                    this.add(imageLabel, c);
                }
                JLabel userNameLabel = new JLabel(userName) ;
                userNameLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
                userNameLabel.setBorder(BorderFactory.createTitledBorder(Vinyls.bundle.getString("genius.nameHeader")));

                JLabel emailLabel = new JLabel(emailAddress);
                emailLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
                emailLabel.setBorder(BorderFactory.createTitledBorder(Vinyls.bundle.getString("genius.emailHeader")));

                JLabel idLabel = new JLabel(userID);
                idLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
                idLabel.setBorder(BorderFactory.createTitledBorder(Vinyls.bundle.getString("genius.idHeader")));

                JLabel iqLabel = new JLabel(iq);
                iqLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
                iqLabel.setBorder(BorderFactory.createTitledBorder(Vinyls.bundle.getString("genius.iqHeader")));

                c.gridx = 6;
                c.gridwidth = 6;
                c.weightx = 0.3;
                c.weighty = 0.3;
                c.insets = new Insets(15, 0, 15, 0);
                JPanel textFieldHolder = new JPanel(new GridLayout(0, 1));
                textFieldHolder.setOpaque(false);
                ((GridLayout) textFieldHolder.getLayout()).setVgap(28);

                textFieldHolder.add(userNameLabel);
                textFieldHolder.add(emailLabel);
                textFieldHolder.add(idLabel);
                textFieldHolder.add(iqLabel);

                this.add(textFieldHolder, c);
            }

            GridBagConstraints c = new GridBagConstraints();
            c.gridx = 12;
            c.gridy = 0;
            c.weightx = 0.3;
            c.weighty = 0.3;
            c.fill = GridBagConstraints.NONE;
            c.gridwidth = 1;
            c.gridheight = 1;
            c.insets = new Insets(0, 20, 0, 20);

            JButton logButton = new JButton();
            logButton.setPreferredSize(new Dimension(130, 40));
            logButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 16));
            if(Genius2.isLoggedIn()) {
                logButton.setText(Vinyls.bundle.getString("genius.logout"));
                logButton.addActionListener(e -> {
                    userName = null;
                    emailAddress = null;
                    userID = null;
                    iq = null;
                    userImage = null;
                    profilePanel = null;

                    Genius2.logout();
                    Sidebar.SorterPanel.others[4].getMouseListeners()[0].mouseClicked(null);
                });
            } else {
                logButton.setText(Vinyls.bundle.getString("genius.login"));
                logButton.addActionListener(e -> {
                    try {
                        String response = Genius2.makeRequest("https://api.genius.com/account");
                        System.out.println(Vinyls.bundle.getString("genius.responseForProfile") + " " + response);

                        JSONObject root = new JSONObject(response);
                        userName = root.getJSONObject("response").getJSONObject("user").getString("name");
                        emailAddress = root.getJSONObject("response").getJSONObject("user").getString("email");
                        userID = "" + root.getJSONObject("response").getJSONObject("user").getLong("id");
                        iq = "" + root.getJSONObject("response").getJSONObject("user").getLong("iq");

                        URL url = new URL(root.getJSONObject("response").getJSONObject("user").getJSONObject("avatar").getJSONObject("medium").getString("url"));
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.addRequestProperty("User-Agent", "");
                        userImage = ImageIO.read(urlConnection.getInputStream());
                        urlConnection.disconnect();

                        {
                            MainFrame.panel.remove(profilePanel);
                            profilePanel = new ProfilePanel();
                            MainFrame.panel.add(profilePanel);
                            MainFrame.panel.remove(onlinePanel);
                            MainFrame.panel.add(onlinePanel);
                            MainFrame.panel.updateUI();
                        }
                    } catch (IOException | InterruptedException ex) {
                        ex.printStackTrace();
                    }
                });
            }

            this.add(logButton, c);

            c.gridy = 1;
            c.weighty = 0.5;
            c.weightx = 0.5;
            c.fill = GridBagConstraints.BOTH;
            JPanel spacer = new JPanel();
            spacer.setOpaque(false);

            this.add(spacer, c);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(OryColors.BLUE);
            ((Graphics2D)g).fill(new RoundRectangle2D.Double(0,0,this.getWidth(), this.getHeight() - 10, 25,25));
        }
    }

    public static void hitSearch(String searchFor) {
        String response = Genius2.makeClientRequest("https://api.genius.com/search?q=" + searchFor.replace(" ", "%20"));
        System.out.println(Vinyls.bundle.getString("genius.responseFor") + " \"" + searchFor + "\": " + response);


        JSONArray hits = new JSONObject(response).getJSONObject("response").getJSONArray("hits");

        searchResults = new GeniusOnlineHandler.GeniusSearchItem[hits.length()];
        JSONObject current;
        for (int i = 0; i < (Math.min(searchResults.length, 8)); i++) {
            current = hits.getJSONObject(i).getJSONObject("result");
            searchResults[i] = new GeniusOnlineHandler.GeniusSearchItem(
                    current.getString("full_title"),
                    current.getString("title"),
                    current.getString("url"),
                    current.getJSONObject("primary_artist").getString("name"),
                    current.getJSONObject("primary_artist").getString("image_url"),
                    current.getString("song_art_image_thumbnail_url")
            );
        }
        ((GeniusPanel) MainFrame.panel).displayContent();
    }

    public static class GeniusOnlineHandler extends JPanel {

        public GeniusOnlineHandler() {
            super(new BorderLayout());
            this.setOpaque(false);
            this.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

            JRoundedTextField searchField = new JRoundedTextField();
            searchField.setPreferredSize(new Dimension(500, 40));
            new GhostText(searchField, Vinyls.bundle.getString("genius.search"));
            try {
                Font font = Font.createFont(Font.TRUETYPE_FONT, Vinyls.class.getResourceAsStream("fonts/AvenirLTProMedium.otf"));
                searchField.setFont(font.deriveFont(Font.PLAIN,searchField.getFont().getSize() + 6));
            } catch (FontFormatException | IOException e) {
                e.printStackTrace();
            }

            searchField.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                }

                @Override
                public void keyPressed(KeyEvent e) {
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyChar() == '\n') { //Enter Hit Search
                        hitSearch(searchField.getText());
                        MainFrame.panel.requestFocus();
                    } else if ((int) e.getKeyChar() == 27) {
                        MainFrame.panel.requestFocus();
                    }
                }
            });
            this.add(searchField, BorderLayout.NORTH);

            if (searchResults != null) {
                JPanel resultShower = new JPanel(new GridLayout(0, 2));
                ((GridLayout)resultShower.getLayout()).setVgap(10);
                ((GridLayout)resultShower.getLayout()).setHgap(10);
                resultShower.setOpaque(false);
                resultShower.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));

                for (GeniusSearchItem item : searchResults) {
                    if (item != null) resultShower.add(item);
                }

                this.add(resultShower, BorderLayout.CENTER);
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(OryColors.PURPLE);
            ((Graphics2D)g).fill(new RoundRectangle2D.Double(0,0,this.getWidth(), this.getHeight(), 25,25));
        }

        public static class GeniusSearchItem extends JPanel {

            int previousHeight;

            Image artistImageSource;
            Image songArtSource;

            Image artistImage;
            Image songArt;

            public GeniusSearchItem(String fullTitle, String title, String songUrl, String primaryArtist, String artistImageUrl, String songArtUrl) {
                super(new GridLayout(0,1));
                this.setOpaque(false);
                this.setBorder(BorderFactory.createEmptyBorder(0,this.getHeight(),0,this.getHeight()));

                JLabel fullTitleLabel = new JLabel(fullTitle);
                fullTitleLabel.setFont(new Font("Arial", Font.BOLD, 18));
                fullTitleLabel.setForeground(new Color(0x157EFB));
                fullTitleLabel.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        try {
                            Desktop.getDesktop().browse(URI.create(songUrl));
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
                        fullTitleLabel.setForeground(new Color(0x1465CA));
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        fullTitleLabel.setForeground(new Color(0x157EFB));
                    }
                });

                JLabel titleLabel = new JLabel(title);
                titleLabel.setFont(new Font("Arial", Font.PLAIN, 15));
                titleLabel.setForeground(new Color(0x4B000000, true));

                JLabel artistLabel = new JLabel(primaryArtist);
                artistLabel.setFont(new Font("Arial", Font.PLAIN, 15));
                artistLabel.setForeground(new Color(0x4B000000, true));

                this.add(fullTitleLabel);
                this.add(titleLabel);
                this.add(artistLabel);

                new Thread(() -> {
                    this.artistImageSource = getImageFromURL(artistImageUrl);
                    this.songArtSource = getImageFromURL(songArtUrl);
                    this.repaint();
                }).start();
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (artistImageSource != null && songArtSource != null) {
                    if (this.getHeight() != previousHeight) {
                        previousHeight = this.getHeight();
                        artistImage = artistImageSource.getScaledInstance(this.getHeight(),this.getHeight(),Image.SCALE_DEFAULT);
                        songArt = songArtSource.getScaledInstance(this.getHeight(),this.getHeight(),Image.SCALE_DEFAULT);
                        this.setBorder(new EmptyBorder(0,previousHeight + 5,0,previousHeight + 5));
                    }
                }
                g.setClip(new RoundRectangle2D.Double(0,0,this.getHeight(),this.getHeight(),15,15));
                if (songArt != null) g.drawImage(songArt,0,0,null);
                g.setClip(new RoundRectangle2D.Double(this.getWidth() - this.getHeight(),0,this.getHeight(),this.getHeight(),15,15));
                if (artistImage != null) g.drawImage(artistImage,this.getWidth() - this.getHeight(), 0,null);
                g.setClip(0,0,this.getWidth(),this.getHeight());
            }
        }
    }
}
