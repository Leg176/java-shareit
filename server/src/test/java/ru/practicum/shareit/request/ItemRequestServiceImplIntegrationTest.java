package ru.practicum.shareit.request;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import ru.practicum.shareit.ShareItApp;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithoutItemsDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.dto.UpdateItemRequestDto;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.entity.User;
import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = ShareItApp.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@ActiveProfiles("test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplIntegrationTest {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRequestService itemRequestService;

    private User owner;
    private User anotherUser;
    private User thirdUser;
    private ItemRequest request1;
    private ItemRequest request2;
    private ItemRequest request3;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        itemRequestRepository.deleteAll();

        owner = userRepository.save(User.builder()
                .name("Owner User")
                .email("owner@example.com")
                .build());

        anotherUser = userRepository.save(User.builder()
                .name("Another User")
                .email("another@example.com")
                .build());

        thirdUser = userRepository.save(User.builder()
                .name("Third User")
                .email("third@example.com")
                .build());

        request1 = itemRequestRepository.save(ItemRequest.builder()
                .description("Need a drill for home repairs")
                .requestor(owner)
                .created(LocalDateTime.now().minusDays(3))
                .build());

        request2 = itemRequestRepository.save(ItemRequest.builder()
                .description("Looking for a hammer")
                .requestor(anotherUser)
                .created(LocalDateTime.now().minusDays(2))
                .build());

        request3 = itemRequestRepository.save(ItemRequest.builder()
                .description("Need a saw for woodworking")
                .requestor(thirdUser)
                .created(LocalDateTime.now().minusDays(1))
                .build());
    }

    @Test
    void getRequestsByOwner_success() {
        Collection<ItemRequestDto> result = itemRequestService.getRequestsByOwner(owner.getId());

        assertThat(result).hasSize(1);
        assertThat(result.iterator().next().getDescription()).isEqualTo("Need a drill for home repairs");
    }

    @Test
    void getRequestsByOwner_userNotFound_returnsEmptyList() {
        Collection<ItemRequestDto> result = itemRequestService.getRequestsByOwner(999L);

        assertThat(result).isEmpty();
    }

    @Test
    void getRequestsByNotOwner_success() {
        Collection<ItemRequestWithoutItemsDto> result = itemRequestService.getRequestsByNotOwner(owner.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ItemRequestWithoutItemsDto::getDescription)
                .containsExactlyInAnyOrder("Looking for a hammer", "Need a saw for woodworking");
    }

    @Test
    void addNewRequest_success() {
        NewItemRequestDto newRequest = NewItemRequestDto.builder()
                .description("Need a new laptop for work")
                .build();

        ItemRequestDto result = itemRequestService.addNewRequest(newRequest, owner.getId());

        assertNotNull(result);
        assertThat(result.getId()).isNotNull();
        assertThat(result.getDescription()).isEqualTo("Need a new laptop for work");
        assertThat(result.getCreated()).isNotNull();
    }

    @Test
    void updateRequest_success() {
        UpdateItemRequestDto updateRequest = UpdateItemRequestDto.builder()
                .id(request1.getId())
                .description("Updated description for drill request")
                .build();

        ItemRequestDto result = itemRequestService.updateRequest(updateRequest, owner.getId());

        assertThat(result.getId()).isEqualTo(request1.getId());
        assertThat(result.getDescription()).isEqualTo("Updated description for drill request");
    }

    @Test
    void getRequestById_success() {
        ItemRequestDto result = itemRequestService.getRequestById(request1.getId());

        assertThat(result.getId()).isEqualTo(request1.getId());
        assertThat(result.getDescription()).isEqualTo("Need a drill for home repairs");
    }

    @Test
    void deleteRequest_success() {
        itemRequestService.deleteRequest(request1.getId(), owner.getId());

        assertThat(itemRequestRepository.findById(request1.getId())).isEmpty();
        assertThat(itemRequestRepository.findById(request2.getId())).isPresent();
        assertThat(itemRequestRepository.findById(request3.getId())).isPresent();
    }

    @Test
    void getRequestsByOwner_multipleRequests_success() {
        ItemRequest anotherOwnerRequest = itemRequestRepository.save(ItemRequest.builder()
                .description("Another request from owner")
                .requestor(owner)
                .created(LocalDateTime.now())
                .build());

        Collection<ItemRequestDto> result = itemRequestService.getRequestsByOwner(owner.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ItemRequestDto::getDescription)
                .containsExactlyInAnyOrder(
                        "Need a drill for home repairs",
                        "Another request from owner"
                );
    }

    @Test
    void getRequestsByNotOwner_withMultipleUsers_success() {
        Collection<ItemRequestWithoutItemsDto> result = itemRequestService.getRequestsByNotOwner(anotherUser.getId());

        assertThat(result).hasSize(2);
        assertThat(result).extracting(ItemRequestWithoutItemsDto::getDescription)
                .containsExactlyInAnyOrder("Need a drill for home repairs", "Need a saw for woodworking");
    }
}