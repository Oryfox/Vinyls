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
