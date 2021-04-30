public class Song {

    String title;
    String artist;
    String albumTitle;
    int releaseYear;
    int index;

    public Song(String name, String artist, String albumTitle, int releaseYear, int index) {
        this.title = name;
        this.artist = artist;
        this.albumTitle = albumTitle;
        this.releaseYear = releaseYear;
        this.index = index;
    }
}
