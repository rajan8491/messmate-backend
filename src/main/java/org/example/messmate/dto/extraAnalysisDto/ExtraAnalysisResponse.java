package org.example.messmate.dto.extraAnalysisDto;

import java.util.List;

public record ExtraAnalysisResponse(
        GeneralStats generalStats,
        List<TrendStat> trendStats
) {}
