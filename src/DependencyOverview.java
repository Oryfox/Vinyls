import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;

public class DependencyOverview extends JFrame {

    static DependencyOverview frame;

    public DependencyOverview() {
        super(Vinyls.bundle.getString("about.usedSoftware"));
        if (frame != null) {
            frame.setVisible(true);
            return;
        }
        this.setIconImage(Vinyls.icon);
        this.setSize(400,600);

        JPanel dependencyHolder = new JPanel(new GridLayout(0, 1));
        dependencyHolder.setBackground(Color.WHITE);

        RoundedButton closeAboutButton = new RoundedButton(Vinyls.bundle.getString("close"), e -> this.setVisible(false), OryColors.YELLOW);
        dependencyHolder.add(closeAboutButton);

        dependencyHolder.add(new DependencyItem("stleary/JSON-java", "https://github.com/stleary/JSON-java"));
        dependencyHolder.add(new DependencyItem("Akaya Telivigala Font", "https://fonts.google.com/specimen/Akaya+Telivigala"));
        dependencyHolder.add(new DependencyItem("Thizzer/JTouchBar", "https://github.com/Thizzer/JTouchBar"));
        dependencyHolder.add(new DependencyItem("mpatric/mp3agic", "https://github.com/mpatric/mp3agic"));
        dependencyHolder.add(new DependencyItem("google/material-design-icons", "https://github.com/google/material-design-icons"));
        dependencyHolder.add(new DependencyItem("ytdl-org/youtube-dl", "https://github.com/ytdl-org/youtube-dl/"));
        dependencyHolder.add(new DependencyItem("twbs/icons", "https://github.com/twbs/icons"));

        JScrollPane scrollPane = new JScrollPane(dependencyHolder);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(0,0));
        scrollPane.getHorizontalScrollBar().setPreferredSize(new Dimension(0,0));
        scrollPane.getVerticalScrollBar().setUnitIncrement(5);

        this.add(scrollPane);
        this.setVisible(true);
    }

    public static class DependencyItem extends JPanel {

        public DependencyItem(String title, String url) {
            super(new GridLayout(0, 1));
            this.setOpaque(false);
            this.setBorder(BorderFactory.createEmptyBorder(5, 15, 10, 15));
            this.setPreferredSize(new Dimension(0,120));

            JLabel titleLabel = new JLabel(title);
            try {
                Font font = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(MainFrame.class.getResourceAsStream("fonts/AkayaTelivigala-Regular.ttf")));
                titleLabel.setFont(font.deriveFont(Font.PLAIN, 26));
            } catch (FontFormatException | IOException e) {
                e.printStackTrace();
            }
            this.add(titleLabel);

            this.add(new RoundedButton("Open in web", e -> {
                try {
                    Desktop.getDesktop().browse(URI.create(url));
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }, OryColors.BLUE, 22));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(OryColors.PURPLE);
            ((Graphics2D) g).fill(new RoundRectangle2D.Double(10, 10, this.getWidth() - 20, this.getHeight() - 20, 25, 25));
        }
    }
}
