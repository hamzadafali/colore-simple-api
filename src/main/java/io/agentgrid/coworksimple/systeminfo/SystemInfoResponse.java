package io.agentgrid.coworksimple.systeminfo;

public record SystemInfoResponse(
        String serverName,
        String version,
        String status,
        String protocolUsed,
        String clientIp
) {
}
