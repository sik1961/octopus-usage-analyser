package org.neunelfer.octopus.pojo;

import lombok.*;

import java.time.OffsetDateTime;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class OctopusUsageRow {
    private Float consumptionKwh;
    private Float estimatedCostIncTaxPence;
    private Float standingCharge;
    private OffsetDateTime start;
    private OffsetDateTime end;
}
