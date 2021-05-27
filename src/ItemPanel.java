import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

public class ItemPanel extends JPanel implements MouseListener {

    public Record record;
    public int id;
    public ItemPanel self;
    public boolean hover;
    private final JLabel titleLabel;
    private final JLabel artistLabel;

    public static ImageIcon star = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Vinyls.class.getResource("icons/star.png")).getScaledInstance(22,22,Image.SCALE_SMOOTH));
    public static ImageIcon starFull = new ImageIcon(Toolkit.getDefaultToolkit().getImage(Vinyls.class.getResource("icons/star_full.png")).getScaledInstance(22,22,Image.SCALE_SMOOTH));

    public ItemPanel(Record record)
    {
        super(null);
        this.setOpaque(false);
        this.setMinimumSize(new Dimension(180,220));
        this.setPreferredSize(new Dimension(180,220));
        this.setMaximumSize(new Dimension(180,220));

        this.record = record;
        this.id = record.id;

        titleLabel = new JLabel(record.title);
        titleLabel.setBounds(2,182,176,20);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 12));
        this.add(titleLabel);

        artistLabel = new JLabel(record.artist);
        artistLabel.setForeground(Color.darkGray);
        artistLabel.setBounds(2,200,176,20);
        artistLabel.setFont(new Font("Arial", Font.BOLD, 12));
        this.add(artistLabel);

        this.addMouseListener(this);
        self = this;
        this.record.itemPanel = this;
    }

    @Override
    public void paintComponent(Graphics gr) {
        super.paintComponent(gr);

        BufferedImage rounded = new BufferedImage(record.miniCover.getIconWidth(), record.miniCover.getIconHeight(),BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = rounded.createGraphics();
        g.setClip(new RoundRectangle2D.Double(0,0, rounded.getWidth(), rounded.getHeight(), 15, 15));
        g.drawImage(record.miniCover.getImage(),0,0,null);

        if (hover) {
            g.setColor(new Color(0x80AEAEAE, true));
            g.fillRect(0,0,this.getWidth(),this.getHeight());
        }
        g.dispose();

        gr.drawImage(rounded, this.getWidth() / 2 - rounded.getWidth() / 2, 0, null);
        if (hover) {
            if (record.favorite) gr.drawImage(starFull.getImage(), this.getWidth() / 2 - rounded.getWidth() / 2 + rounded.getWidth() - 30,151,null);
            else gr.drawImage(star.getImage(), this.getWidth() / 2 - rounded.getWidth() / 2 + rounded.getWidth() - 30,151,null);
        }

        Rectangle bounds = titleLabel.getBounds();
        bounds.x = this.getWidth() / 2 - rounded.getWidth() / 2;
        titleLabel.setBounds(bounds);
        bounds = artistLabel.getBounds();
        bounds.x = this.getWidth() / 2 - rounded.getWidth() / 2;
        artistLabel.setBounds(bounds);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == 1) {
            new Detail(self);
            hover = false;
            repaint();
        } else if (e.getButton() == 3) { //Instantly calls InformationEdit
            new Detail(self);
            hover = false;
            repaint();
            new InformationEdit(self);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        hover = true;
        repaint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        hover = false;
        repaint();
    }
}
