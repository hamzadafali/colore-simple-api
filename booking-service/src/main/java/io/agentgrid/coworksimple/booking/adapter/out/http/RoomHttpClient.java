package io.agentgrid.coworksimple.booking.adapter.out.http;

import io.agentgrid.coworksimple.booking.application.port.RoomClientPort;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class RoomHttpClient implements RoomClientPort {

    private final RestClient.Builder restClientBuilder;

    @Value("${services.room.url}")
    private String roomServiceUrl;

    @Override
    public boolean existsById(UUID roomId) {
        try {
            restClientBuilder
                    .baseUrl(roomServiceUrl)
                    .build()
                    .get()
                    .uri("/api/v1/rooms/{id}", roomId)
                    .retrieve()
                    .toBodilessEntity();
            return true;
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode().isSameCodeAs(HttpStatusCode.valueOf(404))) {
                return false;
            }
            throw exception;
        }
    }
}
