package org.wyeworks;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("test")
public class SpringTestConfiguration {

    @Bean
    public DiscographyApplication discographyApplication() {
        return new DiscographyApplication();
    }
}
