package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class RequestController {
    RequestClient requestClient;
    static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createRequest(@RequestBody @Valid ItemRequestCreateDto itemRequestCreateDto,
                                                @RequestHeader(USER_ID_HEADER) Long userId) {
        return requestClient.createRequest(itemRequestCreateDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsForUser(@RequestHeader(USER_ID_HEADER) Long userId) {
        return requestClient.getRequestsForUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getRequests() {
        return requestClient.getRequests();
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader(USER_ID_HEADER) Long userId,
                                             @PathVariable @Positive Long requestId) {
        return requestClient.getRequest(userId, requestId);
    }
}
