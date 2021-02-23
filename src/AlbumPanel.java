/*Vinyls - Java software to manage vinyl records by collecting their attributes, cover arts and enjoying various other features.
    Copyright (C) 2021  Semih Kaiser

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.*/

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
