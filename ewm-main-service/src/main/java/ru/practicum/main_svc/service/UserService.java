package ru.practicum.main_svc.service;

import ru.practicum.main_svc.dto.user.NewUserRequest;
import ru.practicum.main_svc.dto.user.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(NewUserRequest newUserRequest);

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    void deleteUser(Long userId);
}
