package org.neunelfer.octopus.helpers;


import org.neunelfer.octopus.pojo.MetoHistoricRow;
import org.neunelfer.octopus.pojo.MonthlyUsage;
import org.neunelfer.octopus.pojo.OctopusDownloadRow;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.*;

public class MonthlyUsageHelper {

    public List<OctopusDownloadRow> getOctopusData(List<String> csvFileNames, String xlsxFileName, boolean append) {
        List<OctopusDownloadRow> octopusDownloadRows = new ArrayList<>();

        for(String csvFile: csvFileNames) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(csvFile));

                String line;
                while ((line = br.readLine()) != null) {
                    String[] e = line.split(",");

                    if (e.length == 4 && !e[0].startsWith("Consumption")) {

                        octopusDownloadRows.add(OctopusDownloadRow.builder()
                                .ConsumptionKwh(Float.parseFloat(e[0]))
                                .EstimatedCostIncTaxPence(Float.parseFloat(e[1]))
                                .start(OffsetDateTime.parse(e[2].trim()))
                                .end(OffsetDateTime.parse(e[3].trim()))
                                .build());
                    }
                }
                br.close();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return octopusDownloadRows;
    }

    public Map<String,MonthlyUsage> getMonthlyUsageFromDownloadedData(List<OctopusDownloadRow> downloadedRows,
                                                                      Map<String,Float> tempMap) {
        Map<String,MonthlyUsage> monthlyUsage = new TreeMap<>();

        for (OctopusDownloadRow row : downloadedRows) {
            MonthlyUsage currentMonth = monthlyUsage.get(getMonthId(row));
            if (currentMonth == null) {
                monthlyUsage.put(getMonthId(row),MonthlyUsage.builder()
                        .monthId(getMonthId(row))
                        .avgTempC(tempMap.get(getMonthId(row)))
                        .consumedKwh(row.getConsumptionKwh())
                        .costGbp(row.getEstimatedCostIncTaxPence()/100)
                        .pencePerKwh(row.getEstimatedCostIncTaxPence()/row.getConsumptionKwh())
                        .build());

            } else {
                monthlyUsage.put(getMonthId(row),MonthlyUsage.builder()
                        .monthId(getMonthId(row))
                        .avgTempC(tempMap.get(getMonthId(row)))
                        .consumedKwh(currentMonth.getConsumedKwh() + row.getConsumptionKwh())
                        .costGbp(currentMonth.getCostGbp() + row.getEstimatedCostIncTaxPence()/100)
                        .pencePerKwh(row.getEstimatedCostIncTaxPence()/row.getConsumptionKwh())
                        .build());
            }
        }
        return monthlyUsage;
    }

    private String getMonthId(OctopusDownloadRow row) {
        return row.getStart().toString().substring(0, 7);
    }
}
