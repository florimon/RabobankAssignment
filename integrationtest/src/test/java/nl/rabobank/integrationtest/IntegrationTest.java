package nl.rabobank.integrationtest;

import com.intuit.karate.junit5.Karate;
import nl.rabobank.RaboAssignmentApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.io.IOException;
import java.net.ServerSocket;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;


@SpringBootTest(webEnvironment = RANDOM_PORT, classes = RaboAssignmentApplication.class)
public class IntegrationTest {

    private static final int DEFAULT_SERVER_PORT_NUMBER = 8080;

    private static Integer RANDOM_SERVER_PORT_NUMBER;

    @Karate.Test
    public Karate testFeatures() {
        return Karate.run("classpath:features/happyFlow.feature", "classpath:features/rainyDay.feature");
    }

    @LocalServerPort
    public void setLocalServerPort(int localServerPort) { // instance setter, Spring does not inject into static fields
        RANDOM_SERVER_PORT_NUMBER = localServerPort;
    }

    /**
     * This method is called from within karate-config.js ; if the integration tests were kicked off by running this
     * class (either from within the IDE, or by a Maven build), then the {@link IntegrationTest#RANDOM_SERVER_PORT_NUMBER}
     * will be set, and returned as part of the server url.
     * If the integration tests however were kicked off by executing individual Features or Scenarios directly
     * from within the IDE (in IntelliJ, by clicking on the 'play' icon next to them in the editor left margin),
     * then LOCAL_SERVER_PORT is not set, and the default 8080 is returned, after ensuring that that port is already
     * in use (and if it's not, starting the server explicitly).
     *
     * @return the URL that the server is running on
     */
    public static String getServerUrl() {
        return format("http://localhost:%s/", ofNullable(RANDOM_SERVER_PORT_NUMBER)
                                                .orElseGet(IntegrationTest::defaultPortOfRunningServer));
    }

    private static int defaultPortOfRunningServer() {
        if (isDefaultPortNotAlreadyListening()) {
            RaboAssignmentApplication.main();
        }
        return DEFAULT_SERVER_PORT_NUMBER;
    }

    private static boolean isDefaultPortNotAlreadyListening() {
        try (ServerSocket ss = new ServerSocket(DEFAULT_SERVER_PORT_NUMBER)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
