package io.agentgrid.coworksimple;

import io.agentgrid.coworksimple.systeminfo.SystemInfoProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(SystemInfoProperties.class)
public class CoworkSimpleApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoworkSimpleApplication.class, args);
    }
}
