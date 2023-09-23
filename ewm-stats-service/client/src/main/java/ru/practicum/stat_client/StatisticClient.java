package ru.practicum.stat_client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.practicum.stat_dto.EndpointHitDto;
import ru.practicum.stat_dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticClient {
    private final String serverUrl;
    private final RestTemplate restTemplate;

    public StatisticClient(@Value("${stats-server.url}") String serverUrl, RestTemplate restTemplate) {
        this.serverUrl = serverUrl;
        this.restTemplate = restTemplate;
    }

    public void addStats(EndpointHitDto endpointHitDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<EndpointHitDto> requestEntity = new HttpEntity<>(endpointHitDto, headers);

        ResponseEntity<Void> response = restTemplate.exchange(
                serverUrl + "/hit",
                HttpMethod.POST,
                requestEntity,
                Void.class);
    }

    public List<ViewStatsDto> getStats(LocalDateTime start,
            LocalDateTime end,
            List<String> uris,
            Boolean unique) {

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", start.format(dateTimeFormatter));
        parameters.put("end", end.format(dateTimeFormatter));
        if (uris != null) {
            parameters.put("uris", String.join(",", uris));
        }
        if (unique != null) {
            parameters.put("unique", unique);
        }

        ResponseEntity<String> response = restTemplate.getForEntity(
                serverUrl + "/stats?start={start}&end={end}" +
                        (uris != null ? "&uris={uris}" : "") +
                        (unique != null ? "&unique={unique}" : ""),
                String.class,
                parameters);

        ObjectMapper objectMapper = new ObjectMapper();
        ViewStatsDto[] viewStatsArray = new ViewStatsDto[0];
        try {
            viewStatsArray = objectMapper.readValue(response.getBody(), ViewStatsDto[].class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return Arrays.asList(viewStatsArray);
    }
}
