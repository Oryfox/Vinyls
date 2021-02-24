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

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("BusyWait")
public class Vinyls {

    static boolean mac = System.getProperty("os.name").toLowerCase().contains("mac"); //Bonus features due to lack of libraries

    static boolean lowSpecMode = false;

    static File home = new File(System.getProperty("user.home") + "/.vinyls");
    static File lib = new File(home.getAbsolutePath() + "/lib");
    static File oAuthHome = new File(home.getAbsolutePath() + "/oauth");
    static File contentsJSON = new File(home.getAbsolutePath() + "/contents.json");
    static File cover = new File(home.getAbsolutePath() + "/cover");
    static File coverDownsized = new File(cover.getAbsolutePath() + "/downsized");
    static File ffmpeg = new File(lib.getAbsolutePath() + "/ffmpeg");
    static File youtubeDL = new File(lib.getAbsolutePath() + "/youtube-dl");
    static File loginSuccess = new File(lib.getAbsolutePath() + "/loginSuccess.html");
    static File runJar = new File(home.getAbsolutePath() + "/run.jar");
    static File running = new File(home.getAbsolutePath() + "/running");
    static File hopIn = new File(lib.getAbsolutePath() + "/HopIn.jar");
    static File service = new File(System.getProperty("user.home") + "/Library/Services/Schallplatte hinzuf\u00fcgen.workflow");
    static File cache = new File(home.getAbsolutePath() + "/cache");
    static File help = new File(home.getAbsolutePath() + "/help.pdf");

    protected static String youtubeApiKey = "";
    protected static String lastFMApiKey = "";
    protected static String geniusClientID = "";
    protected static String geniusClientAccessToken = "";
    protected static String geniusClientSecret = "";

    static boolean betaFeatures;
    static boolean developer;

    static int nextID = 1001;
    static JSONObject contents;

    static ResourceBundle bundle;

    static String version = "v1.0";
    static String today;

    static ServerSocket serverSocket;
    static final int PORT = 13515;

    static boolean initialized = false;

    static Image icon;

    public static void main(String[] input) {
        if (input.length >= 1) {
            for (String s : input) {
                if (s.equals("activateBetaFeatures")) betaFeatures = true;
                if (s.equals("dev") | s.equals("developer") | s.equals("debug")) developer = true;
                if (s.equals("low")) lowSpecMode = true;
            }
        } //Arguments
        osSettings();

        if (developer) {
            home = new File("developmentData");
            lib = new File(home.getAbsolutePath() + "/lib");
            oAuthHome = new File(home.getAbsolutePath() + "/oauth");
            contentsJSON = new File(home.getAbsolutePath() + "/contents.json");
            cover = new File(home.getAbsolutePath() + "/cover");
            coverDownsized = new File(cover.getAbsolutePath() + "/downsized");
            ffmpeg = new File(lib.getAbsolutePath() + "/ffmpeg");
            youtubeDL = new File(lib.getAbsolutePath() + "/youtube-dl");
            loginSuccess = new File(lib.getAbsolutePath() + "/loginSuccess.html");
            cache = new File(home.getAbsolutePath() + "/cache");
            help = new File(home.getAbsolutePath() + "/help.pdf");
        } //Change to development data set

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                Files.deleteIfExists(running.toPath());
                if (MainFrame.frame != null & contents != null) {
                    if (contents.getJSONObject("WindowSize").getInt("Width") != MainFrame.frame.getWidth() |
                            contents.getJSONObject("WindowSize").getInt("Height") != MainFrame.frame.getHeight()) {
                        saveJSONData();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        })); //Shutdown Hook

