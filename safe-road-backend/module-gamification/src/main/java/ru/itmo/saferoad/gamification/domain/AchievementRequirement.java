package ru.itmo.saferoad.gamification.domain;

/**
 * Record representing parsed achievement requirement JSON.
 */
public record AchievementRequirement(String actionType, long value) {
}

