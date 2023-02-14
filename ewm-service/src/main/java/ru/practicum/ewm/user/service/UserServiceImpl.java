package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.common.PageParam;
import ru.practicum.ewm.exception.DataValidationException;
import ru.practicum.ewm.user.dao.UserRepository;
import ru.practicum.ewm.user.dto.NewUserDto;
import ru.practicum.ewm.user.dto.ShortUserDto;
import ru.practicum.ewm.user.dto.UserRespDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;

import javax.persistence.EntityNotFoundException;
import java.sql.SQLException;
import java.util.List;

import static ru.practicum.ewm.common.PageParam.pageRequest;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Sort SORT_TYPE = Sort.by("id").descending();
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<UserRespDto> getUsers(PageParam pageParam, List<Long> usersId) {
        List<User> foundUsers;

        if (usersId != null && !usersId.isEmpty()) {
            foundUsers = userRepository.findAllByUsersId(usersId, pageRequest(pageParam, SORT_TYPE));
        } else {
            foundUsers = userRepository.findAll(pageRequest(pageParam, SORT_TYPE)).toList();
        }

        log.debug("Users found: {}", foundUsers);
        return userMapper.toUsersRespDto(foundUsers);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public UserRespDto addUser(NewUserDto newUserDto) {
        User createdUser;

        try {
            createdUser = userRepository.save(userMapper.toUser(newUserDto));
            log.debug("Saved: {}", createdUser);
        } catch (DataIntegrityViolationException ex) {
            log.warn("Save user error", ex);
            throw new DataValidationException(String.format("User with email %s already exist", newUserDto.getEmail()));
        }

        return userMapper.toUserRespDto(createdUser);
    }

    @Transactional(rollbackFor = SQLException.class, isolation = Isolation.SERIALIZABLE)
    public void removeUser(Long userId) {
        findUserById(userId);

        userRepository.deleteById(userId);
        log.debug("User with id {} was deleted", userId);
    }

    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return findUserById(userId);
    }

    @Transactional(readOnly = true)
    public List<ShortUserDto> getShortUsers(PageParam pageParam, List<Long> usersId) {
        List<User> foundUsers = userRepository.findAllByUsersId(usersId, pageRequest(pageParam, SORT_TYPE));

        log.debug("Users found: {}", foundUsers);
        return userMapper.toShortUserDto(foundUsers);
    }

    private User findUserById(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("UserID %s does not exist", userId)));

        log.debug("User with id {} was found", userId);
        return user;
    }
}
