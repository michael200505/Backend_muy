package com.muy.demo.modelosdto;

import java.util.List;
import java.util.Map;

public class AdvisoryDashboardFilterResponse {
    public Map<String, Long> countsByStatus;
    public List<Item> items;

    public static class Item {
        public Long id;
        public String startAt;
        public String endAt;
        public String externalName;
        public String status;
        public String modality;
        public String topic;
    }
}
