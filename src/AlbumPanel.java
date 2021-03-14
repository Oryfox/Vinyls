import javax.swing.*;
import java.awt.*;

public class AlbumPanel extends JPanel {

    public AlbumPanel() {
        super(new GridLayout(0,(int) Math.floor( (double) (MainFrame.frame.getWidth() - 200) / 210)));
        this.setOpaque(false);

        for (Record record : Record.visibleRecords) {
            this.add(new ItemPanel(record));
        }
    }

    public AlbumPanel(boolean inverted) { //For search and
        super(new GridLayout(0,(int) Math.floor( (double) (MainFrame.frame.getWidth() - 200) / 210)));
        this.setOpaque(false);

        for (Record record : Record.visibleRecords) {
            if(inverted) this.add(new ItemPanel(record),0);
            else this.add(new ItemPanel(record));
        }
    }

    public AlbumPanel(Artist artist) { //For ArtistPanel
        super(new GridLayout(0,(int) Math.floor((double) (MainFrame.frame.getWidth() - 480) / 210)));
        this.setOpaque(false);

        for (Record p : artist.records) {
            this.add(new ItemPanel(p));
        }
    }
}
