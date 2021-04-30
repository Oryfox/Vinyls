import java.util.ArrayList;
import java.util.List;

public class Artist {

    static Artist[] artists;
    String name;
    List<Record> records;

    public Artist(String name, ArrayList<Record> records)
    {
        this.name = name;
        this.records = records;
    }

    public static void build()
    {
        Record.sort(0);

        artists = new Artist[Record.getArtistCount()];
        ArrayList<String> artistNames = Record.getArtists();

        for (String artistName : artistNames) {
            artists[artistNames.indexOf(artistName)] = new Artist(artistName, Record.getArtistsRecords(artistName));
        }
    }
}
