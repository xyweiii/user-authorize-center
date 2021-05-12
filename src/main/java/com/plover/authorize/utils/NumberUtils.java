package com.plover.authorize.utils;

import java.math.RoundingMode;
import java.text.NumberFormat;

public class NumberUtils {

    public static String format2(double value) {
        NumberFormat nf = NumberFormat.getNumberInstance();
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        nf.setRoundingMode(RoundingMode.HALF_UP);
        return nf.format(value);
    }
}
