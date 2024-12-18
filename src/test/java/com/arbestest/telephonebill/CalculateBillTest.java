package com.arbestest.telephonebill;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CalculateBillTest {

    static String csv1 =
            "2,13-01-2020 18:10:15,13-01-2020 18:11:57\n" +
            "1,13-01-2020 18:10:15,13-01-2020 18:11:57\n" +
            "2,18-01-2020 08:50:20,18-01-2020 08:54:55\n" +
            "3,18-01-2020 08:59:20,18-01-2020 09:10:00\n" +
            "3,18-01-2020 08:59:20,18-01-2020 09:10:00\n";

    static String csv2 = "1,13-01-2020 18:10:15,13-01-2020 18:12:57";

    static String csv3 =
            "1,13-01-2020 18:10:15,13-01-2020 18:10:57\n" +
            "2,18-01-2020 18:50:20,18-01-2020 18:50:55" ;

    static String csv4 =
            "1,13-01-2020 18:10:15,13-01-2020 18:12:57\n" +
            "2,18-01-2020 18:50:20,18-01-2020 18:50:55" ;

    static String csv5 =
            "1,13-01-2020 18:10:15,13-01-2020 18:15:57\n" +
            "2,18-01-2020 18:50:20,18-01-2020 18:50:55" ;

    static String csv6 =
            "1,13-01-2020 07:59:15,13-01-2020 08:01:57\n" +
            "2,18-01-2020 18:50:20,18-01-2020 18:50:55" ;
    static String csv7 =
            "1,13-01-2020 15:59:15,13-01-2020 16:03:57\n" +
            "2,18-01-2020 18:50:20,18-01-2020 18:50:55" ;

    @Test
    void calculateBaseRate() {
        CalculateBill calculateBill = new CalculateBill();
        BigDecimal total = calculateBill.calculate(csv3);
        // test jednoho radku a zakladni sazby
        assertEquals(BigDecimal.valueOf(0.5), total);

        total = calculateBill.calculate(csv4);
        // vypocet zakladni sazby delsi nez minuta ale mensi nez 5 minut
        assertEquals(BigDecimal.valueOf(1.5), total);

        total = calculateBill.calculate(csv5);
        // vypocet zakladni sazby delsi nez minuta ale delsi nez 5 minut
        assertEquals(BigDecimal.valueOf(2.7), total);
    }

    @Test
    void calculateExpensiveRate() {
        CalculateBill calculateBill = new CalculateBill();
        BigDecimal total = calculateBill.calculate(csv6);
        // test hranicni hodnoty 8. hodiny
        assertEquals(BigDecimal.valueOf(2.5), total);

        total = calculateBill.calculate(csv7);
        // test hranicni hodnoty 16. hodiny
        assertEquals(BigDecimal.valueOf(3.0), total);
    }


    @Test
    void calculateWithoutMostFrequentNumber() {
        CalculateBill calculateBill = new CalculateBill();
        BigDecimal total = calculateBill.calculate(csv2);
        // podle zadani a bodu s promo akci. Tady je jenom jedno cislo, tak vylucuji
        assertEquals(BigDecimal.valueOf(0), total);

        total = calculateBill.calculate(csv1);
        // vylouceni cisla nejvyssiho hodnoty a test vice radku
        assertEquals(BigDecimal.valueOf(7.0), total);
    }

}