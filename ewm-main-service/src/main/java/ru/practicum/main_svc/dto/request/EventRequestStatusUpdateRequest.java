package ru.practicum.main_svc.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.main_svc.enums.RequestStatus;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EventRequestStatusUpdateRequest {
    private List<Long> requestIds;
    private RequestStatus status;
}
