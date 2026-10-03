package org.example.messmate.dto.extraAnalysisDto;

import java.math.BigDecimal;

public record TrendStat(
        String date,
        BigDecimal total
) {}
