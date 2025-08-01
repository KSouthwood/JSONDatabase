package client;

import model.Args;

import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private final String address;
    private final int    port;
    private final Args   args;

    public Client(String address, int port, Args args) {
        this.address = address;
        this.port = port;
        this.args = args;
    }

    public void connect() {
        try (Socket socket = new Socket(InetAddress.getByName(address), port);
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream())
        ) {
            System.out.println("Client started!");
            String commandLine = new Gson().toJson(args);
            output.writeUTF(commandLine);
            System.out.println("Sent: " + commandLine);
            String receivedFromServer = input.readUTF();
            System.out.println("Received: " + receivedFromServer);
        } catch (UnknownHostException ignored) {
            System.err.println("Couldn't connect to host at: " + address);
        } catch (IOException e) {
            System.err.println("IO Error occurred: " + e.getMessage());
        }
    }
}
