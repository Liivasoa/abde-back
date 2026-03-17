package mg.msys.abde_back.batch;

import javax.sql.DataSource;

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
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@EnableJdbcJobRepository(tablePrefix = "syst.BATCH_")
public class BatchConfig {

    @Bean
    public FlatFileItemReader<Book> reader() {
        BeanWrapperFieldSetMapper<Book> mapper = new BeanWrapperFieldSetMapper<>();
        mapper.setTargetType(Book.class);
        return new FlatFileItemReaderBuilder<Book>()
                .name("bookReader")
                .resource(new ClassPathResource("pg_catalog.csv"))
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

    @Bean
    public Step step(JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<Book> reader,
            ItemProcessor<Book, Book> processor,
            ItemWriter<Book> writer) {

        return new StepBuilder("step1", jobRepository)
                .<Book, Book>chunk(100).transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }

    @Bean
    public Job importBooksJob(JobRepository jobRepository, Step step) {
        return new JobBuilder("importBooksJob", jobRepository)
                .start(step)
                .build();
    }
}
