package org.neunelfer.octopus.pojo;

import lombok.*;

import java.time.OffsetDateTime;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class OctopusUsageRow {
    private Float ConsumptionKwh;
    private Float EstimatedCostIncTaxPence;
    private OffsetDateTime start;
    private OffsetDateTime end;
}
