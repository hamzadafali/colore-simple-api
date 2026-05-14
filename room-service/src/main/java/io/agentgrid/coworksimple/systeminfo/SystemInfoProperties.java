package io.agentgrid.coworksimple.systeminfo;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cowork")
public record SystemInfoProperties(String serverName, String version) {
}
