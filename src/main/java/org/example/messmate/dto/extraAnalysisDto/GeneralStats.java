package org.example.messmate.dto.extraAnalysisDto;

import java.math.BigDecimal;
import java.util.List;

public record GeneralStats(
        BigDecimal total,
        long count,
        BigDecimal avgPerDay,
        List<PieStat> pie,
        List<ItemStat> items
) {}
