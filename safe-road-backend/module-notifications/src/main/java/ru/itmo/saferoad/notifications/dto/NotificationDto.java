package ru.itmo.saferoad.notifications.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDto {
	private Long id;
	private String title;
	private String content;
	private LocalDateTime createdAt;
	private Boolean isRead;
}

