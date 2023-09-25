package ru.practicum.main_svc.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main_svc.dto.category.CategoryDto;
import ru.practicum.main_svc.dto.category.NewCategoryDto;
import ru.practicum.main_svc.dto.comment.CommentDto;
import ru.practicum.main_svc.dto.comment.NewCommentDto;
import ru.practicum.main_svc.dto.compilation.CompilationDto;
import ru.practicum.main_svc.dto.compilation.NewCompilationDto;
import ru.practicum.main_svc.dto.compilation.UpdateCompilationRequest;
import ru.practicum.main_svc.dto.event.EventFullDto;
import ru.practicum.main_svc.dto.event.UpdateEventRequest;
import ru.practicum.main_svc.dto.user.NewUserRequest;
import ru.practicum.main_svc.dto.user.UserDto;
import ru.practicum.main_svc.enums.EventState;
import ru.practicum.main_svc.service.*;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/admin")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminApiController {
    private final CategoryService categoryService;
    private final UserService userService;
    private final CompilationService compilationService;
    private final EventService eventService;
    private final CommentService commentService;

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@Valid @RequestBody NewCategoryDto body) {
        return categoryService.createCategory(body);
    }

    @PatchMapping("/categories/{catId}")
    public CategoryDto updateCategory(@PathVariable("catId") Long catId,
            @Valid @RequestBody CategoryDto body) {
        return categoryService.updateCategory(catId, body);
    }

    @DeleteMapping("/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable("catId") Long catId) {
        categoryService.deleteCategory(catId);
    }

    @PostMapping("/users")
    @ResponseStatus(value = HttpStatus.CREATED)
    public UserDto registerUser(@Valid @RequestBody NewUserRequest body) {
        return userService.createUser(body);
    }

    @GetMapping("/users")
    public List<UserDto> getUsers(@Valid @RequestParam(value = "ids", required = false) List<Long> ids,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        return userService.getUsers(ids, from, size);
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("userId") Long userId) {
        userService.deleteUser(userId);
    }

    @GetMapping("/events")
    public List<EventFullDto> getEvents(
            @Valid @RequestParam(value = "users", required = false) List<Long> users,
            @Valid @RequestParam(value = "states", required = false) List<EventState> states,
            @Valid @RequestParam(value = "categories", required = false) List<Long> categories,
            @Valid @RequestParam(value = "rangeStart", required = false) String rangeStart,
            @Valid @RequestParam(value = "rangeEnd", required = false) String rangeEnd,
            @RequestParam(name = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(name = "size", defaultValue = "10") @Positive Integer size) {
        return eventService.getEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable("eventId") Long eventId,
            @Valid @RequestBody UpdateEventRequest body) {
        return eventService.updateEventByAdmin(eventId, body);
    }

    @PostMapping("/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto saveCompilation(@Valid @RequestBody NewCompilationDto body) {
        return compilationService.createCompilation(body);
    }

    @PatchMapping("/compilations/{compId}")
    public CompilationDto updateCompilation(@PathVariable("compId") Long compId,
            @Valid @RequestBody UpdateCompilationRequest body) {
        return compilationService.updateCompilation(compId, body);
    }

    @DeleteMapping("/compilations/{compId}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable("compId") Long compId) {
        compilationService.deleteCompilation(compId);
    }

    @GetMapping("/comments/{commentId}")
    public CommentDto getCommentById(@PathVariable(value = "commentId") Long commentId) {
        return commentService.getCommentById(commentId);
    }

    @PatchMapping("/comments/{commentId}")
    public CommentDto updateComment(
            @PathVariable(value = "commentId") Long commentId,
            @Valid @RequestBody NewCommentDto body) {
        return commentService.updateCommentByAdmin(body, commentId);
    }

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentByUser(@PathVariable(value = "commentId") Long commentId) {
        commentService.deleteCommentByAdmin(commentId);
    }
}
