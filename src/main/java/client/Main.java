package client;

import com.beust.jcommander.JCommander;
import model.Args;

public class Main {
    private final static String SERVER_ADDRESS = "127.0.0.1";
    private final static int    SERVER_PORT    = 23456;

    public static void main(String[] args) {
        var command = new Args();
        JCommander.newBuilder()
                  .addObject(command)
                  .build()
                  .parse(args);
        var client = new Client(SERVER_ADDRESS, SERVER_PORT, command);
        client.connect();
    }
}
