package ru.itmo.saferoad.content.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateSectionRequest(
		@NotBlank String name,
		@NotNull Integer orderIndex
) {
}

