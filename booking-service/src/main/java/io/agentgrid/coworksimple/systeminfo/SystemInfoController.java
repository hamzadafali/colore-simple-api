package io.agentgrid.coworksimple.systeminfo;

import jakarta.servlet.http.HttpServletRequest;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/system")
@RequiredArgsConstructor
public class SystemInfoController {

    private static final String SERVER_TIME_HEADER = "X-Cowork-Server-Time";
    private static final String FORWARDED_FOR_HEADER = "X-Forwarded-For";

    private final SystemInfoProperties properties;

    @GetMapping("/info")
    public ResponseEntity<SystemInfoResponse> getSystemInfo(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        String protocolUsed = buildProtocolInfo(request.getProtocol(), userAgent);
        String clientIp = resolveClientIp(request);

        SystemInfoResponse response = new SystemInfoResponse(
                properties.serverName(),
                properties.version(),
                "UP",
                protocolUsed,
                clientIp
        );

        return ResponseEntity.ok()
                .header(SERVER_TIME_HEADER, OffsetDateTime.now(ZoneOffset.UTC).toString())
                .body(response);
    }

    private static String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader(FORWARDED_FOR_HEADER);
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            int commaIndex = forwardedFor.indexOf(',');
            if (commaIndex > 0) {
                return forwardedFor.substring(0, commaIndex).trim();
            }
            return forwardedFor.trim();
        }
        return request.getRemoteAddr();
    }

    private static String buildProtocolInfo(String protocol, String userAgent) {
        if (userAgent == null || userAgent.isBlank()) {
            return protocol;
        }
        return protocol + " | " + userAgent.trim();
    }
}
