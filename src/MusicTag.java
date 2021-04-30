import com.mpatric.mp3agic.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MusicTag {

    Mp3File mp3File;
    ID3v2 tag;

    String fileLocation;

    public MusicTag(String fileLocation) throws InvalidDataException, IOException, UnsupportedTagException {
        this.fileLocation = fileLocation;
        mp3File = new Mp3File(fileLocation);
        if (mp3File.hasId3v1Tag()) {
            mp3File.removeId3v1Tag();
        }
        if (mp3File.hasCustomTag()) {
            mp3File.removeCustomTag();
        }
        if (mp3File.hasId3v2Tag()) {
            tag = mp3File.getId3v2Tag();
        } else {
            tag = new ID3v23Tag();
            mp3File.setId3v2Tag(tag);
        }
    }

    public void save() throws IOException, NotSupportedException {
        mp3File.save(fileLocation.replaceAll("\\suntagged",""));
    }

    public void setTitle(String title) {
        tag.setTitle(title);
    }

    public void setTrack(String track) {
        tag.setTrack(track);
    }

    public void setArtist(String artist) {
        tag.setArtist(artist);
    }

    public void setYear(String year) {
        tag.setYear(year);
    }

    public void setCoverArt(String albumImageLocation) throws IOException {
        File file = new File(albumImageLocation);
        byte[] bytes = new byte[(int)file.length()];
        FileInputStream fileIn = new FileInputStream(file);
        //noinspection ResultOfMethodCallIgnored
        fileIn.read(bytes);
        fileIn.close();
        tag.setAlbumImage(bytes,"image/png");
    }
}
