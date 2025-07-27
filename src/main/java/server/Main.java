package server;

import consoleio.ConsoleIO;

public class Main {
    public static void main(String[] args) {
        new DatabaseArray(new ConsoleIO(System.in, System.out)).commandLoop();
    }
}
