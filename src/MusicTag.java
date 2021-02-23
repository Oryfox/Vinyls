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
