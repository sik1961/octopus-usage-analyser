package org.neunelfer.octopus.pojo;

import lombok.*;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class OctopusDownloadRow {
    private Float ConsumptionKwh;
    private Float EstimatedCostIncTaxPence;
    private OffsetDateTime start;
    private OffsetDateTime end;
}
