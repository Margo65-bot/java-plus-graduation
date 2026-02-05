package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.proto.UserActionProto;
import ru.practicum.service.handler.UserActionHandler;

@Service
@RequiredArgsConstructor
public class UserActionServiceImpl implements UserActionService {
    private final UserActionHandler userActionHandler;

    @Override
    public void handleUserAction(UserActionProto userActionProto) {
        userActionHandler.handle(userActionProto);
    }
}
