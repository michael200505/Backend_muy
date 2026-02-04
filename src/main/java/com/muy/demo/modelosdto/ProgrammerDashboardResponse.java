package com.muy.demo.modelosdto;

import java.util.List;
import java.util.Map;

public class ProgrammerDashboardResponse {
    public Map<String, Long> advisoriesByStatus;
    public long activeProjects;
    public List<UpcomingAdvisoryItem> upcoming;
    public List<MonthlyCount> monthly;

    public static class UpcomingAdvisoryItem {
        public Long id;
        public String startAt;
        public String endAt;
        public String externalName;
        public String topic;
        public String modality;
    }

    public static class MonthlyCount {
        public String yearMonth; // "2026-02"
        public Long count;
    }
}
