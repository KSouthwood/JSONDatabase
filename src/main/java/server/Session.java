package server;

import com.google.gson.Gson;
import model.Args;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.Callable;

public class Session implements Callable<Boolean> {
    private final Socket socket;
    private final DatabaseArray databaseArray;

    public Session(Socket socket, DatabaseArray databaseArray) {
        this.socket = socket;
        this.databaseArray = databaseArray;
    }

    @Override
    public Boolean call() {
        try (
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream())
        ) {
            String command = input.readUTF();
            var commandArgs = new Gson().fromJson(command, Args.class);
            var response = databaseArray.processClientRequest(commandArgs);
            output.writeUTF(new Gson().toJson(response));
            socket.close();
            if (commandArgs.request().equals("exit")) {
                return false;
            }
        } catch (IOException e) {
            System.err.println("Error reading from socket: " + e.getMessage());
        }
        return true;
    }
}
