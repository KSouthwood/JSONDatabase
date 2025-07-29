package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicBoolean;

public class Main {
    private final static int SERVER_PORT = 23456;

    public static void main(String[] args) {
        DatabaseArray db = new DatabaseArray();
        try (ServerSocket socket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Server started!");
            AtomicBoolean running = new AtomicBoolean(true);
            while (running.get())
            {
                Session session = new Session(socket.accept(), db, running);
                session.start();
            }
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        }
    }
}
