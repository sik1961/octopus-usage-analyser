package org.neunelfer.octopus.pojo;

import lombok.*;

import java.util.Map;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class MonthlyUsage {
    public String monthId;
    private Float avgTempC;
    private Float consumedKwh;
    private Float costGbp;
    private Float pencePerKwh;

    public String toCsv() {
        return String.format("%s,%.2f,%.2f,%.2f,%.2f", monthId, avgTempC, consumedKwh, costGbp, pencePerKwh);
    }

}
