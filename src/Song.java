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
