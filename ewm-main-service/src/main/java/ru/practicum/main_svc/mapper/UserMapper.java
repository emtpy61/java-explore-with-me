package ru.practicum.main_svc.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practicum.main_svc.dto.user.NewUserRequest;
import ru.practicum.main_svc.dto.user.UserDto;
import ru.practicum.main_svc.model.User;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    User toModel(NewUserRequest newUserRequest);

    UserDto toDto(User user);

    List<UserDto> toDtoList(List<User> usersList);
}