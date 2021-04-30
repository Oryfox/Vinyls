import javax.swing.*;
import java.awt.*;

public class ScrollPane extends JScrollPane {
    boolean inverted = false;
    public ScrollPane()
    {
        super(new AlbumPanel());
        this.setBounds(217, 15, 1280, 720);
        this.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
        this.getVerticalScrollBar().setPreferredSize(new Dimension(0,720));
        this.getVerticalScrollBar().setUnitIncrement(12);
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setOpaque(false);
        this.getViewport().setOpaque(false);
    }

    public ScrollPane(boolean inverted)
    {
        super(new AlbumPanel(inverted));
        this.inverted = inverted;
        this.setBounds(217, 15, 1280, 720);
        this.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
        this.getVerticalScrollBar().setPreferredSize(new Dimension(0,720));
        this.getVerticalScrollBar().setUnitIncrement(12);
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setOpaque(false);
        this.getViewport().setOpaque(false);
    }
}
