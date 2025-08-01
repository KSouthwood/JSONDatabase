package server;

import com.google.gson.Gson;
import model.Args;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class Session extends Thread {
    private final Socket socket;
    private final DatabaseArray databaseArray;
    private final AtomicBoolean running;

    public Session(Socket socket, DatabaseArray databaseArray, AtomicBoolean running) {
        this.socket = socket;
        this.databaseArray = databaseArray;
        this.running = running;
    }

    public void run() {
        try (
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream())
        ) {
            String command = input.readUTF();
            var commandArgs = new Gson().fromJson(command, Args.class);
            var response = databaseArray.processClientRequest(commandArgs);
            output.writeUTF(new Gson().toJson(response));
            if (commandArgs.request().equals("exit")) {
                running.set(false);
            }
            socket.close();
        } catch (IOException e) {
            System.err.println("Error reading from socket: " + e.getMessage());
        }
    }
}
