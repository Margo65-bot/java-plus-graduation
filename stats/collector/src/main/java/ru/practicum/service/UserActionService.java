package ru.practicum.service;

import ru.practicum.ewm.stats.proto.UserActionProto;

public interface UserActionService {
    void handleUserAction(UserActionProto userActionProto);
}
