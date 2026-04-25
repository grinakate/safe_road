package ru.itmo.saferoad.learning.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.itmo.saferoad.learning.domain.enums.ProgressStatus;

@Data
@NoArgsConstructor
@Entity
@Table(name = "user_section_progress")
public class UserSectionProgress {
  @EmbeddedId private UserSectionProgressId id;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, columnDefinition = "progress_status")
  private ProgressStatus status;
}
