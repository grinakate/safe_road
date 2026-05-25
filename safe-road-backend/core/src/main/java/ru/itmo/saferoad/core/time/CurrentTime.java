package ru.itmo.saferoad.core.time;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface CurrentTime {

	LocalDate nowDate();

	LocalDateTime nowDateTime();

	LocalDate weekStart();
}