        if (Vinyls.mac) new Thread(() -> {
            try {
                Socket socket;
                serverSocket = new ServerSocket(PORT);

                while (true) {
                    socket = serverSocket.accept();

                    while (!initialized) {
                        Thread.sleep(100);
                    }

                    System.out.println("Connection established");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String filePath = reader.readLine().replaceAll("\\?", "");
                    reader.close();
                    System.out.println("Received: " + filePath);
                    socket.close();
                    new RecordCreation(filePath);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start(); //Hop in Server for MacOS

        today = new SimpleDateFormat("dd:MM:yyyy").format(Calendar.getInstance().getTime()); //Getting Today as String

        {
            try {
                bundle = ResourceBundle.getBundle("ResourceBundles/Strings", Locale.getDefault());
            } catch (MissingResourceException ignored) {
                System.out.println("Language not supported! - Using English");
                bundle = ResourceBundle.getBundle("ResourceBundles/Strings", Locale.ENGLISH);
            }
        } //ResourceBundle assignment

        checkAndRepairFiles();
        preload();
        if (!contents.getString("version").equals(version)) updateDatabase();
        if (!betaFeatures) betaFeatures = contents.getBoolean("betaFeatures");

        {
            new MainFrame(contents.getJSONObject("WindowSize").getInt("Width"), contents.getJSONObject("WindowSize").getInt("Height"));
            MainFrame.panel.requestFocus();
        } //Window related stuff

        Record.load();

        {
            VinylOfTheDay.enabled = contents.getBoolean("vinylOfTheDayEnabled");
            VinylOfTheDay.fenster = new VinylOfTheDay();
            if (VinylOfTheDay.enabled && !contents.getString("lastTimeUsed").equals(today)) {
                contents.put("lastTimeUsed", today);
                try {
                    FileWriter writer = new FileWriter(contentsJSON);
                    writer.write(contents.toString());
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Record.records.size() >= 3) VinylOfTheDay.fenster.setVisible(true);
            } //Check whether to show VinylOfTheDay
        } //Vinyl of the day related

        {
            JSONObject apiCredentials = contents.getJSONObject("apiCredentials");
            JSONObject youtube = apiCredentials.getJSONObject("youtube");
            JSONObject lastFM = apiCredentials.getJSONObject("lastFM");
            JSONObject genius = apiCredentials.getJSONObject("genius");

            if ((!genius.getString("clientID").equals("") & !genius.getString("clientSecret").equals("")) | !genius.getString("clientAccessToken").equals(""))
                setGeniusApiCredentials(genius.getString("clientID"), genius.getString("clientSecret"), genius.getString("clientAccessToken"));


            if (!youtube.getString("apiKey").equals(""))
                setYoutubeApiKey(youtube.getString("apiKey"));

            YouTubeDL.init(ffmpeg, youtubeDL); //YouTubeDL filepath

            if (!lastFM.getString("apiKey").equals("")) {
                setLastFMApiKey(lastFM.getString("apiKey"));
            }
        } //API initializations

        initialized = true;

        try {
            //noinspection ResultOfMethodCallIgnored
            running.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        } //Running file

        {
            String title = MainFrame.frame.getTitle();
            MainFrame.frame.setTitle(title + " --- " + bundle.getString("finishedLoading"));
            new Thread(() -> {
                try {
                    Thread.sleep(1500);
                    MainFrame.frame.setTitle(title);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } //Initialization finished Text
    }

    public static void preload() {
        StringBuilder builder = new StringBuilder();
        String line;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(contentsJSON));
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            reader.close();
            contents = new JSONObject(builder.toString());
        } catch (IOException e) {
            e.printStackTrace();
            if (e.getClass().getName().equals("java.io.FileNotFoundException")) {
                Vinyls.saveJSONData();
                preload();
            }
        }
    }

    private static void osSettings() {
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        } else if (System.getProperty("os.name").toLowerCase().contains("win")) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void checkAndRepairFiles() {
        if (!home.exists()) {
            home.mkdirs();
            System.out.println("Created home");
        }
        if (!contentsJSON.exists()) {
            saveJSONData();
            System.out.println("Created contents.json");
        }
        if (!cover.exists()) {
            cover.mkdirs();
            System.out.println("Created cover folder");
        }
        if (!coverDownsized.exists()) {
            coverDownsized.mkdirs();
            System.out.println("Created cover downsized folder");
        }
        if (!lib.exists()) {
            lib.mkdirs();
            System.out.println("Created external library");
        }
        if (!cache.exists()) {
            cache.mkdirs();
            System.out.println("Created cache");
        } else {
            File[] files = cache.listFiles();
            if (files != null) {
                for (File f : files) {
                    try {
                        Files.deleteIfExists(f.toPath());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (!youtubeDL.exists()) {
            try {
                InputStream inputStream = Vinyls.class.getResourceAsStream("lib/youtube-dl");
                FileOutputStream outputStream = new FileOutputStream(youtubeDL);
                byte[] bytes = new byte[16 * 1024];

                int count;
                while ((count = inputStream.read(bytes)) > 0) {
                    outputStream.write(bytes, 0, count);
                }
                inputStream.close();
                outputStream.close();

                System.out.println("Extracted youtube-dl");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (mac) {
            if (!ffmpeg.exists()) {
                try {
                    InputStream inputStream = Vinyls.class.getResourceAsStream("lib/ffmpeg");
                    FileOutputStream outputStream = new FileOutputStream(ffmpeg);
                    byte[] bytes = new byte[16 * 1024];

                    int count;
                    while ((count = inputStream.read(bytes)) > 0) {
                        outputStream.write(bytes, 0, count);
                    }
                    inputStream.close();
                    outputStream.close();

                    ProcessBuilder builder = new ProcessBuilder("chmod", "a+x", ffmpeg.getAbsolutePath());
                    builder.start();

                    System.out.println("Extracted ffmpeg and made it executable");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (!hopIn.exists()) {
                try {
                    InputStream inputStream = Vinyls.class.getResourceAsStream("lib/HopIn.jar");
                    FileOutputStream outputStream = new FileOutputStream(hopIn);
                    byte[] bytes = new byte[16 * 1024];

                    int count;
                    while ((count = inputStream.read(bytes)) > 0) {
                        outputStream.write(bytes, 0, count);
                    }
                    inputStream.close();
                    outputStream.close();

                    System.out.println("Extracted HopIn Jar");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (!service.exists()) {
                try {
                    service.mkdirs();
                    File contents = new File(service.getAbsolutePath() + "/Contents");
                    contents.mkdirs();
                    File quickLook = new File(contents.getAbsolutePath() + "/QuickLook");
                    quickLook.mkdirs();


                    InputStream inputStream = Vinyls.class.getResourceAsStream("lib/Schallplatte hinzuf\u00fcgen.workflow/Contents/QuickLook/Thumbnail.png");
                    FileOutputStream outputStream = new FileOutputStream(quickLook.getAbsolutePath() + "/Thumbnail.png");
                    byte[] bytes = new byte[16 * 1024];

                    int count;
                    while ((count = inputStream.read(bytes)) > 0) {
                        outputStream.write(bytes, 0, count);
                    }
                    inputStream.close();
                    outputStream.close();

                    inputStream = Vinyls.class.getResourceAsStream("lib/Schallplatte hinzuf\u00fcgen.workflow/Contents/document.wflow");
                    outputStream = new FileOutputStream(contents.getAbsolutePath() + "/document.wflow");
                    bytes = new byte[16 * 1024];

                    while ((count = inputStream.read(bytes)) > 0) {
                        outputStream.write(bytes, 0, count);
                    }
                    inputStream.close();
                    outputStream.close();

                    inputStream = Vinyls.class.getResourceAsStream("lib/Schallplatte hinzuf\u00fcgen.workflow/Contents/Info.plist");
                    outputStream = new FileOutputStream(contents.getAbsolutePath() + "/Info.plist");
                    bytes = new byte[16 * 1024];

                    while ((count = inputStream.read(bytes)) > 0) {
                        outputStream.write(bytes, 0, count);
                    }
                    inputStream.close();
                    outputStream.close();

                    System.out.println("Extracted Service");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (System.getProperty("os.name").toLowerCase().contains("win")) {
            SongActions.ffmpegExists = new File(ffmpeg.getAbsolutePath() + ".exe").exists();
            ffmpeg = new File(lib.getAbsolutePath() + "/ffmpeg.exe");
        }
        if (!loginSuccess.exists()) {
            try {
                FileWriter writer = new FileWriter(loginSuccess);
                writer.write("<html>\n" +
                        "<head>\n" +
                        "<title>Borealis Vinyls</title>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "<h1>Login successful!</h1>\n" +
                        "<h2>You can close this tab now</h2>\n" +
                        "</body>\n" +
                        "</html>\n");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Created login success");
        }
        if (!help.exists()) {
            try {
                InputStream inputStream = Vinyls.class.getResourceAsStream("help.pdf");
                FileOutputStream outputStream = new FileOutputStream(help);
                byte[] bytes = new byte[16 * 1024];

                int count;
                while ((count = inputStream.read(bytes)) > 0) {
                    outputStream.write(bytes, 0, count);
                }
                inputStream.close();
                outputStream.close();

                System.out.println("Extracted help file");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (!runJar.exists()) {
            try {
                Files.copy(new File(Vinyls.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toPath(), runJar.toPath());
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void saveJSONData() {
        try {
            JSONObject root = new JSONObject();
            root.put("nextID", nextID);
            root.put("count", Record.records.size());
            root.put("version", version);
            root.put("lastTimeUsed", today);

            JSONObject windowSize = new JSONObject();
            if (MainFrame.frame != null) {
                windowSize.put("Width", MainFrame.frame.getWidth());
                windowSize.put("Height", MainFrame.frame.getHeight());
            } else {
                windowSize.put("Width", 1480);
                windowSize.put("Height", 741);
            }

            JSONArray array = new JSONArray();
            JSONObject aktuell;
            JSONArray songs;

            for (Record currentRecord : Record.records) {
                if (currentRecord != null) {
                    aktuell = new JSONObject();
                    aktuell.put("id", currentRecord.id);
                    aktuell.put("title", currentRecord.title);
                    aktuell.put("artist", currentRecord.artist);
                    aktuell.put("releaseYear", currentRecord.releaseYear);
                    aktuell.put("color", currentRecord.color);
                    aktuell.put("limited", currentRecord.limited);
                    aktuell.put("bootleg", currentRecord.bootleg);
                    aktuell.put("favorite", currentRecord.favorite);

                    if (currentRecord.songs != null) {
                        songs = new JSONArray(currentRecord.songs);
                    } else {
                        songs = new JSONArray();
                    }
                    aktuell.put("songs", songs);

                    array.put(aktuell);
                }
            }

            root.put("WindowSize", windowSize);
            root.put("database", array);

            JSONObject apiKeys = new JSONObject();
            JSONObject genius = new JSONObject();
            if (contents != null) {

                genius.put("clientID", geniusClientID);
                genius.put("clientSecret", geniusClientSecret);
                genius.put("clientAccessToken", geniusClientAccessToken);
                apiKeys.put("genius", genius);

                JSONObject youtube = new JSONObject();
                youtube.put("apiKey", youtubeApiKey);
                apiKeys.put("youtube", youtube);

                JSONObject lastFM = new JSONObject();
                lastFM.put("apiKey", lastFMApiKey);
                apiKeys.put("lastFM", lastFM);
            } else {
                genius.put("clientID", "");
                genius.put("clientSecret", "");
                genius.put("clientAccessToken", "");
                apiKeys.put("genius", genius);

                JSONObject youtube = new JSONObject();
                youtube.put("apiKey", "");
                apiKeys.put("youtube", youtube);

                JSONObject lastFM = new JSONObject();
                lastFM.put("apiKey", "");
                apiKeys.put("lastFM", lastFM);
            }
            root.put("apiCredentials", apiKeys);
            root.put("vinylOfTheDayEnabled", VinylOfTheDay.enabled);
            root.put("betaFeatures", Vinyls.betaFeatures);

            FileWriter writer = new FileWriter(contentsJSON);
            writer.write(root.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getNextID() {
        int back = nextID;
        nextID++;
        return back;
    }

    public static void updateDatabase() {
        switch (contents.getString("version")) {
            case "v0.0": {
                ArrayList<Record> list = new ArrayList<>();
                JSONArray database = contents.getJSONArray("database");
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

                    list.add(new Record(
                            aktuell.getInt("id"), //ID
                            aktuell.getString("title"), //Title
                            aktuell.getString("artist"), //Artist
                            aktuell.getInt("releaseYear"), //ReleaseYear
                            aktuell.getString("color"), //Color
                            aktuell.getBoolean("limited"), //Limited
                            aktuell.getBoolean("bootleg"),
                            false,//Bootleg
                            songs //Songs as StringArray
                    ));
                }

                JSONObject root = new JSONObject();
                root.put("nextID", contents.getInt("nextID"));
                root.put("count", list.size());
                root.put("version", "v0.1");

                database = new JSONArray();
                JSONArray songsJSON;

                for (Record record : list) {
                    if (record != null) {
                        aktuell = new JSONObject();
                        aktuell.put("id", record.id);
                        aktuell.put("title", record.title);
                        aktuell.put("artist", record.artist);
                        aktuell.put("releaseYear", record.releaseYear);
                        aktuell.put("color", record.color);
                        aktuell.put("limited", record.limited);
                        aktuell.put("bootleg", record.bootleg);
                        aktuell.put("favorite", record.favorite);

                        if (record.songs != null) {
                            songsJSON = new JSONArray(record.songs);
                        } else {
                            songsJSON = new JSONArray();
                        }
                        aktuell.put("songs", songsJSON);

                        database.put(aktuell);
                    }
                }

                root.put("database", database);

                try {
                    FileWriter writer = new FileWriter(contentsJSON);
                    writer.write(root.toString());
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                contents = root;
            }
            break;
            case "v0.1": {
                SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
                SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
                SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");

                int day = Integer.parseInt(dayFormat.format(Calendar.getInstance().getTime()));
                int month = Integer.parseInt(monthFormat.format(Calendar.getInstance().getTime()));
                int year = Integer.parseInt(yearFormat.format(Calendar.getInstance().getTime()));
                contents.put("lastTimeUsed", (day - 1) + ":" + month + ":" + year);
                contents.put("version", "v0.2");

                try {
                    FileWriter writer = new FileWriter(contentsJSON);
                    writer.write(contents.toString());
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            break;
            case "v0.2": {
                JSONObject o = new JSONObject();
                o.put("Width", 1480);
                o.put("Height", 720);

                contents.put("WindowSize", o);
                contents.put("version", "v0.3");

                try {
                    FileWriter writer = new FileWriter(contentsJSON);
                    writer.write(contents.toString());
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            break;
            case "v0.3": {
                contents.put("version", "v0.4");

                try {
                    FileWriter writer = new FileWriter(contentsJSON);
                    writer.write(contents.toString());
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            break;
            case "v0.4": {
                contents.put("version", "v0.4.1");

                try {
                    FileWriter writer = new FileWriter(contentsJSON);
                    writer.write(contents.toString());
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            break;
            case "v0.4.1": {
                contents.put("version", "v0.4.2");

                try {
                    FileWriter writer = new FileWriter(contentsJSON);
                    writer.write(contents.toString());
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            break;
            case "v0.4.2": {
                contents.put("version", "v0.5");

                try {
                    FileWriter writer = new FileWriter(contentsJSON);
                    writer.write(contents.toString());
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            break;
            case "v0.5":
                contents.put("version", "v0.5.1");
                JSONObject apiKeys = new JSONObject();

                JSONObject genius = new JSONObject();
                genius.put("clientID", "");
                genius.put("clientSecret", "");
                genius.put("clientAccessToken", "");
                apiKeys.put("genius", genius);

                JSONObject youtube = new JSONObject();
                youtube.put("apiKey", "");
                apiKeys.put("youtube", youtube);

                JSONObject lastFM = new JSONObject();
                lastFM.put("apiKey", "");
                apiKeys.put("lastFM", lastFM);

                contents.put("apiCredentials", apiKeys);

                try {
                    FileWriter writer = new FileWriter(contentsJSON);
                    writer.write(contents.toString());
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "v0.5.1": {
                contents.put("version", "v0.6");
                if (Vinyls.mac) {
                    updateJar();
                    System.out.println("Updated jar file");
                }
                contents.put("vinylOfTheDayEnabled", true);
                contents.put("betaFeatures", false);

                try {
                    FileWriter writer = new FileWriter(contentsJSON);
                    writer.write(contents.toString());
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            break;
            case "v0.6": {
                contents.put("version", "v1.0");
                try {
                    FileWriter writer = new FileWriter(contentsJSON);
                    writer.write(contents.toString());
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (!contents.getString("version").equals(version)) updateDatabase();
    }

    public static void updateJar() {
        try {
            Files.delete(runJar.toPath());
            Files.copy(new File(Vinyls.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toPath(), runJar.toPath());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    protected static void setYoutubeApiKey(String apiKey) {
        youtubeApiKey = apiKey;
        YouTube.init(apiKey);
    }

    protected static void setLastFMApiKey(String apiKey) {
        lastFMApiKey = apiKey;
        LastFM.init(apiKey);
    }

    protected static void setGeniusApiCredentials(String clientID, String clientSecret, String clientAccessToken) {
        geniusClientID = clientID;
        geniusClientSecret = clientSecret;
        geniusClientAccessToken = clientAccessToken;
        if (geniusClientID.equals("") && geniusClientSecret.equals("") && geniusClientAccessToken.equals("")) Genius2.genius = null;
        else Genius2.genius = new Genius2(geniusClientID, geniusClientSecret, geniusClientAccessToken, "http://localhost:8888", oAuthHome.getAbsolutePath(), contents.toString(), loginSuccess);
        MainFrame.basePanel.remove(Sidebar.panel);
        MainFrame.basePanel.add(Sidebar.panel = new Sidebar(), MainFrame.sidebarConstraints);
        MainFrame.basePanel.updateUI();
    }
}
