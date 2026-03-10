package ru.practicum.data.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.data.entity.UserAction;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserActionRepository extends JpaRepository<UserAction, Long> {
    Optional<UserAction> findByUserIdAndEventId(Long userId, Long eventId);

    List<Long> findByUserId(Long userId, Pageable pageable);

    @Query("select a.eventId from UserAction a where a.userId = :userId and a.eventId != :eventId")
    Set<Long> findByUserIdExcludeEventId(long userId, long eventId);

    List<UserAction> findByEventIdIn(Set<Long> eventIds);
}
