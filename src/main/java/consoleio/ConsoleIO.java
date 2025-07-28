package consoleio;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Objects;
import java.util.Scanner;
import java.util.function.IntFunction;

public final class ConsoleIO {

    private final PrintStream output;
    private final Scanner     input;

    public ConsoleIO(InputStream input, PrintStream output) {
        Objects.requireNonNull(input, "Input stream cannot be null and should be System.in");
        Objects.requireNonNull(output, "Output stream cannot be null and should be System.out");
        this.input = new Scanner(input);
        this.output = output;
    }

    public void println(String message) {
        output.println(message);
    }

    public void print(String message) {
        output.print(message);
        output.flush();
    }

    public void clear() {
        try {
            var clearCommand = System.getProperty("os.name").contains("Windows")
                               ? new ProcessBuilder("cmd", "/c", "cls")
                               : new ProcessBuilder("clear");
            clearCommand.inheritIO().start().waitFor();
        } catch (IOException | InterruptedException noop) {
            // NOOP
        }
    }

    public void getAnything() {
        input.nextLine();
    }

    public String getUserInput() {
        return input.nextLine().trim();
    }

    public String getLiteralUserInput() {
        return input.nextLine();
    }

    public int getIntegerUntilAboveZero() {
        IntFunction<Integer> aboveZero = i -> i > 0 ? i : null;

        Integer value = null;
        while (value == null) {
            value = getInteger(aboveZero);
            if (value == null) {
                output.print("Error! Incorrect Input. Try again: ");
            }
        }
        return value;
    }

    public Integer getInteger(IntFunction<Integer> validation) {
        try {
            var readIn = this.input.nextLine();
            var parsedInt = Integer.parseInt(readIn);
            return validation.apply(parsedInt);
        } catch (NumberFormatException | IllegalStateException e) {
            return null;
        }
    }
}
