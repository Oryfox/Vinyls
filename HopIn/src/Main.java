import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class Main {

    static File running = new File(System.getProperty("user.home") + "/.vinyls/running");
    static File runJar = new File(System.getProperty("user.home") + "/.vinyls/run.jar");

    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length == 1) {
            if (running.exists()) {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(InetAddress.getLocalHost(), 13515), 1000);
                PrintWriter writer = new PrintWriter(socket.getOutputStream());
                writer.println(args[0]);
                writer.close();
                socket.close();
            } else {
                ProcessBuilder builder = new ProcessBuilder("java", "-jar", runJar.getAbsolutePath());
                builder.start();


                int counter = 0;
                while (!running.exists()) {
                    counter += 200;
                    //noinspection BusyWait
                    Thread.sleep(200);
                    if (counter >= 60000) System.exit(1);
                }

                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(InetAddress.getLocalHost(), 13515), 1000);
                PrintWriter writer = new PrintWriter(socket.getOutputStream());
                writer.println(args[0]);
                writer.close();
                socket.close();
            }
            System.exit(0);
        }
    }
}