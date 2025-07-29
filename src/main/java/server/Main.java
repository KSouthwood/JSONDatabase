package server;

import java.io.IOException;
import java.net.ServerSocket;

public class Main {
    private final static int SERVER_PORT = 23456;

    public static void main(String[] args) {
        try (ServerSocket socket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Server started!");
            Session session = new Session(socket.accept());
            session.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
