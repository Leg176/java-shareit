package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.dto.UpdateItemRequestDto;

@Service
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> getRequestsByNotOwner(Long userId) {
        return get("/all", userId);
    }

    public ResponseEntity<Object> getRequestById(Long id) {
        return get("/" + id);
    }

    public ResponseEntity<Object> getRequestsByOwner(Long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> addNewRequest(NewItemRequestDto itemRequest, long ownerId) {
        return post("", ownerId, itemRequest);
    }

    public ResponseEntity<Object> updateRequest(UpdateItemRequestDto request, Long ownerId, Long id) {
        return patch("/" + id, ownerId, request);
    }

    public ResponseEntity<Object> deleteRequest(Long id, Long ownerId) {
        return delete("/" + id, ownerId);
    }
}
