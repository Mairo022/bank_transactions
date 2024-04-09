package com.playtech.assignment.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalCustom {
    public static BigDecimal BigDecimalCC(String number) {
        return new BigDecimal(number).setScale(2, RoundingMode.DOWN);
    }
}
