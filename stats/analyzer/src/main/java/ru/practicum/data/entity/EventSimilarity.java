package ru.practicum.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

import java.time.Instant;

@Entity
@Table(name = "event_similarity", schema = "stats_service")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EventSimilarity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    Long id;

    @JoinColumn(name = "score", nullable = false)Double score;

    @JoinColumn(name = "event_a", nullable = false)
    Long eventA;

    @JoinColumn(name = "event_b", nullable = false)
    Long eventB;

    @Column(name = "timestamp", nullable = false)
    Instant timestamp;
}
