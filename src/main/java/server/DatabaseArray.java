package server;

import java.util.Arrays;

/**
 * Implements a simple in-memory database using an array to store string values. Supports basic operations like set,
 * get, and delete through a command-line interface.
 */
public class DatabaseArray {
    private static final int MAX_SIZE = 1000;

    private static final String ERROR = "ERROR";
    private static final String OK = "OK";

    private static final String COMMAND_SET = "set";
    private static final String COMMAND_GET = "get";
    private static final String COMMAND_DELETE = "delete";
    private static final String COMMAND_EXIT = "exit";

    private final String[] database = new String[MAX_SIZE];

    public DatabaseArray() {
        Arrays.fill(database, "");
    }

    /**
     * Parses and executes a command based on the given command line input. The method splits the input into components
     * and determines the operation (set, get, or delete) to perform on the database. If the command is invalid,
     * an error message is returned.
     *
     * @param commandLine the full command line string to be parsed and executed. The format of the command is as follows:
     *                    - "set <index> <value>" to set a value at a specific index,
     *                    - "get <index>" to retrieve a value from a specific index,
     *                    - "delete <index>" to delete a value at a specific index.
     * @return the result of the command execution as a string. Returns "OK" for successful operations,
     *         "ERROR" for invalid commands or errors during execution.
     */
    public String parseCommandLine(final String commandLine) {
        var command = commandLine.split(" ", 3);
        return switch (command[0].toLowerCase()) {
            case COMMAND_SET -> set(command);
            case COMMAND_GET -> get(command);
            case COMMAND_DELETE -> delete(command);
            default -> ERROR;
        };

    }

    /**
     * Sets a value at the specified index in the database. Command format: "set {@literal <index> <value>}"
     *
     * @param commandLine array containing the command components where: commandLine[0] is the command name ("set")
     *                    commandLine[1] is the index commandLine[2] is the value to store
     */
    String set(String[] commandLine) {
        var index = getIndex(commandLine[1]);
        if (index == -1) {
            return ERROR;
        }
        database[index] = commandLine[2];
        return OK;
    }

    /**
     * Retrieves a value from the specified index in the database. Command format: "get {@literal <index>}"
     *
     * @param commandLine array containing the command components where: commandLine[0] is the command name ("get")
     *                    commandLine[1] is the index
     */
    String get(String[] commandLine) {
        var index = getIndex(commandLine[1]);
        if (index == -1) {
            return ERROR;
        }

        if (database[index].isEmpty()) {
            return ERROR;
        }

        return database[index];
    }

    /**
     * Deletes a value at the specified index in the database by setting it to empty string. Command format: "delete
     * {@literal <index>}"
     *
     * @param commandLine array containing the command components where: commandLine[0] is the command name ("delete")
     *                    commandLine[1] is the index
     */
    String delete(String[] commandLine) {
        var index = getIndex(commandLine[1]);
        if (index == -1) {
            return ERROR;
        }
        database[index] = "";
        return OK;
    }

    /**
     * Converts the provided string key to an integer index and validates if it falls
     * within the valid range of the database.
     *
     * @param key the string representing the potential index
     * @return the integer index if the key is a valid integer and within the allowed range,
     *         or -1 if the key is invalid or out of range
     */
    int getIndex(String key) {
        int index = -1;
        try {
            index = Integer.parseInt(key);
        } catch (NumberFormatException e) {
            return -1;
        }
        if (index < 1 || index > database.length) {
            return  -1;
        }
        return index - 1;
    }
}
