package com.arbestest.telephonebill;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class CalculateBill implements TelephoneBillCalculator {

    private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";
    private static final String NEW_LINE = "\n";
    private static final String NEW_COLUMN = ",";

    @Override
    public BigDecimal calculate(String phoneLog) {
        Map<BigDecimal, List<BillItem>> map = parseCsv(phoneLog);

        // najdi nejfrekventovanejsi a odeber
        BigDecimal mostFrequentNumber = findMostFrequentNumber(map);
        map.remove(mostFrequentNumber);

        // spocitej postupne pro kazde cislo
        AtomicReference<BigDecimal> total = new AtomicReference<>(BigDecimal.ZERO);
        map.forEach((bigDecimal, billItems) ->
            total.set(total.get().add(total.get().add(calculateMapItem(billItems))))
        );

        return total.get();
    }

    private BigDecimal calculateMapItem(List<BillItem> items) {
        AtomicReference<BigDecimal> total = new AtomicReference<>(BigDecimal.ZERO);
        items.forEach(billItem ->
            total.set(total.get().add(calculateBillItem(billItem)))
        );
        return total.get();
    }

    private BigDecimal calculateBillItem(BillItem billItem) {
        LocalDateTime from = billItem.getFrom().withSecond(0);
        boolean moreThanFiveMinutes = false;
        BigDecimal total = getValueByTime(from, moreThanFiveMinutes);

        int i = 0;
        while (from.isBefore(billItem.getTo().withSecond(0))) {
            from = from.plusMinutes(1);
            i++;
            if (i > 4) {
                moreThanFiveMinutes = true;
            }
            total = total.add(getValueByTime(from, moreThanFiveMinutes));
        }
        return total;
    }

    private BigDecimal getValueByTime(LocalDateTime time, boolean moreThanFiveMinute) {
        if (moreThanFiveMinute) {
            return BigDecimal.valueOf(0.2);
        } else if (checkExpensiveRate(time)) {
            return BigDecimal.valueOf(1);
        } else {
            return BigDecimal.valueOf(0.5);
        }
    }

    private Map<BigDecimal, List<BillItem>> parseCsv(String phoneLog) {
        Map<BigDecimal, List<BillItem>> map = new HashMap<>();
        String[] lines = phoneLog.split(NEW_LINE);
        Arrays.stream(lines).map(line -> line.split(NEW_COLUMN)).forEach(parts -> {
            BigDecimal phoneNumber = new BigDecimal(parts[0]);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

            LocalDateTime startTime = LocalDateTime.parse(parts[1], formatter);
            LocalDateTime endTime = LocalDateTime.parse(parts[2], formatter);
            if (!map.containsKey(phoneNumber)) {
                map.put(phoneNumber, new ArrayList<>());
            }

            map.get(phoneNumber).add(new BillItem(phoneNumber, startTime, endTime));
        });
        return map;
    }

    private boolean checkExpensiveRate(LocalDateTime time) {
        return time.getHour() >= 8 && time.getHour() < 16;
    }

    private BigDecimal findMostFrequentNumber(Map<BigDecimal, List<BillItem>> map) {
        Map<BigDecimal, Integer> countMap = new HashMap<>();
        map.forEach((key, value) -> countMap.put(key, value.size()));

        int maxCount = Collections.max(countMap.values());
        return countMap.entrySet().stream()
                .filter(entry -> entry.getValue() == maxCount)
                .map(Map.Entry::getKey)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
    }

}
