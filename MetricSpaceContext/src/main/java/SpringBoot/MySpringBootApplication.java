package SpringBoot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

@SpringBootApplication()
@MapperScan("SpringBoot.dao")
public class MySpringBootApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(MySpringBootApplication.class);
        application.run(args);
    }

    @Configuration
    public class ErrorPageConfig {
        @Bean
        public WebServerFactoryCustomizer<ConfigurableWebServerFactory> webServerFactoryCustomizer() {

            /*return new WebServerFactoryCustomizer<ConfigurableWebServerFactory>() {
                @Override
                public void customize(ConfigurableWebServerFactory factory) {
                    ErrorPage errorPage404 = new ErrorPage(HttpStatus.NOT_FOUND, "/404.html");
                    factory.addErrorPages(errorPage404);
                }
            };*/

            return (factory -> {
                ErrorPage errorPage404 = new ErrorPage(HttpStatus.NOT_FOUND, "/index.html");
                factory.addErrorPages(errorPage404);
            });
        }
    }

}