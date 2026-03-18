package mg.msys.abde_back.batch;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import com.sun.net.httpserver.HttpServer;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.test.util.ReflectionTestUtils;

import mg.msys.abde_back.batch.task.DownloadCatalogTasklet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DownloadCatalogStepTest {

    private HttpServer server;
    private Path tempDir;

    @AfterEach
    void tearDown() throws IOException {
        if (server != null) {
            server.stop(0);
        }

        if (tempDir != null && Files.exists(tempDir)) {
            Files.walk(tempDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException ignored) {
                        }
                    });
        }
    }

    @Test
    void shouldDownloadCatalogToConfiguredPath() throws Exception {
        tempDir = Files.createTempDirectory("download-step-test-");
        Path output = tempDir.resolve("pg_catalog.csv");

        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/pg_catalog.csv", exchange -> {
            byte[] body = "Text#,Type,Issued,Title,Language,Authors,Subjects\n1,Text,2020,Book,en,Doe,Subject\n"
                    .getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();

        String url = "http://localhost:" + server.getAddress().getPort() + "/pg_catalog.csv";

        DownloadCatalogTasklet tasklet = new DownloadCatalogTasklet();
        ReflectionTestUtils.setField(tasklet, "catalogUrl", url);
        ReflectionTestUtils.setField(tasklet, "catalogDownloadPath", output.toString());

        RepeatStatus status = tasklet.execute(null, null);

        assertEquals(RepeatStatus.FINISHED, status);
        assertTrue(Files.exists(output));
        assertTrue(Files.size(output) > 0);
    }

    @Test
    void shouldFailWhenRemoteStatusIsNot200() throws Exception {
        tempDir = Files.createTempDirectory("download-step-failure-test-");
        Path output = tempDir.resolve("pg_catalog.csv");

        server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/pg_catalog.csv", exchange -> {
            exchange.sendResponseHeaders(503, -1);
            exchange.close();
        });
        server.start();

        String url = "http://localhost:" + server.getAddress().getPort() + "/pg_catalog.csv";

        DownloadCatalogTasklet tasklet = new DownloadCatalogTasklet();
        ReflectionTestUtils.setField(tasklet, "catalogUrl", url);
        ReflectionTestUtils.setField(tasklet, "catalogDownloadPath", output.toString());

        assertThrows(IllegalStateException.class, () -> tasklet.execute(null, null));
    }
}
