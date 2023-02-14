package ru.practicum.ewm.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.common.PageParam;
import ru.practicum.ewm.user.dto.NewUserDto;
import ru.practicum.ewm.user.dto.UserRespDto;
import ru.practicum.ewm.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping()
    public List<UserRespDto> getUsers(@Valid PageParam pageParam,
                                      @RequestParam(required = false) List<Long> ids) {
        return userService.getUsers(pageParam, ids);
    }

    @PostMapping()
    @ResponseStatus(code = HttpStatus.CREATED)
    public UserRespDto addUser(@Valid @NotNull @RequestBody NewUserDto newUserDto) {
        return userService.addUser(newUserDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void removeUser(@PathVariable Long userId) {
        userService.removeUser(userId);
    }
}
