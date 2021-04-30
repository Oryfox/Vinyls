import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.RoundRectangle2D;

public class ArtistPanel extends JPanel {

    static AllArtists allArtists;
    static JComponent coverFlow;

    static GridBagConstraints artistListConstraints = artistListConstraints();
    static GridBagConstraints coverFlowConstraints = getCoverFlowConstraints();

    public ArtistPanel() {
        super(new GridBagLayout());
        this.setOpaque(false);

        JPanel placeHolder = new JPanel();
        placeHolder.setOpaque(false);
        this.add(allArtists = new AllArtists(), artistListConstraints);
        this.add(coverFlow = placeHolder, getCoverFlowConstraints());
    }

    private static GridBagConstraints artistListConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.BOTH;

        return c;
    }

    private static GridBagConstraints getCoverFlowConstraints() {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.weightx = 1;
        c.weighty = 1;
        c.insets = new Insets(20,20,0,0);
        c.fill = GridBagConstraints.BOTH;

        return c;
    }

    public static class AllArtists extends JScrollPane {
        public AllArtists()
        {
            super(new ArtistPane());
            this.setMinimumSize(new Dimension(280,Integer.MAX_VALUE));
            this.setPreferredSize(new Dimension(280,Integer.MAX_VALUE));
            this.setMaximumSize(new Dimension(280,Integer.MAX_VALUE));
            this.setBounds(0,0,260,720);
            this.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
            this.getVerticalScrollBar().setUnitIncrement(12);
            this.getVerticalScrollBar().setPreferredSize(new Dimension(0,720));
            this.setBorder(BorderFactory.createEmptyBorder());
            this.setOpaque(false);
            this.getViewport().setOpaque(false);
        }

        public static class ArtistPane extends JPanel {

            static ArtistItem[] items;

            public ArtistPane()
            {
                super(null);
                items = new ArtistItem[Artist.artists.length];
                this.setBounds(0,0,280,Math.max(50 * Artist.artists.length, 720));
                this.setPreferredSize(new Dimension(280,Math.max(720,50*Artist.artists.length)));
                this.setOpaque(false);

                for (int i = 0; i < items.length; i++) {
                    this.add(items[i] = new ArtistItem(Artist.artists[i],i * 50));
                }
            }

            @Override
            protected void paintComponent(Graphics gr) {
                super.paintComponent(gr);
                gr.setColor(new Color(159, 159, 159, 80));
                for(int i = 1; i < items.length; i++) {
                    gr.drawLine(0,i * 50,280, i * 50);
                }
                gr.drawLine(279,0,279,Math.max(50 * Artist.artists.length, 720));
            }

            public static class ArtistItem extends JPanel {

                ArtistItem self;
                JLabel label;

                public ArtistItem(Artist artist, int y)
                {
                    super(null);
                    this.setPreferredSize(new Dimension(280,50));
                    this.setMaximumSize(new Dimension(280,50));
                    this.setBounds(0,y,280,50);
                    this.setOpaque(false);
                    this.setBackground(new Color(0x157EFB));

                    label = new JLabel(artist.name);
                    label.setBounds(20,0,240,50);
                    label.setPreferredSize(new Dimension(280,50));
                    this.add(label);

                    self = this;
                    this.addMouseListener(new MouseListener() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            for (ArtistItem item : items) {
                                item.setOpaque(false);
                                item.label.setForeground(Color.black);
                            }

                            self.setOpaque(true);
                            label.setForeground(OryColors.BLUE);
                            if (coverFlow != null) MainFrame.panel.remove(coverFlow);
                            MainFrame.panel.add(coverFlow = new CoverFlow(artist), coverFlowConstraints);
                            SwingUtilities.updateComponentTreeUI(MainFrame.panel);
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
                    });
                }

                @Override
                protected void paintComponent(Graphics g) {
                    g.setClip(new RoundRectangle2D.Double(0,0,this.getWidth(), this.getHeight(), 25,25));
                    super.paintComponent(g);
                }
            }
        }
    }

    public static class CoverFlow extends JScrollPane {
        public CoverFlow(Artist artist) {
            super(new AlbumPanel(artist));
            this.setBounds(280,0,1000,720);
            this.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
            this.getVerticalScrollBar().setUnitIncrement(12);
            this.getVerticalScrollBar().setPreferredSize(new Dimension(0,720));
            this.setBorder(BorderFactory.createEmptyBorder());
            this.setOpaque(false);
            this.getViewport().setOpaque(false);
        }
    }
}
