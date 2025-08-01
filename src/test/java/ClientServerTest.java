import com.beust.jcommander.JCommander;
import com.google.gson.Gson;
import model.Args;
import model.ServerResponse;
import org.junit.jupiter.api.*;
import org.junitpioneer.jupiter.StdIo;
import org.junitpioneer.jupiter.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    private void processClientCommands(List<Map<String, String[]>> testArgs, StdOut out) {
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
        return "Sent: " + new Gson().toJson(args);
    }

    private String generateClientReceivedString(String response, String reason, String value) {
        var serverResponse = new ServerResponse().response(response)
                                                 .reason(reason)
                                                 .value(value);
        return "Received: " + serverResponse.toString();
    }
}
