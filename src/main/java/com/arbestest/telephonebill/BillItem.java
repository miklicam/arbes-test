package com.arbestest.telephonebill;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class BillItem {

    private BigDecimal phone;
    private LocalDateTime from;
    private LocalDateTime to;
}
