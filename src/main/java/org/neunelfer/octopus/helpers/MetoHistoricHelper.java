package org.neunelfer.octopus.helpers;

import org.neunelfer.octopus.pojo.MetoHistoricRow;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class MetoHistoricHelper {

    private static final List<String> MONTHS = Arrays.asList("01","02","03","04","05","06","07","08","09","10","11","12");
    private static final List<String> interestedLocations = Arrays.asList("Leuchars","Wick_Airport");
    private static final int TWENTY_FOUR = 2024;
    private static final int I_ID = 0;
    private static final int I_NME = 1;
    private static final int I_2 = 2;
    private static final int I_3 = 3;
    private static final int I_4 = 4;
    private static final int I_5 = 5;
    private static final int I_6 = 6;
    private static final int I_7 = 7;
    private static final int I_8 = 8;
    private static final int I_9 = 9;
    private static final int I_10 = 10;
    private static final int I_11 = 11;
    private static final String NA = "n/a";
    private static final String NULL = "null";



    public List<MetoHistoricRow> getMetoHistoricRows(String historicCsvFile) {
        List<MetoHistoricRow> metoHistoricRows = new ArrayList<>();

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(historicCsvFile));

            String line;
            while ((line = br.readLine()) != null) {
               String[] e = line.split(",");

               if (e.length == 12) {
                   if(isInteresting(e)) {
                       metoHistoricRows.add(MetoHistoricRow.builder()
                           .id(e[I_ID])
                               .stationName(e[I_NME])
                               .stationLocation(e[I_2] + e[I_3] + e[I_4])
                               .monthStartDate(e[I_5])
                               .tempMaxC(getFloat(e[I_6]))
                               .tempMedC(getFloat(e[I_7]))
                               .tempMinC(getFloat(e[I_8]))
                               .afDays(getInteger(e[I_9]))
                               .rainfallMm(getFloat(e[I_10]))
                               .sunHours(getFloat(e[I_11]))
                               .build());
                   }
               } else if (e.length == 10) {
                   if(isInteresting(e)) {
                       metoHistoricRows.add(MetoHistoricRow.builder()
                               .id(e[I_ID])
                               .stationName(e[I_NME])
                               .stationLocation(e[I_2])
                               .monthStartDate(e[I_3])
                               .tempMaxC(getFloat(e[I_4]))
                               .tempMedC(getFloat(e[I_5]))
                               .tempMinC(getFloat(e[I_6]))
                               .afDays(getInteger(e[I_7]))
                               .rainfallMm(getFloat(e[I_8]))
                               .sunHours(getFloat(e[I_9]))
                               .build());
                   }

               } else {
                   throw new IllegalStateException("Unexpected row length: " + e.length + " at line " + line);
               }

            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return metoHistoricRows;
    }


    /**
     * Get avg temps for Balintore by finding the median between Wick & Leuchars
     * @param metoHistoricRows
     * @return
     */
    public Map<String,Float> getAvgTempMap(List<MetoHistoricRow> metoHistoricRows) {
        Map<String,Float> avgTempMap = new HashMap<>();

        for (int i = TWENTY_FOUR; i <= LocalDateTime.now().getYear(); i++) {
            for (String month : MONTHS) {
                List<MetoHistoricRow> monthRows =  getMonth(metoHistoricRows, Integer.toString(i) + "-" + month);

                if (monthRows.size() == 2) {
                    Float medTemp = (monthRows.get(0).getTempMedC() + monthRows.get(1).getTempMedC()) / 2;
                    avgTempMap.put((i) + "-" + month,medTemp);
                }
            }
        }
        return avgTempMap;
    }

    private List<MetoHistoricRow> getMonth(List<MetoHistoricRow> months, String yyyymm) {
        return months.stream()
                .filter(row -> row.getMonthStartDate().startsWith(yyyymm))
                .collect(Collectors.toList());
    }

    private Float getFloat(String s) {
        return (s.equalsIgnoreCase(NA) || s.equalsIgnoreCase(NULL)) ? -1.0F : Float.parseFloat(s);
    }

    private Integer getInteger(String s) {
        return (s.equalsIgnoreCase(NA) || s.equalsIgnoreCase(NULL)) ? -1 : Integer.parseInt(s);
    }

    private boolean isInteresting(String[] e) {
        return (interestedLocations.contains(e[I_NME]) && isRecentYear(e));
    }

    private boolean isRecentYear(String[] e) {
        return (e.length == 12 && Integer.parseInt(e[I_5].substring(0,4)) >= TWENTY_FOUR)
                || (e.length == 10 && Integer.parseInt(e[I_3].substring(0,4)) >= TWENTY_FOUR);
    }

}
