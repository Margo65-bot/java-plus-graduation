package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dto.HitCreateDto;
import ru.practicum.dto.RequestStatsDto;
import ru.practicum.dto.ResponseStatsDto;

import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class StatsClientRestImpl implements StatsClient {

    private static final String STATS_SERVICE_ID = "stats-server";

    private final DiscoveryClient discoveryClient;
    private final DateTimeFormatter formatter;

    public StatsClientRestImpl(
            DiscoveryClient discoveryClient,
            @Value("${stats.date_time.format}") String pattern
    ) {
        this.discoveryClient = discoveryClient;
        this.formatter = DateTimeFormatter.ofPattern(pattern);
    }

    @Override
    public void hit(HitCreateDto dto) {
        try {
            RestClient.create(getBaseUrl())
                    .post()
                    .uri("/hit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(dto)
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            log.warn("Failed to send hit to stats-server", e);
        }
    }

    @Override
    public List<ResponseStatsDto> get(RequestStatsDto requestStatsDto) {
        try {
            URI uri = UriComponentsBuilder
                    .fromPath("/stats")
                    .queryParam("start", requestStatsDto.start().format(formatter))
                    .queryParam("end", requestStatsDto.end().format(formatter))
                    .queryParam("unique", requestStatsDto.unique())
                    .queryParamIfPresent("uris", Optional.ofNullable(requestStatsDto.uris()))
                    .build()
                    .toUri();

            return RestClient.create(getBaseUrl())
                    .get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<ResponseStatsDto>>() {});
        } catch (Exception e) {
            log.warn("Failed to get stats from stats-server", e);
            return Collections.emptyList();
        }
    }

    private String getBaseUrl() {
        return discoveryClient.getInstances(STATS_SERVICE_ID).stream()
                .findFirst()
                .map(instance -> instance.getUri().toString())
                .orElseThrow(() ->
                        new IllegalStateException("stats-server not found in Eureka"));
    }
}