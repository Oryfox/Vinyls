import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;

public class VinylOfTheDay extends JFrame implements MouseListener {

    static VinylOfTheDay fenster;
    static Record randomSchallplatte;

    static boolean enabled = true;

    public VinylOfTheDay() {
        super(Vinyls.bundle.getString("menubar.vinylOfTheDay"));
        this.setBounds(Toolkit.getDefaultToolkit().getScreenSize().width / 2 - 250, Toolkit.getDefaultToolkit().getScreenSize().height / 2 - 250, 500, 500);

        if (Record.records.size() >= 3) {
            int random = new Random().nextInt(Record.getCount());
            randomSchallplatte = Record.records.get(random % Record.records.size());

            JPanel panel = new JPanel(new BorderLayout());
            panel.add(new JLabel(new ImageIcon(Toolkit.getDefaultToolkit().getImage(Vinyls.cover.getAbsolutePath() + "/" + randomSchallplatte.id + ".png").getScaledInstance(400, 400, Image.SCALE_SMOOTH))), BorderLayout.NORTH);
            JLabel title = new JLabel(randomSchallplatte.title + " - " + randomSchallplatte.artist, SwingConstants.CENTER);
            title.setFont(new Font("Comic Sans MS", Font.BOLD, 18));
            panel.add(title, BorderLayout.SOUTH);

            this.add(panel);
            this.addMouseListener(this);
        } else {
            this.add(new JLabel(Vinyls.bundle.getString("notEnoughRecords")));
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        fenster.setVisible(false);
        new Detail(randomSchallplatte.itemPanel);
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
}
