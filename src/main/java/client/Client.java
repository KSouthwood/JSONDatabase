package client;

import com.beust.jcommander.Parameter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    private final String address;
    private final int    port;

    @Parameter(names = "-t", description = "Request type (get, set, delete, exit)")
    private final String request = "";

    @Parameter(names = "-i", description = "Index of the record to operate on")
    private final String index = "";

    @Parameter(names = "-m", description = "Message to store on the server")
    private final String message = "";

    public Client(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public void connect() {
        try (Socket socket = new Socket(InetAddress.getByName(address), port);
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream())
        ) {
            System.out.println("Client started!");
            String commandLine = String.join(" ", request, index, message);
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
