package server;

import consoleio.ConsoleIO;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junitpioneer.jupiter.StdIn;
import org.junitpioneer.jupiter.StdIo;
import org.junitpioneer.jupiter.StdOut;

import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DatabaseArrayTest {
    private static DatabaseArray databaseArray;
    private static final String ERROR = "ERROR";
    private static final String OK = "OK";

    @BeforeAll
    static void setUpBeforeClass() {
        databaseArray = new DatabaseArray(new ConsoleIO(System.in, System.out));
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @StdIo({"set 1 Hello, world!", "get 1", "exit"})
    void stage1Test1(StdIn in, StdOut out) {
        new DatabaseArray(new ConsoleIO(in, new PrintStream(out))).commandLoop();
        String[] expected = {OK, "Hello, world!"};
        assertArrayEquals(expected, out.capturedLines());
    }

    @Test
    @StdIo({"get 10", "set 10 There is something here.", "get 10", "exit"})
    void stage1Test2(StdIn in, StdOut out) {
        new DatabaseArray(new ConsoleIO(in, new PrintStream(out))).commandLoop();
        String[] expected = {ERROR, OK, "There is something here."};
        assertArrayEquals(expected, out.capturedLines());
    }

    @Test
    @StdIo({"get 50", "set 50 Something that is very long.", "set 50 Short phrase.", "get 50", "exit"})
    void stage1Test3(StdIn in, StdOut out) {
        new DatabaseArray(new ConsoleIO(in, new PrintStream(out))).commandLoop();
        String[] expected = {ERROR, OK, OK, "Short phrase."};
        assertArrayEquals(expected, out.capturedLines());
    }

    @Test
    @StdIo({"GET 100", "sEt 100 Testing mixed case commands", "GeT 100", "DEleTE 100", "geT 100", "exit"})
    void stage1Test4(StdIn in, StdOut out) {
        new DatabaseArray(new ConsoleIO(in, new PrintStream(out))).commandLoop();
        String[] expected = {ERROR, OK, "Testing mixed case commands", OK, ERROR};
        assertArrayEquals(expected, out.capturedLines());
    }

    @Test
    @StdIo({"set 0 Not valid", "set 1001 Not valid", "set ten not valid", "set 33.3 Not valid", "exit"})
    void stage1Test5(StdIn in, StdOut out) {
        new DatabaseArray(new ConsoleIO(in, new PrintStream(out))).commandLoop();
        String[] expected = {ERROR, ERROR, ERROR, ERROR};
        assertArrayEquals(expected, out.capturedLines());
    }

    @ParameterizedTest(name = "Index argument of {0} should return {1}")
    @CsvSource(value = {"1, 0", "0, -1", "1000, 999", "1001, -1", "one, -1", "2.5, -1"})
    @DisplayName("Test the getIndex method works properly with integer inputs only")
    void getIndex(String index, int expected) {
        assertEquals(expected, databaseArray.getIndex(index));
    }

}
