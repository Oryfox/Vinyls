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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.awt.geom.RoundRectangle2D;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class SongTable extends JScrollPane {

    public SongTable() {
        this.setBounds(200, 0, 1280, 720);
        this.getVerticalScrollBar().setUnitIncrement(12);
        this.setHorizontalScrollBarPolicy(HORIZONTAL_SCROLLBAR_NEVER);
        this.getVerticalScrollBar().setPreferredSize(new Dimension(0, 720));
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setOpaque(false);

        ArrayList<Song> songs = Record.getAllSongs();

        String[] spaltenNamen = new String[]{Vinyls.bundle.getString("table.songTitle"), Vinyls.bundle.getString("record.artist"), Vinyls.bundle.getString("record.title"), Vinyls.bundle.getString("record.releaseYear"), Vinyls.bundle.getString("table.trackNumber")};
        String[][] data = new String[songs.size()][spaltenNamen.length];

        for (int i = 0; i < songs.size(); i++) {
            data[i][0] = songs.get(i).title;
            data[i][1] = songs.get(i).artist;
            data[i][2] = songs.get(i).albumTitle;
            data[i][3] = Integer.toString(songs.get(i).releaseYear);
            data[i][4] = Integer.toString(songs.get(i).index);
        }

        JTable table = new JTable();
        DefaultTableModel tableModel = new DefaultTableModel(data, spaltenNamen) {
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };
        table.setModel(tableModel);
        table.setOpaque(false);
        ((DefaultTableCellRenderer) table.getDefaultRenderer(Object.class)).setOpaque(false);

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
        table.setRowSorter(sorter);
        ArrayList<RowSorter.SortKey> sortKeys = new ArrayList<>(25);
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.ASCENDING));
        sortKeys.add(new RowSorter.SortKey(1, SortOrder.ASCENDING));
        sortKeys.add(new RowSorter.SortKey(2, SortOrder.ASCENDING));
        sortKeys.add(new RowSorter.SortKey(3, SortOrder.ASCENDING));
        sortKeys.add(new RowSorter.SortKey(4, SortOrder.ASCENDING));
        sorter.setSortKeys(sortKeys);

        JViewport viewport = new JViewport();
        viewport.setView(table);
        viewport.setOpaque(false);
        this.setViewport(viewport);
    }

    public static void exportToCSV(String path, int sort) {
        try {
            Record[] platten = new Record[Record.getCount()];
            int i = 0;
            for (Record record : Record.records) {
                platten[i] = record;
                i++;
            }
            Song[] songs = new Song[Record.getSongCount()];
            //Gehe alle Schallplatten durch
            for (Record platte : platten) {
                //Wenn Platte nicht null
                if (platte != null) {
                    //Wenn Songs nicht null
                    if (platte.songs != null) {
                        //Gehe alle Songs von platte durch
                        for (int j = 0; j < platte.songs.length; j++) {
                            //Suche nach freier Stelle im Array
                            for (i = 0; i < songs.length; i++) {
                                if (songs[i] == null) {
                                    songs[i] = new Song(platte.songs[j].replaceAll(",", "\\."), platte.artist.replaceAll(",", "\\."), platte.title.replaceAll(",", "\\."), platte.releaseYear, (j + 1));
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            switch (sort) {
                case 0:
                    Arrays.sort(songs, (o1, o2) -> {
                        if (o1 != null & o2 != null)
                            return o1.title.toLowerCase().compareTo(o2.title.toLowerCase());
                        return 0;
                    });
                    break;
                case 1:
                    Arrays.sort(songs, (o1, o2) -> {
                        if (o1 != null & o2 != null)
                            return o1.artist.toLowerCase().compareTo(o2.artist.toLowerCase());
                        return 0;
                    });
                    break;
                case 2:
                    Arrays.sort(songs, (o1, o2) -> {
                        if (o1 != null & o2 != null)
                            return o1.albumTitle.toLowerCase().compareTo(o2.albumTitle.toLowerCase());
                        return 0;
                    });
                    break;
                case 3:
                    Arrays.sort(songs, (o1, o2) -> {
                        if (o1 != null & o2 != null)
                            return Integer.toString(o1.releaseYear).compareTo(Integer.toString(o2.releaseYear));
                        return 0;
                    });
                    break;
            }

            String[] spaltenNamen = new String[]{Vinyls.bundle.getString("table.songTitle"), Vinyls.bundle.getString("record.artist"), Vinyls.bundle.getString("record.title"), Vinyls.bundle.getString("record.releaseYear"), Vinyls.bundle.getString("table.trackNumber")};
            String[][] data = new String[songs.length][spaltenNamen.length];

            for (i = 0; i < songs.length; i++) {
                data[i][0] = songs[i].title;
                data[i][1] = songs[i].artist;
                data[i][2] = songs[i].albumTitle;
                data[i][3] = Integer.toString(songs[i].releaseYear);
                data[i][4] = Integer.toString(songs[i].index);
            }

            DefaultTableModel model = new DefaultTableModel(data, spaltenNamen) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    //all cells false
                    return false;
                }
            };

            FileWriter csv = new FileWriter(path + "/Vinyl CSVSheet.csv");

            for (i = 0; i < model.getColumnCount(); i++) {
                if (i == model.getColumnCount() - 1) csv.write(model.getColumnName(i));
                else csv.write(model.getColumnName(i) + ",");
            }

            csv.write("\n");

            for (i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    if (j == model.getColumnCount() - 1) csv.write(model.getValueAt(i, j).toString());
                    else csv.write(model.getValueAt(i, j).toString() + ",");
                }
                csv.write("\n");
            }

            csv.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class CSVExportWindow extends JFrame implements WindowFocusListener {

        final static String[] sortOptions = new String[]{Vinyls.bundle.getString("table.songTitle"), Vinyls.bundle.getString("record.artist"), Vinyls.bundle.getString("record.title"), Vinyls.bundle.getString("record.releaseYear")};

        JComboBox<String> sorterBox;
        JTextField pathSelector;

        public CSVExportWindow() {
            super("CSV Export");
            this.setBounds((Toolkit.getDefaultToolkit().getScreenSize().width / 2) - 150, (Toolkit.getDefaultToolkit().getScreenSize().height / 2) - 100, 300, 220);

            JPanel basePanel = new JPanel(new GridLayout(0, 1)) {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(OryColors.PURPLE);
                    ((Graphics2D) g).fill(new RoundRectangle2D.Double(10, 10, this.getWidth() - 20, this.getHeight() - 20, 25, 25));
                }
            };
            basePanel.setBackground(Color.white);
            basePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            ((GridLayout) basePanel.getLayout()).setVgap(8);

            basePanel.add(sorterBox = dropSorter());
            basePanel.add(pathSelector = pathSelector());
            basePanel.add(saveButton());

            this.add(basePanel);

            this.setVisible(true);
            this.addWindowFocusListener(this);
            basePanel.requestFocus();
        }

        private JComboBox<String> dropSorter() {
            JComboBox<String> dropDown = new JComboBox<>(sortOptions);
            dropDown.setBorder(BorderFactory.createTitledBorder(Vinyls.bundle.getString("table.sortBy")));
            return dropDown;
        }

        private JTextField pathSelector() {
            final JFrame frame = this;
            JTextField textField = new JTextField(System.getProperty("user.home") + (System.getProperty("os.name").toLowerCase().contains("win") ? "\\" : "/") + "Desktop");
            textField.setBorder(BorderFactory.createTitledBorder(Vinyls.bundle.getString("files.location")));
            textField.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    JFileChooser chooser = new JFileChooser();
                    try {
                        chooser = new JFileChooser();
                        chooser.setDialogTitle(Vinyls.bundle.getString("files.selectLocation"));
                        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        chooser.showOpenDialog(MainFrame.frame);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    if (chooser.getSelectedFile() == null) {
                        JOptionPane.showMessageDialog(MainFrame.frame, Vinyls.bundle.getString("files.noSelection"), Vinyls.bundle.getString("error"), JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        textField.setText(chooser.getSelectedFile().getAbsolutePath());
                    }
                    frame.setVisible(true);
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
            return textField;
        }

        private JButton saveButton() {
            JButton button = new JButton(Vinyls.bundle.getString("export"));
            button.addActionListener(e -> {
                exportToCSV(pathSelector.getText(), sorterBox.getSelectedIndex());
                this.setVisible(false);
            });
            return button;
        }

        @Override
        public void windowGainedFocus(WindowEvent e) {

        }

        @Override
        public void windowLostFocus(WindowEvent e) {
            this.setVisible(false);
        }
    }
}
