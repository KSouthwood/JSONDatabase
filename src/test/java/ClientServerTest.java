import com.beust.jcommander.JCommander;
import com.google.gson.Gson;
import model.Args;
import model.ServerResponse;
import org.junit.jupiter.api.*;
import org.junitpioneer.jupiter.StdIo;
import org.junitpioneer.jupiter.StdOut;
import org.opentest4j.AssertionFailedError;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.io.File.separator;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ClientServerTest {
    private static final String ERROR = "ERROR";
    private static final String OK    = "OK";

    @BeforeAll
    static void setUpBeforeClass() {

    }

    @BeforeEach
    void setUp() {
        Thread server = new Thread(new server.Main());
        server.start();
    }

    @AfterEach
    void tearDown() {

    }

    @AfterAll
    static void tearDownAfterClass() {

    }

    @Test
    @StdIo
    void test1(StdOut out) {
        List<Map<String, String[]>> testArgs = List.of(
                Map.of("sent", new String[]{"-t", "get", "-k", "100"},
                       "received", new String[]{ERROR, "No such key", null}),
                Map.of("sent", new String[]{"-t", "exit"},
                       "received", new String[]{OK, null, null})
        );
        processClientCommands(testArgs, out);
    }

    @Test
    @StdIo
    void test2(StdOut out) {
        List<Map<String, String[]>> testArgs = List.of(
                Map.of("sent", new String[]{"-t", "set", "-k", "100", "-v", "100"},
                       "received", new String[]{OK, null, null}),
                Map.of("sent", new String[]{"-t", "get", "-k", "100"},
                       "received", new String[]{OK, null, "100"}),
                Map.of("sent", new String[]{"-t", "exit"},
                       "received", new String[]{OK, null, null}));
        processClientCommands(testArgs, out);
    }

    @Test
    @StdIo
    void test3(StdOut out) {
        List<Map<String, String[]>> testArgs = List.of(
                Map.of("sent", new String[]{"-t", "get", "-k", "1"},
                       "received", new String[]{ERROR, "No such key", null}),
                Map.of("sent", new String[]{"-t", "set", "-k", "1", "-v", "Hello World!"},
                       "received", new String[]{OK, null, null}),
                Map.of("sent", new String[]{"-t", "set", "-k", "1", "-v", "HelloWorld!"},
                       "received", new String[]{OK, null, null}),
                Map.of("sent", new String[]{"-t", "get", "-k", "1"},
                       "received", new String[]{OK, null, "HelloWorld!"}),
                Map.of("sent", new String[]{"-t", "delete", "-k", "1"},
                       "received", new String[]{OK, null, null}),
                Map.of("sent", new String[]{"-t", "delete", "-k", "1"},
                       "received", new String[]{ERROR, "No such key", null}),
                Map.of("sent", new String[]{"-t", "get", "-k", "1"},
                       "received", new String[]{ERROR, "No such key", null}),
                Map.of("sent", new String[]{"-t", "set", "-k", "name", "-v", "Wigbald Higgins"},
                       "received", new String[]{OK, null, null}),
                Map.of("sent", new String[]{"-t", "get", "-k", "name"},
                       "received", new String[]{OK, null, "Wigbald Higgins"}),
                Map.of("sent", new String[]{"-t", "exit"},
                       "received", new String[]{OK, null, null}));
        processClientCommands(testArgs, out);
    }

    @Test
    @StdIo
    void test4(StdOut out) {
        List<Map<String, String[]>> testArgs = List.of(
                Map.of("sent", new String[]{"-in", "testSet.json"},
                       "received", new String[]{OK, null, null}),
                Map.of("sent", new String[]{"-in", "testGet.json"},
                       "received", new String[]{OK, null, "Frodo Baggins"}),
                Map.of("sent", new String[]{"-in", "testDelete.json"},
                       "received", new String[]{OK, null, null})
        );
        createTestFiles();
        processClientCommands(testArgs, out);
    }

    private void processClientCommands(List<Map<String, String[]>> testArgs, StdOut out) {
        try {
            var expected = new ArrayList<String>();
            expected.add("Server started!");
            for (Map<String, String[]> test : testArgs) {
                String[] sent     = test.get("sent");
                String[] received = test.get("received");
                expected.addAll(List.of(generateExpectedClientOutput(generateClientSentString(sent),
                                                                     generateClientReceivedString(received[0], received[1], received[2]))));
                client.Main.main(sent);
                assertEquals(expected, Arrays.asList(out.capturedLines()));
            }
        } catch (AssertionFailedError assertionFailedError) {
            System.err.println("Shutting down server...");
            client.Main.main(new String[]{"-t", "exit"});
            fail(assertionFailedError.getMessage());
        }
    }

    private String[] generateExpectedClientOutput(String sent, String received) {
        final String[] expected = new String[3];
        expected[0] = "Client started!";
        expected[1] = sent;
        expected[2] = received;
        return expected;
    }

    private String generateClientSentString(String[] argv) {
        Args args = new Args();
        JCommander.newBuilder()
                  .addObject(args)
                  .build()
                  .parse(argv);
        if (args.filename() != null) {
            String dataDirectory = "%1$s%2$ssrc%2$smain%2$sjava%2$sclient%2$sdata%2$s".formatted(System.getProperty("user.dir"), separator);
            try (FileReader reader = new FileReader(dataDirectory + args.filename())) {
                args = new Gson().fromJson(reader, Args.class);
            } catch (IOException e) {
                System.err.println("Error reading file: " + e.getMessage());
            }
        }return "Sent: " + new Gson().toJson(args);
    }

    private String generateClientReceivedString(String response, String reason, String value) {
        var serverResponse = new ServerResponse().response(response)
                                                 .reason(reason)
                                                 .value(value);
        return "Received: " + serverResponse.toString();
    }

    private void createTestFiles() {
        String separator = FileSystems.getDefault()
                                      .getSeparator();
        String dataDirectory = "%1$s%2$ssrc%2$smain%2$sjava%2$sclient%2$sdata%2$s".formatted(System.getProperty("user.dir"), separator);
        String filename      = "testSet.json";
        String contents = """
                          {"type": "set","key": "name","value": "Frodo Baggins"}
                          """;
        File file = new File(dataDirectory + filename);
        createTestFile(file);
        writeTestFiles(file, contents);

        filename = "testGet.json";
        contents = """
                   {"type": "get","key": "name"}
                   """;
        file = new File(dataDirectory + filename);
        createTestFile(file);
        writeTestFiles(file, contents);

        filename = "testDelete.json";
        contents = """
                   {"type": "delete","key": "name"}
                   """;
        file = new File(dataDirectory + filename);
        createTestFile(file);
        writeTestFiles(file, contents);
    }

    private void createTestFile(File file) {
        try {
            if (file.getParentFile()
                    .mkdirs()) {
                System.err.println("Created necessary directories");
            }

            if (file.createNewFile()) {
                file.deleteOnExit();
            } else {
                System.err.printf("File '%s' already exists.%n", file.getName());
            }
        } catch (IOException e) {
            System.err.println("IOError while creating file: " + e.getMessage());
        }
    }

    private void writeTestFiles(File file, String contents) {
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(contents);
        } catch (IOException e) {
            System.err.println("IOError while writing file: " + e.getMessage());
        }
    }
}
