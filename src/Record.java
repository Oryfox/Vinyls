import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Record {

    protected static List<Record> records = new ArrayList<>();
    public static List<Record> visibleRecords = new ArrayList<>();

    protected int id;
    protected String title;
    protected String artist;
    protected int releaseYear;
    protected String color;
    protected boolean limited;
    protected boolean bootleg;
    protected boolean favorite;
    protected ImageIcon cover;
    protected ImageIcon miniCover;
    protected String[] songs;

    ItemPanel itemPanel;

    static final ImageIcon placeHolder = createPlaceHolder();

    public Record(int id, String title, String artist, int releaseYear, String color, boolean limited, boolean bootleg, boolean favorite, String[] songs) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.releaseYear = releaseYear;
        this.color = color;
        this.limited = limited;
        this.bootleg = bootleg;
        this.favorite = favorite;
        if (!Vinyls.lowSpecMode) this.miniCover = new ImageIcon(Vinyls.coverDownsized.getAbsolutePath() + "/" + id + ".png");
        else this.miniCover = placeHolder;
        this.songs = songs;
    }

    //Adds record to the records list
    protected static void add(Record neu) {
        records.add(neu);
    }

    //Adds record to the records list and automatically adds to interface. Used at startup.
    protected static void addAtStartup(Record neu) {
        records.add(neu);
        visibleRecords.add(neu);
        ((AlbumPanel) ((ScrollPane) MainFrame.panel).getViewport().getView()).add(new ItemPanel(neu),0);
        SwingUtilities.updateComponentTreeUI(((ScrollPane) MainFrame.panel).getViewport().getView());
    }

    //Removed a record from all lists by id
    public static void remove(int id) {
        records.removeIf(record -> record.id == id);
        visibleRecords.removeIf(record -> record.id == id);
    }

    //Loads database
    public static void load() {
        records.clear();
        visibleRecords.clear();

        JSONObject root = Vinyls.contents;
        Vinyls.nextID = root.getInt("nextID");
        JSONArray database = root.getJSONArray("database");

        JSONObject aktuell;
        JSONArray songsArray;
        String[] songs;
        for (int i = 0; i < database.length(); i++) {
            aktuell = database.getJSONObject(i);
            songsArray = aktuell.getJSONArray("songs");
            songs = new String[songsArray.length()];

            for (int j = 0; j < songsArray.length(); j++) {
                songs[j] = songsArray.getString(j);
            }

            addAtStartup(new Record(
                    aktuell.getInt("id"), //ID
                    aktuell.getString("title"), //Title
                    aktuell.getString("artist"), //Artist
                    aktuell.getInt("releaseYear"), //ReleaseYear
                    aktuell.getString("color"), //Color
                    aktuell.getBoolean("limited"), //Limited
                    aktuell.getBoolean("bootleg"),
                    aktuell.getBoolean("favorite"),//Bootleg
                    songs //Songs as StringArray
            ));
        }
    }

    //Returns count of all records
    public static int getCount() {
        return records.size();
    }

    //Returns count of all songs
    public static int getSongCount() {
        int back = 0;

        for (Record record : records) {
            back += record.getThisSongCount();
        }

        return back;
    }

    //Returns count of songs of one album
    public int getThisSongCount() {
        int back = 0;
        if (songs != null) {
            for (String s : songs) {
                if (s != null) back++;
            }
        }
        return back;
    }

    //Returns all songs
    public static ArrayList<Song> getAllSongs() {
        ArrayList<Song> songs = new ArrayList<>();

        //Gehe alle Schallplatten durch
        for (Record record : records) {
            //Wenn Songs nicht null
            if (record.songs != null) {
                //Gehe alle Songs von platte durch
                for (int j = 0; j < record.songs.length; j++) {
                    songs.add(new Song(record.songs[j], record.artist, record.title, record.releaseYear, (j + 1)));
                }
            }
        }

        return songs;
    }

    //Returns count of all artists
    public static int getArtistCount() {
        ArrayList<String> names = new ArrayList<>();

        for (Record record : records) {
            if (!names.contains(record.artist))
                names.add(record.artist);
        }

        return names.size();
    }

    //Returns all artist names
    public static ArrayList<String> getArtists() {
        ArrayList<String> artists = new ArrayList<>();

        for (Record record : visibleRecords) {
            if (!artists.contains(record.artist))
                artists.add(record.artist);
        }

        return artists;
    }

    //Returns all records of an artist
    public static ArrayList<Record> getArtistsRecords(String name) {
        ArrayList<Record> back = new ArrayList<>();
        for (Record record : visibleRecords) {
            if (record.artist.equalsIgnoreCase(name)) {
                back.add(record);
            }
        }

        return back;
    }

    //Fills the visibleRecords list with records containing the search term
    public static void search(String searchTerm) {
        if (searchTerm.equals("")) {
            visibleRecords = new ArrayList<>(records);
            if (Sidebar.sortByTitle) {
                Record.sort(1);
            }
        } else {
            visibleRecords = records.stream().filter(record -> record.title.toLowerCase().contains(searchTerm.toLowerCase())
                    | record.artist.toLowerCase().contains(searchTerm.toLowerCase())).collect(Collectors.toList());
        }
        if (MainFrame.panel instanceof ScrollPane) {
            ((ScrollPane) MainFrame.panel).setViewportView(new AlbumPanel(((ScrollPane) MainFrame.panel).inverted & searchTerm.equals("")));
            MainFrame.panel.setBorder(BorderFactory.createEmptyBorder());
        }
    }

    //Replaces a record for editing purpose
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void edit(int id, Record newRecord) {
        if (id == newRecord.id) {
            for (Record record : records) {
                if (record.id == id) {
                    Collections.replaceAll(records, record, newRecord);
                    Collections.replaceAll(visibleRecords, record, newRecord);
                }
            }
        }
    }

    //Sorts the visibleRecord list using Arrays.sort and converting - HAS TO BE IMPROVED
    public static void sort(int by) { //0 = Artist, 1 = Title
        switch (by) {
            case 0:
                Record.visibleRecords.sort((o1, o2) -> {
                    if (o1 != null & o2 != null)
                        return o1.artist.toLowerCase().compareTo(o2.artist.toLowerCase());
                    return 0;
                });
                break;
            case 1:
                Record.visibleRecords.sort((o1, o2) -> {
                    if (o1 != null & o2 != null)
                        return o1.title.toLowerCase().compareTo(o2.title.toLowerCase());
                    return 0;
                });
                break;
            default:
                Record.visibleRecords = new ArrayList<>(Record.records);
                break;
        }
    }

    private static ImageIcon createPlaceHolder() {
        BufferedImage bi = new BufferedImage(180,180,BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.setColor(Color.darkGray);
        g.drawString("Low Spec Mode: ",0,90);
        g.drawString("Image not loaded",0,100);
        g.dispose();
        return new ImageIcon(bi);
    }
}
