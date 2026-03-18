package mg.msys.abde_back.batch;

import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.test.util.ReflectionTestUtils;

import mg.msys.abde_back.batch.task.DeleteCatalogFileTasklet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CleanupCatalogFileStepTest {

    @Test
    void shouldDeleteCatalogFileWhenItExists() throws Exception {
        Path file = Files.createTempFile("cleanup-step-", ".csv");

        DeleteCatalogFileTasklet tasklet = new DeleteCatalogFileTasklet();
        ReflectionTestUtils.setField(tasklet, "catalogDownloadPath", file.toString());

        RepeatStatus status = tasklet.execute(null, null);

        assertEquals(RepeatStatus.FINISHED, status);
        assertFalse(Files.exists(file));
    }

    @Test
    void shouldNotFailWhenCatalogFileDoesNotExist() throws Exception {
        Path file = Path.of(System.getProperty("java.io.tmpdir"), "missing-catalog-file.csv");
        Files.deleteIfExists(file);

        DeleteCatalogFileTasklet tasklet = new DeleteCatalogFileTasklet();
        ReflectionTestUtils.setField(tasklet, "catalogDownloadPath", file.toString());

        RepeatStatus status = tasklet.execute(null, null);

        assertEquals(RepeatStatus.FINISHED, status);
        assertFalse(Files.exists(file));
    }
}
