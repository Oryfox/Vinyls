import com.thizzer.jtouchbar.JTouchBar;
import com.thizzer.jtouchbar.item.TouchBarItem;
import com.thizzer.jtouchbar.item.view.TouchBarButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.geom.RoundRectangle2D;
import java.io.IOException;

public class Stats extends JFrame implements WindowFocusListener {

    final static int WIDTH = 350;
    final static int HEIGHT = 300;

    public Stats() {
        this.setUndecorated(true);
        this.setBounds((Toolkit.getDefaultToolkit().getScreenSize().width / 2) - (WIDTH / 2),(Toolkit.getDefaultToolkit().getScreenSize().height / 2) - (HEIGHT / 2),WIDTH,HEIGHT);
        this.setBackground(new Color(0, true));

        JPanel panel = new JPanel(new GridLayout(0,1)) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(OryColors.PURPLE);
                ((Graphics2D)g).fill(new RoundRectangle2D.Double(0,0,this.getWidth(),this.getHeight(),25,25));
            }
        };

        panel.add(new ScorePanel(Vinyls.bundle.getString("stats.recordCount"), Record.getCount()));
        panel.add(new ScorePanel(Vinyls.bundle.getString("stats.artistCount"), Record.getArtistCount()));
        panel.add(new ScorePanel(Vinyls.bundle.getString("stats.songCount"), Record.getSongCount()));
        panel.add(new RoundedButton(Vinyls.bundle.getString("close"), evt -> this.setVisible(false), OryColors.RED));

        this.add(panel);

        this.setVisible(true);

        if(Vinyls.mac) {
            getJTouchBar().show(this);
        }
        this.addWindowFocusListener(this);
    }

    private JTouchBar getJTouchBar() {
        JTouchBar jTouchBar = new JTouchBar();
        jTouchBar.setCustomizationIdentifier("Borealis Touchbar");

        TouchBarButton cancel = new TouchBarButton();
        cancel.setTitle(Vinyls.bundle.getString("close"));
        cancel.setAction(touchBarView -> this.setVisible(false));
        jTouchBar.addItem(new TouchBarItem("cancel", cancel, true));

        return jTouchBar;
    }

    public class ScorePanel extends JPanel {
        public ScorePanel(String text, int score) {
            super(new BorderLayout());
            JLabel textField = new JLabel(text + ": " + score);
            textField.setVerticalAlignment(SwingConstants.CENTER);
            try {
                textField.setFont(Font.createFont(Font.TRUETYPE_FONT, Stats.class.getResourceAsStream("fonts/Sofia Pro Regular Az.otf")).deriveFont(Font.PLAIN,26));
            } catch (FontFormatException | IOException e) {
                e.printStackTrace();
            }
            this.setOpaque(false);
            this.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
            this.add(textField, BorderLayout.CENTER);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(OryColors.BLUE);
            ((Graphics2D)g).fill(new RoundRectangle2D.Double(10,10,this.getWidth() - 20, this.getHeight() - 20, 25,25));
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
