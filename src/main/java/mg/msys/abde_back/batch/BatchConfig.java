package mg.msys.abde_back.batch;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.infrastructure.item.file.FlatFileItemReader;
import org.springframework.batch.infrastructure.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.infrastructure.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.infrastructure.item.file.separator.DefaultRecordSeparatorPolicy;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.EnableJdbcJobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@EnableJdbcJobRepository(tablePrefix = "syst.BATCH_")
@EnableRetry
public class BatchConfig {

    @Value("${batch.catalog.download-path:/tmp/abde/pg_catalog.csv}")
    private String catalogDownloadPath;

    @Bean
    public Job importBookJob(JobRepository jobRepository, Step downloadCatalogStep, Step ingestionCatalogStep) {
        return new JobBuilder("importBookJob", jobRepository)
                .start(downloadCatalogStep)
                .next(ingestionCatalogStep)
                .build();
    }

    @Bean
    public Step downloadCatalogStep(JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            DownloadCatalogTasklet downloadCatalogTasklet) {
        return new StepBuilder("downloadCatalogStep", jobRepository)
                .tasklet(downloadCatalogTasklet)
                .transactionManager(transactionManager)
                .build();
    }

    @Bean
    public Step ingestionCatalogStep(JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<Book> reader,
            ItemProcessor<Book, Book> processor,
            ItemWriter<Book> writer) {

        return new StepBuilder("ingestionCatalogStep", jobRepository)
                .<Book, Book>chunk(100).transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public FlatFileItemReader<Book> reader() {
        BeanWrapperFieldSetMapper<Book> mapper = new BeanWrapperFieldSetMapper<>();
        mapper.setTargetType(Book.class);
        return new FlatFileItemReaderBuilder<Book>()
                .name("bookReader")
                .resource(new FileSystemResource(catalogDownloadPath))
                .linesToSkip(1)
                .recordSeparatorPolicy(new DefaultRecordSeparatorPolicy())
                .delimited()
                .includedFields(0, 2, 3, 4, 6)
                .names("id", "issued", "title", "languages", "subjects")
                .fieldSetMapper(mapper)
                .build();
    }

    @Bean
    public ItemProcessor<Book, Book> processor() {
        return book -> {
            book.setTitle(book.getTitle().toUpperCase());
            return book;
        };
    }

    @Bean
    public JdbcBatchItemWriter<Book> writer(DataSource dataSource) {
        return new JdbcBatchItemWriterBuilder<Book>()
                .dataSource(dataSource)
                .sql("INSERT INTO book (id, title, issued, languages, subjects) VALUES (:id, :title, :issued, :languages, :subjects) ON CONFLICT (id) DO NOTHING")
                .beanMapped()
                .assertUpdates(false)
                .build();
    }

}
