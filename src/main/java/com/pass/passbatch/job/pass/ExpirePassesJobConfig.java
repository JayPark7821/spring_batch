package com.pass.passbatch.job.pass;

import java.time.LocalDateTime;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.pass.passbatch.repository.pass.PassEntity;
import com.pass.passbatch.repository.pass.PassStatus;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class ExpirePassesJobConfig {
	private final int CHUNK_SIZE = 5;

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final EntityManagerFactory emf;

	@Bean
	public Job expirePassesJob() {
		return this.jobBuilderFactory.get("expirePassesJob")
			.start(expirePassesStep())
			.build();
	}

	@Bean
	public Step expirePassesStep() {
		return this.stepBuilderFactory.get("expirePassesStep")
			.<PassEntity, PassEntity>chunk(CHUNK_SIZE)
			.reader(expirePassesItemReader())
			.processor(expirePassesProcessor())
			.writer(expirePassesWriter())
			.build();
	}

	@Bean
	@StepScope
	public JpaCursorItemReader<PassEntity> expirePassesItemReader() {
		return new JpaCursorItemReaderBuilder<PassEntity>()
			.name("expirePassesItemReader")
			.entityManagerFactory(emf)
			.queryString("select p from PassEntity p where p.status = :status and p.endedAt <= :endedAt")
			.parameterValues(Map.of("status", PassStatus.PROGRESSED,"endedAt", LocalDateTime.now()))
			.build();
	}

	@Bean
	public ItemProcessor<PassEntity, PassEntity> expirePassesProcessor() {
		return passEntity -> {
			passEntity.setStatus(PassStatus.EXPIRED);
			passEntity.setExpiredAt(LocalDateTime.now());
			return passEntity;
		};
	}

	private ItemWriter<PassEntity> expirePassesWriter() {
		return new JpaItemWriterBuilder<PassEntity>()
			.entityManagerFactory(emf)
			.build();
	}
}
