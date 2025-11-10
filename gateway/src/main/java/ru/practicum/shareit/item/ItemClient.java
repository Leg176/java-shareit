package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.NewCommentRequest;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> searchItems(String text) {
        Map<String, Object> parameters = Map.of("text", text);
        return get("/search?text={text}", null, parameters);
    }

    public ResponseEntity<Object> getItem(Long itemId, Long ownerId) {
        return get("/" + itemId, ownerId);
    }

    public ResponseEntity<Object> getItemsOwner(Long ownerId) {
        return get("", ownerId);
    }

    public ResponseEntity<Object> addNewItem(long ownerId, NewItemDto request) {
        return post("", ownerId, request);
    }

    public ResponseEntity<Object> updateItem(UpdateItemDto request, Long ownerId, Long id) {
        return patch("/" + id, ownerId, request);
    }

    public ResponseEntity<Object> deleteItem(Long id, Long ownerId) {
        return delete("/" + id, ownerId);
    }

    public ResponseEntity<Object> addNewComment(long ownerId, long itemId, NewCommentRequest request) {
        return post("/" + itemId + "/comment", ownerId, request);
    }
}
