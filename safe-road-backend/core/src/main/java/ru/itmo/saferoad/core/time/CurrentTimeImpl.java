package ru.itmo.saferoad.core.time;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Objects;

@Component
public class CurrentTimeImpl implements CurrentTime {

	private final Clock clock;

	public CurrentTimeImpl() {
		this(Clock.systemDefaultZone());
	}

	public CurrentTimeImpl(Clock clock) {
		this.clock = Objects.requireNonNull(clock);
	}

	@Override
	public LocalDate nowDate() {
		return LocalDate.now(clock);
	}

	@Override
	public LocalDateTime nowDateTime() {
		return LocalDateTime.now(clock);
	}

	@Override
	public LocalDate weekStart() {
		LocalDate today = nowDate();
		return today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
	}
}

