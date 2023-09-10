package ru.practicum.stat_dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.stat_dto.validation.DateTimeFormatEWM;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EndpointHitDto {
    private Long id;
    @NotNull(message = "Поле app не может быть null")
    private String app;
    @NotNull(message = "Поле uri не может быть null")
    private String uri;
    @Pattern(regexp = "^\\d+\\.\\d+\\.\\d+\\.\\d+$", message = "Недопустимое значение поля ip")
    @NotNull(message = "Поле ip не может быть null")
    private String ip;
    @DateTimeFormatEWM(message = "Недопустимый формат даты и времени. Используйте формат 'YYYY-MM-DD HH:mm:ss'")
    private String timestamp;
}
