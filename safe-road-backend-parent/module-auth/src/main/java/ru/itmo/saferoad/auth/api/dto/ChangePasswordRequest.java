package ru.itmo.saferoad.auth.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {

	@NotBlank(message = "Старый пароль не может быть пустым")
	private String oldPassword;

	@NotBlank(message = "Новый пароль не может быть пустым")
    @Size(min = 8, max = 64, message = "Длина пароля — от 8 до 64 символов")
	private String newPassword;
}
