package com.srjons.templatejpa.batch;

import com.srjons.templatejpa.entity.User;
import com.srjons.templatejpa.repo.UserRepo;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class SpringBatchConfigExportUsers {

    private static final int MAX_CHUNK = 100;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private UserRowMapper studentMapper;

    @Autowired
    private DataSource dataSource;

    @Bean
    public JdbcCursorItemReader<User> jdbcCursorItemReader() {
        JdbcCursorItemReader<User> reader = new JdbcCursorItemReader<>();
        reader.setSql("select * from app_users");
        reader.setName("app_users_export");
        reader.setRowMapper(new UserRowMapper());
        reader.setDataSource(dataSource);
        return reader;
    }

    @Bean
    public FlatFileItemWriter<User> fileItemWriter() {
        FlatFileItemWriter<User> itemWriter = new FlatFileItemWriter<>();
        itemWriter.setResource(new FileSystemResource("src/main/resources/users_exported.csv"));
        itemWriter.setName("csvWriter");
        itemWriter.setAppendAllowed(true);
        itemWriter.setLineSeparator(System.lineSeparator());
        itemWriter.setLineAggregator(new DelimitedLineAggregator<>() {
            {
                setDelimiter(",");
                setFieldExtractor(new BeanWrapperFieldExtractor<>() {
                    {
                        setNames(new String[]{"userId", "userName", "password", "email"});
                    }
                });
            }
        });
        itemWriter.setHeaderCallback
                (writer -> writer.write("userId,userName,password,email"));
        return itemWriter;
    }

    public UserProcessor exportProcessor() {
        return new UserProcessor();
    }

    public Step exportStep1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("csv-export-step", jobRepository).
                <User, User>chunk(MAX_CHUNK, transactionManager)
                .reader(jdbcCursorItemReader())
                .processor(exportProcessor())
                .writer(fileItemWriter())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean("exportJob")
    public Job runJobExport(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new JobBuilder("exportUsers", jobRepository)
                .flow(exportStep1(jobRepository, transactionManager)).end().build();
    }

    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor asyncTaskExecutor = new SimpleAsyncTaskExecutor();
        asyncTaskExecutor.setConcurrencyLimit(10);
        return asyncTaskExecutor;
    }
}
