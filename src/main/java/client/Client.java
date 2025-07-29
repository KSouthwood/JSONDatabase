package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private final String address;
    private final int    port;

    private DataInputStream  input;
    private DataOutputStream output;

    public Client(String address, int port) {
        this.address = address;
        this.port = port;
        connect();
    }

    public void connect() {
        try (Socket socket = new Socket(InetAddress.getByName(address), port);
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream())
        ) {
            System.out.println("Client started!");
            String sendToServer = "Give me a record # 42";
            output.writeUTF(sendToServer);
            System.out.println("Sent: " + sendToServer);
            String receivedFromServer = input.readUTF();
            System.out.println("Received: " + receivedFromServer);
        } catch (UnknownHostException ignored) {
            System.err.println("Couldn't connect to host at: " + address);
        } catch (IOException e) {
            System.err.println("IO Error occurred: " + e.getMessage());
        }
    }
}
