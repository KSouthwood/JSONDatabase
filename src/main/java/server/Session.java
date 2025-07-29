package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Session extends Thread {
    private final Socket socket;

    public Session(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        try (
                DataInputStream input = new DataInputStream(socket.getInputStream());
                DataOutputStream output = new DataOutputStream(socket.getOutputStream())
        ) {
            String msg = input.readUTF();
            System.out.println("Received: " + msg);
            String index = msg.substring(msg.indexOf("#") + 2);
            String response = "A record # " + index + " was sent!";
            output.writeUTF(response);
            System.out.println("Sent: " + response);
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
