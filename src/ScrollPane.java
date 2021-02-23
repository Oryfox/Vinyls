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
