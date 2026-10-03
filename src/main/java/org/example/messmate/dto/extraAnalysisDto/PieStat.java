package org.example.messmate.dto.extraAnalysisDto;

import java.math.BigDecimal;

public record PieStat(
        String name,
        BigDecimal value
) {}
