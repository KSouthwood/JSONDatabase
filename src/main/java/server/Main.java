package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main implements Runnable{
    private final static int SERVER_PORT = 23456;

    public static void main(String[] args) {
        new Main().run();
    }

    @Override
    public void run() {
        DatabaseArray db = new DatabaseArray();

        try (ServerSocket socket = new ServerSocket(SERVER_PORT);
             ExecutorService executor = Executors.newFixedThreadPool(5)) {
            System.out.println("Server started!");
            Boolean running = true;
            while (running) {
                var result = executor.submit(new Session(socket.accept(), db));
                running = result.get();
            }
            executor.shutdown();
        } catch (IOException e) {
            System.err.println("Error starting server: " + e.getMessage());
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
