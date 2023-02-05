package ru.practicum.ewm.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.RequestRespDto;
import ru.practicum.ewm.request.service.RequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @GetMapping("/{userId}/requests")
    public List<RequestRespDto> getRequests(@PathVariable Long userId) {
        return requestService.getRequests(userId);
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(code = HttpStatus.CREATED)
    public RequestRespDto addRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        return requestService.addRequest(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public RequestRespDto cancelRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        return requestService.cancelRequest(userId, requestId);
    }
}
