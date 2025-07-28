package server;

import consoleio.ConsoleIO;

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

    private final ConsoleIO consoleIO;
    private final String[] database = new String[MAX_SIZE];


    public DatabaseArray(ConsoleIO consoleIO) {
        this.consoleIO = consoleIO;
        Arrays.fill(database, "");
    }

    /**
     * Starts the main command processing loop that handles user input. Continuously reads and processes commands until
     * an exit command is received. Supported commands: set, get, delete, and exit.
     */
    void commandLoop() {
        var commandInput = "";
        var exitLoop = false;
        while (!exitLoop) {
            commandInput = consoleIO.getUserInput();
            var command = commandInput.split(" ", 3);
            switch (command[0].toLowerCase()) {
                case COMMAND_SET -> set(command);
                case COMMAND_GET -> get(command);
                case COMMAND_DELETE -> delete(command);
                case COMMAND_EXIT -> exitLoop = true;
                default -> consoleIO.println(ERROR);
            }
        }
    }

    /**
     * Sets a value at the specified index in the database. Command format: "set {@literal <index> <value>}"
     *
     * @param commandLine array containing the command components where: commandLine[0] is the command name ("set")
     *                    commandLine[1] is the index commandLine[2] is the value to store
     */
    void set(String[] commandLine) {
        var index = getIndex(commandLine[1]);
        if (index == -1) {
            consoleIO.println(ERROR);
            return;
        }
        database[index] = commandLine[2];
        consoleIO.println(OK);
    }

    /**
     * Retrieves a value from the specified index in the database. Command format: "get {@literal <index>}"
     *
     * @param commandLine array containing the command components where: commandLine[0] is the command name ("get")
     *                    commandLine[1] is the index
     */
    void get(String[] commandLine) {
        var index = getIndex(commandLine[1]);
        if (index == -1) {
            consoleIO.println(ERROR);
            return;
        }

        if (database[index].isEmpty()) {
            consoleIO.println(ERROR);
            return;
        }

        consoleIO.println(database[index]);
    }

    /**
     * Deletes a value at the specified index in the database by setting it to empty string. Command format: "delete
     * {@literal <index>}"
     *
     * @param commandLine array containing the command components where: commandLine[0] is the command name ("delete")
     *                    commandLine[1] is the index
     */
    void delete(String[] commandLine) {
        var index = getIndex(commandLine[1]);
        if (index == -1) {
            consoleIO.println(ERROR);
            return;
        }
        database[index] = "";
        consoleIO.println(OK);
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
