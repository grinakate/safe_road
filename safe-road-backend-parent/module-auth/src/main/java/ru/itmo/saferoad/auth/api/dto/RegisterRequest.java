package ru.itmo.saferoad.auth.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class RegisterRequest {

	@NotBlank(message = "Никнейм не может быть пустым")
	@Size(min = 3, max = 30, message = "Никнейм должен быть от 3 до 30 символов")
	@Pattern(regexp = "^[A-Za-zА-Яа-я0-9_]+$",
			message = "Никнейм может содержать только латиницу, кириллицу, цифры и подчеркивание")
	private String nickname;

	@NotBlank(message = "Email не может быть пустым")
	@Email(message = "Некорректный формат email")
	@Size(max = 50, message = "Email слишком длинный")
	private String email;

	@NotNull(message = "Дата рождения обязательна")
	@Past(message = "Дата рождения должна быть в прошлом")
	private LocalDate birthDate;

	@NotBlank(message = "Пароль не может быть пустым")
	@Size(min = 8, max = 64, message = "Длина пароля — от 8 до 64 символов")
	@Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@#$%^&+=!_\\-]).*$",
			message = "Пароль должен содержать как минимум одну букву, одну цифру и один спецсимвол (@#$%^&+=!_-)")
	private String password;
}


