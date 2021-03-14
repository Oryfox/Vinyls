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


