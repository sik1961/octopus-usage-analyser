package org.neunelfer.octopus;

import org.neunelfer.octopus.helpers.MetoHistoricHelper;
import org.neunelfer.octopus.helpers.MonthlyUsageHelper;
import org.neunelfer.octopus.pojo.MetoHistoricRow;
import org.neunelfer.octopus.pojo.MonthlyUsage;
import org.neunelfer.octopus.pojo.OctopusUsageRow;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class OctopusAnalyserRunner {

    private static final String METO_HISTORIC_CSV_FILE = "/Users/sik/met-office/MetOfficeHistoricData.csv";
    private static final List<String> OCTOPUS_USAGE_CSV_FILES =
            Arrays.asList("/Users/sik/OctopusEnergy/Octopus-data-old-tariff.csv",
                    "/Users/sik/OctopusEnergy/Octopus-data-new-tariff.csv");
    private static final Integer BILL_DATE = 14;

    public static void main(String[] args) {

        MetoHistoricHelper metoHistoricHelper = new MetoHistoricHelper();
        MonthlyUsageHelper monthlyUsageHelper = new MonthlyUsageHelper();

        List<MetoHistoricRow> metoHistRows =
                metoHistoricHelper.getMetoHistoricRows(METO_HISTORIC_CSV_FILE);

        Map<String,Float> averageTempMap = metoHistoricHelper.getAvgTempMap(metoHistRows);

        List<OctopusUsageRow> usageRows = monthlyUsageHelper.getOctopusData(OCTOPUS_USAGE_CSV_FILES, 14, null, false);

        Map<String,MonthlyUsage> monthlyUsageMap = monthlyUsageHelper.getMonthlyUsageFromDownloadedData(usageRows, averageTempMap, BILL_DATE);



        for (String key : monthlyUsageMap.keySet()) {
            //System.out.println(">>>>>>> " + monthlyUsageMap.get(key));
        }

        for (String key : monthlyUsageMap.keySet()) {
            System.out.println(monthlyUsageMap.get(key).toCsv());
        }


    }
}