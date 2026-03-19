package mg.msys.abde_back.batch;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;

import mg.msys.abde_back.batch.entity.GutenbergBook;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

class IngestionCatalogStepTest {

    @Test
    void shouldCreateIngestionStepWithExpectedName() {
        BatchConfig batchConfig = new BatchConfig();

        JobRepository jobRepository = mock(JobRepository.class);
        PlatformTransactionManager txManager = mock(PlatformTransactionManager.class);
        @SuppressWarnings("unchecked")
        ItemReader<GutenbergBook> reader = (ItemReader<GutenbergBook>) mock(ItemReader.class);
        @SuppressWarnings("unchecked")
        ItemWriter<GutenbergBook> writer = (ItemWriter<GutenbergBook>) mock(ItemWriter.class);

        Step step = batchConfig.ingestionCatalogStep(jobRepository, txManager, reader, writer);

        assertNotNull(step);
        assertEquals("ingestionCatalogStep", step.getName());
    }

    @Test
    void shouldBuildReaderUsingConfiguredFilePath() {
        BatchConfig batchConfig = new BatchConfig();
        String configuredPath = Path.of(System.getProperty("java.io.tmpdir"), "ingestion-step-reader.csv").toString();

        ReflectionTestUtils.setField(batchConfig, "catalogDownloadPath", configuredPath);

        var reader = batchConfig.reader();

        assertNotNull(reader);
        assertEquals("bookReader", reader.getName());

        Object resourceField = ReflectionTestUtils.getField(reader, "resource");
        assertNotNull(resourceField);
        assertEquals(configuredPath,
                ((Resource) resourceField).getDescription().replace("file [", "").replace("]", ""));
    }

    @Test
    void shouldParseIssuedYearAsFirstDayOfYear() throws Exception {
        LocalDate issued = parseIssued("2020");

        assertEquals(LocalDate.of(2020, 1, 1), issued);
    }

    @Test
    void shouldParseIssuedIsoDate() throws Exception {
        LocalDate issued = parseIssued("2020-05-10");

        assertEquals(LocalDate.of(2020, 5, 10), issued);
    }

    @Test
    void shouldReturnNullWhenIssuedIsNullOrBlank() throws Exception {
        assertNull(parseIssued(null));
        assertNull(parseIssued("   "));
    }

    @Test
    void shouldReturnNullWhenIssuedFormatIsInvalid() throws Exception {
        assertNull(parseIssued("unknown"));
        assertNull(parseIssued("2020/12/31"));
    }

    private static LocalDate parseIssued(String issuedValue) throws Exception {
        Method parseIssuedMethod = BatchConfig.class.getDeclaredMethod("parseIssued", String.class);
        parseIssuedMethod.setAccessible(true);
        return (LocalDate) parseIssuedMethod.invoke(null, issuedValue);
    }
}
