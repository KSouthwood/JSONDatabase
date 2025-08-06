package client;

import model.Args;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Type;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.FileSystems;

public class Client {
    private final String address;
    private final int    port;
    private final Args   args;

    private final String workingDirectory = System.getProperty("user.dir");
    private final String separator = FileSystems.getDefault()
                                                .getSeparator();
    private final String hsDirectory = "src%1$sclient%1$sdata".formatted(separator);
    private final String dataDirectory = "src%1$smain%1$sjava%1$sclient%1$sdata".formatted(separator);

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
            String[] commandLine = commands(args);
            for (String command : commandLine) {
                output.writeUTF(command);
                System.out.println("Sent: " + command);
                String receivedFromServer = input.readUTF();
                System.out.println("Received: " + receivedFromServer);
            }
        } catch (UnknownHostException ignored) {
            System.err.println("Couldn't connect to host at: " + address);
        } catch (IOException e) {
            System.err.println("IO Error occurred: " + e.getMessage());
        }
    }

    private String[] commands(Args args) {
        if (args.filename() != null) {
            return readFile(args.filename());
        }
        return new String[] { new Gson().toJson(args) };
    }

    private String[] readFile(String filename) {
        File file = getFile(filename);
        if (!file.exists()) {
            System.err.printf("File %s not found in '%s.'%n", filename, file.getParent());
            return new String[0];
        }
        try (Reader reader = new FileReader(file)) {
            Type type = new TypeToken<String[]>() {
            }.getType();
            return new Gson().fromJson(reader, type);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return new String[0];
        }
    }

    private File getFile(String filename) {
        // checks for Hyperskill project directory structure
        File directory = new File(workingDirectory + separator + hsDirectory);
        if (directory.exists()) {
            return new File(directory + separator + filename);
        }
        
        // checks for local project directory structure
        directory = new File(workingDirectory + separator + dataDirectory);
        if (directory.exists()) {
            return new File(directory + separator + filename);
        }
        
        // returns working directory structure
        return new File(workingDirectory + separator + filename);
    }
}
