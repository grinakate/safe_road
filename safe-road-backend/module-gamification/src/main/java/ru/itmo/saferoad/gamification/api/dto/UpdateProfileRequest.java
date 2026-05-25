package ru.itmo.saferoad.gamification.api.dto;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

	@Size(min = 3, max = 30, message = "Никнейм должен быть от 3 до 30 символов")
	@Pattern(regexp = "^[A-Za-zА-Яа-я0-9_]+$",
			message = "Никнейм может содержать только латиницу, кириллицу, цифры и подчеркивание")
	private String nickname;

	@Past(message = "Дата рождения должна быть в прошлом")
	private LocalDate birthDate;

	@Size(min = 8, max = 64, message = "Длина пароля — от 8 до 64 символов")
	private String password;

	private Boolean leaderboardEnabled;

	private Boolean notificationsEnabled;
}

