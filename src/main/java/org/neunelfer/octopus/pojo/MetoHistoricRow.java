package org.neunelfer.octopus.pojo;

import lombok.*;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class MetoHistoricRow {
    private String id;
    private String stationName;
    private String stationLocation;
    private String monthStartDate;
    private Float tempMaxC;
    private Float tempMedC;
    private Float tempMinC;
    private Integer afDays;
    private Float rainfallMm;
    private Float sunHours;

}
