package server;

import model.Args;
import model.ServerResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * Implements a simple in-memory key-value database using a HashMap to store string values. 
 * Supports basic operations like set, get, and delete through command-line interface.
 * The implementation is thread-safe for concurrent access.
 */
public class DatabaseArray {
    private static final String ERROR = "ERROR";
    private static final String OK = "OK";

    private static final String COMMAND_SET = "set";
    private static final String COMMAND_GET = "get";
    private static final String COMMAND_DELETE = "delete";
    private static final String COMMAND_EXIT = "exit";

    private final Map<String, String> database;

    public DatabaseArray() {
        database = new HashMap<>();
    }

    /**
     * Processes incoming client requests and executes the corresponding database operation.
     *
     * @param commandLine the command arguments containing request type, key and value
     * @return ServerResponse object containing the operation result and optional value/error message
     */
    public ServerResponse processClientRequest(final Args commandLine) {
        return switch (commandLine.request()) {
            case COMMAND_SET -> set(commandLine);
            case COMMAND_GET -> get(commandLine);
            case COMMAND_DELETE -> delete(commandLine);
            case COMMAND_EXIT -> new ServerResponse().response(OK);
            default -> new ServerResponse().response(ERROR).reason("Invalid command");
        };
    }

    /**
     * Associates the specified value with the specified key in the database.
     * Command format: "set -k key -v value"
     *
     * @param commandLine Args object containing the key and value to store
     * @return ServerResponse indicating success or failure of the operation
     */
    ServerResponse set(Args commandLine) {
        if (commandLine.key() == null || commandLine.value() == null) {
            return new ServerResponse().response(ERROR).reason("Request should have a key and a value to be valid.");
        }
        database.put(commandLine.key(), commandLine.value());
        return new ServerResponse().response(OK);
    }

    /**
     * Retrieves the value associated with the specified key from the database.
     * Command format: "get -k key"
     *
     * @param commandLine Args object containing the key to retrieve
     * @return ServerResponse containing the retrieved value or error message if key not found
     */
    ServerResponse get(Args commandLine) {
        if (commandLine.key() == null || !database.containsKey(commandLine.key())) {
            return new ServerResponse().response(ERROR).reason("No such key");
        }
        return new ServerResponse().response(OK).value(database.get(commandLine.key()));
    }

    /**
     * Removes the entry for the specified key from the database if present.
     * Command format: "delete -k key"
     *
     * @param commandLine Args object containing the key to delete
     * @return ServerResponse indicating success or failure of the operation
     */
    ServerResponse delete(Args commandLine) {
        if (commandLine.key() == null || !database.containsKey(commandLine.key())) {
            return new ServerResponse().response(ERROR).reason("No such key");
        }
        database.remove(commandLine.key());
        return new ServerResponse().response(OK);
    }
}
