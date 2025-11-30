package me.sathish.aws_refresh.config;

import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EntityScan("me.sathish.aws_refresh.domain")
@EnableJpaRepositories("me.sathish.aws_refresh.repos")
@EnableTransactionManagement
public class DomainConfig {
}
