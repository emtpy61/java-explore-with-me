package ru.practicum.main_svc.service.impl;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_svc.dto.user.NewUserRequest;
import ru.practicum.main_svc.dto.user.UserDto;
import ru.practicum.main_svc.exception.ewm.NameAlreadyExistException;
import ru.practicum.main_svc.mapper.UserMapper;
import ru.practicum.main_svc.model.User;
import ru.practicum.main_svc.repository.UserRepository;
import ru.practicum.main_svc.service.UserService;

import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto createUser(NewUserRequest newUserRequest) {
        if (userRepository.existsByName(newUserRequest.getName())) {
            String errorMessage = String.format("Unable to create a user. A user with the name %s already exists",
                    newUserRequest.getName());
            log.warn(errorMessage);
            throw new NameAlreadyExistException(errorMessage);
        }
        User user = userMapper.toModel(newUserRequest);
        user = userRepository.save(user);
        log.debug(String.format("A user named %s has been created", user.getName()));
        return userMapper.toDto(user);
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        Pageable page = PageRequest.of(from / size, size);
        List<User> users = null;
        if (ids == null || ids.isEmpty()) {
            users = userRepository.findAll(page).toList();
        } else {
            users = userRepository.findAllById(ids);
        }
        return userMapper.toDtoList(users);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
}
