package ru.itmo.saferoad.auth.api.dto;

import jakarta.validation.constraints.NotBlank;
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

	@NotBlank(message = "Никнейм не может быть пустым")
	@Size(min = 3, max = 30, message = "Никнейм должен быть от 3 до 30 символов")
	@Pattern(regexp = "^[A-Za-zА-Яа-я0-9_]+$",
			message = "Никнейм может содержать только латиницу, кириллицу, цифры и подчеркивание")
	private String nickname;

	@Past(message = "Дата рождения должна быть в прошлом")
	private LocalDate birthDate;
}

