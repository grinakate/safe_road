package ru.itmo.saferoad.gamification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@IdClass(XpHistoryId.class)
@Table(name = "xp_history")
@EqualsAndHashCode(of = {"userId", "weekStart"})
public class XpHistory {

    @Id
    @NonNull
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Id
    @NonNull
    @Column(name = "week_start", nullable = false)
    private LocalDate weekStart;

    @NonNull
    @Column(name = "xp", nullable = false)
    private Long xp;

    @NonNull
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}

