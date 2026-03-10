package ru.practicum.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;

import java.time.Instant;

@Entity
@Table(name = "user_action", schema = "stats_service")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @JoinColumn(name = "user_id", nullable = false)
    Long userId;

    @JoinColumn(name = "event_id", nullable = false)
    Long eventId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action_type", length = 20, nullable = false)
    ActionTypeAvro actionType;

    @Column(name = "timestamp", nullable = false)
    Instant timestamp;
}
