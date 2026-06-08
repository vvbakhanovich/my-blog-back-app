package dev.vvbakh;

import dev.vvbakh.config.DataSourceConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = {
        "dev.vvbakh.posts.repository",
        "dev.vvbakh.comments.repository",
        "dev.vvbakh.tags.repository"
})
@PropertySource("classpath:application-test.properties")
@Import(DataSourceConfiguration.class)
public class RepositoryTestConfiguration {
}
