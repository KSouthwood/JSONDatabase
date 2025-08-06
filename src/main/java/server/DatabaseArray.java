package server;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.Args;
import model.ServerResponse;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Implements a simple in-memory key-value database using a HashMap to store string values. Supports basic operations
 * like set, get, and delete through a command-line interface. The implementation is thread-safe for concurrent access.
 */
public class DatabaseArray {
    private static final String ERROR = "ERROR";
    private static final String OK    = "OK";

    private static final String COMMAND_SET    = "set";
    private static final String COMMAND_GET    = "get";
    private static final String COMMAND_DELETE = "delete";
    private static final String COMMAND_EXIT   = "exit";

    private static final File DB_FILE = new File(System.getProperty("user.dir") + "/src/main/java/server/data/db.json");
//    private static final File DB_FILE = new File(System.getProperty("user.dir") + "/src/server/data/db.json");

    private final ReentrantReadWriteLock           lock      = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock  readLock  = lock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    private final Map<String, String> database;
    private final Gson                gson = new Gson();

    public DatabaseArray() {
        database = new HashMap<>();
        loadDatabase();
    }

    private void loadDatabase() {
        if (checkFileExists()) {
            return;
        }
        try (FileReader reader = new FileReader(DB_FILE)) {
            Gson gson = new Gson();
            Type type = new TypeToken<HashMap<String, String>>() {
            }.getType();
            Map<String, String> loaded = gson.fromJson(reader, type);
            if (loaded != null) {
                database.putAll(loaded);
            }
        } catch (Exception e) {
            System.err.println("Error loading database: " + e.getMessage());
        }
    }

    private boolean checkFileExists() {
        if (!DB_FILE.exists()) {
            try {
                var ignored = DB_FILE.getParentFile().mkdirs();
                if (!DB_FILE.createNewFile()) {
                    System.err.println("File already exists: " + DB_FILE);
                    return false;
                }
                return true;
            } catch (Exception e) {
                System.err.println("Error creating file: " + e.getMessage());
            }
        }
        return false;
    }

    /**
     * Processes incoming client requests and executes the corresponding database operation.
     *
     * @param commandLine the command arguments containing a request type, key and value
     * @return ServerResponse object containing the operation result and optional value/error message
     */
    public ServerResponse processClientRequest(final Args commandLine) {
        return switch (commandLine.request()) {
            case COMMAND_SET -> set(commandLine);
            case COMMAND_GET -> get(commandLine);
            case COMMAND_DELETE -> delete(commandLine);
            case COMMAND_EXIT -> new ServerResponse().response(OK);
            default -> new ServerResponse().response(ERROR)
                                           .reason("Invalid command");
        };
    }

    /**
     * Associates the specified value with the specified key in the database. Command format: "set -k key -v value"
     *
     * @param commandLine Args object containing the key and value to store
     * @return ServerResponse indicating success or failure of the operation
     */
    private ServerResponse set(Args commandLine) {
        if (commandLine.key() == null || commandLine.value() == null) {
            return new ServerResponse().response(ERROR)
                                       .reason("Request should have a key and a value to be valid.");
        }
        database.put(commandLine.key(), commandLine.value());
        writeDatabaseToFile();
        return new ServerResponse().response(OK);
    }

    /**
     * Retrieves the value associated with the specified key from the database. Command format: "get -k key"
     *
     * @param commandLine Args object containing the key to retrieve
     * @return ServerResponse containing the retrieved value or error message if key not found
     */
    private ServerResponse get(Args commandLine) {
        var response = new ServerResponse();
        readLock.lock();
        try {
            response = (commandLine.key() == null || !database.containsKey(commandLine.key()))
                       ?
                       response.response(ERROR)
                               .reason("No such key")
                       :
                       response.response(OK)
                               .value(database.get(commandLine.key()));
        } finally {
            readLock.unlock();
        }
        return response;
    }

    /**
     * Removes the entry for the specified key from the database if present. Command format: "delete -k key"
     *
     * @param commandLine Args object containing the key to delete
     * @return ServerResponse indicating success or failure of the operation
     */
    private ServerResponse delete(Args commandLine) {
        if (commandLine.key() == null || !database.containsKey(commandLine.key())) {
            return new ServerResponse().response(ERROR)
                                       .reason("No such key");
        }
        database.remove(commandLine.key());
        writeDatabaseToFile();
        return new ServerResponse().response(OK);
    }

    private void writeDatabaseToFile() {
        writeLock.lock();
        try (FileWriter writer = new FileWriter(DB_FILE)) {
            gson.toJson(database, writer);
        } catch (Exception e) {
            System.err.println("Error saving database: " + e.getMessage());
        } finally {
            writeLock.unlock();
        }
    }
}
