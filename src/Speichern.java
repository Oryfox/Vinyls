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

import java.io.File;
import java.nio.file.Files;

public class Speichern {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void backup(File ziel) {
        try {
            Files.copy(Vinyls.contentsJSON.toPath(), new File(ziel.getAbsolutePath() + "/contents.json").toPath());
            new File(ziel.getAbsolutePath() + "/cover/downsized").mkdirs();
            for (Record record : Record.records) {
                if (record != null) {
                    Files.copy(new File(Vinyls.cover.getAbsolutePath() + "/" + record.id + ".png").toPath(), new File(ziel.getAbsolutePath() + "/cover/" + record.id + ".png").toPath());
                    Files.copy(new File(Vinyls.coverDownsized.getAbsolutePath() + "/" + record.id + ".png").toPath(),new File(ziel.getAbsolutePath() + "/cover/downsized/" + record.id + ".png").toPath());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


