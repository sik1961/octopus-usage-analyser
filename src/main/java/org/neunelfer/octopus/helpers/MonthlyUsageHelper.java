package org.neunelfer.octopus.helpers;


import org.neunelfer.octopus.pojo.MonthlyUsage;
import org.neunelfer.octopus.pojo.OctopusUsageRow;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.*;

public class MonthlyUsageHelper {

    private static final String DASH = "-";
    private static final String MM_FMT = "%02d";

    public List<OctopusUsageRow> getOctopusData(List<String> csvFileNames, Integer billDate, String xlsxFileName, boolean append) {
        List<OctopusUsageRow> octopusDownloadRows = new ArrayList<>();

        for(String csvFile: csvFileNames) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(csvFile));

                String line;
                while ((line = br.readLine()) != null) {
                    String[] e = line.split(",");

                    if (e.length == 4 && !e[0].startsWith("Consumption")) {

                        octopusDownloadRows.add(OctopusUsageRow.builder()
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

    public Map<String,MonthlyUsage> getMonthlyUsageFromDownloadedData(List<OctopusUsageRow> usageRows,
                                                                      Map<String,Float> tempMap,
                                                                      Integer billDate) {
        Map<String,MonthlyUsage> monthlyUsage = new TreeMap<>();

        for (OctopusUsageRow row : usageRows) {
            MonthlyUsage currentMonth = monthlyUsage.get(getMonthId(row,billDate));
            if (currentMonth == null) {
                monthlyUsage.put(getMonthId(row,billDate),MonthlyUsage.builder()
                        .monthId(getMonthId(row, billDate))
                        .avgTempC(tempMap.get(getMonthId(row, billDate)))
                        .consumedKwh(row.getConsumptionKwh())
                        .costGbp(row.getEstimatedCostIncTaxPence()/100)
                        .build());

            } else {
                monthlyUsage.put(getMonthId(row, billDate),MonthlyUsage.builder()
                        .monthId(getMonthId(row, billDate))
                        .avgTempC(tempMap.get(getMonthId(row, billDate)))
                        .consumedKwh(currentMonth.getConsumedKwh() + row.getConsumptionKwh())
                        .costGbp(currentMonth.getCostGbp() + row.getEstimatedCostIncTaxPence()/100)
                        .build());
            }
        }
        return monthlyUsage;
    }

    private String getMonthId(OctopusUsageRow row, Integer billDate) {
        Integer year;
        Integer month;
        if (row.getStart().getDayOfMonth() >= billDate) {
           month = row.getStart().getMonthValue();
           year = row.getStart().getYear();
        } else {
            month = row.getStart().getMonthValue() - 1;
            year = row.getStart().getYear();
            if (month < 1) {
                month = 12;
                year--;
            }
        }
        return new StringBuilder().append(year).append(DASH).append(String.format(MM_FMT, month)).toString();
    }
}
