package br.com.agrologqueue.api.model.enums;

import lombok.Getter;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
public enum ReportPeriod {
    TODAY("Hoje"),
    YESTERDAY("Ontem"),
    LAST_7_DAYS("Últimos 7 Dias");

    private final String label;

    ReportPeriod(String label) {
        this.label = label;
    }

    public LocalDateTime getStart() {
        return switch (this) {
            case TODAY -> LocalDateTime.now().with(LocalTime.MIN);
            case YESTERDAY -> LocalDateTime.now().minusDays(1).with(LocalTime.MIN);
            case LAST_7_DAYS -> LocalDateTime.now().minusDays(7).with(LocalTime.MIN);
        };
    }

    public LocalDateTime getEnd() {
        return switch (this) {
            case YESTERDAY -> LocalDateTime.now().minusDays(1).with(LocalTime.MAX);
            default -> LocalDateTime.now().with(LocalTime.MAX);
        };
    }
}