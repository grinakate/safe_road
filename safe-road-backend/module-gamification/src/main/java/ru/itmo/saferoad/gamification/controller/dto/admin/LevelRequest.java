package ru.itmo.saferoad.gamification.controller.dto.admin;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LevelRequest {
	@NotBlank(message = "Название уровня обязательно")
	private String title;

	@NotNull(message = "Порядковый номер уровня обязателен")
	@Min(value = 1, message = "Номер уровня должен быть больше 0")
	private Integer number;

	@NotNull(message = "Порог опыта обязателен")
	@Min(value = 0, message = "Порог опыта не может быть отрицательным")
	private Integer xpThreshold;
}
