

package com.airedale.StationCreation.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumericUtils {
    public NumericUtils() {
    }

    public static int getMinimumOfThreeIntegers(int a, int b, int c) {
        int minAB = Math.min(a, b);
        int minABC = Math.min(minAB, c);
        return minABC;
    }

    public static boolean isEssentiallyZero(double d, double allowedVariance) {
        return d + allowedVariance > 0.0 && d - allowedVariance < 0.0;
    }

    public static boolean areEssentiallyEqual(double d1, double d2, double allowedVariance) {
        double delta = d1 - d2;
        return delta + allowedVariance > 0.0 && delta - allowedVariance < 0.0;
    }

    public static boolean isNotNegative(BigDecimal numberToCheck) {
        return numberToCheck.compareTo(BigDecimal.valueOf(0L)) != -1;
    }

    public static BigDecimal roundBigDecimal(BigDecimal value, int decimalPlaces) {
        if (decimalPlaces < 0) {
            throw new IllegalArgumentException();
        } else {
            return value.setScale(decimalPlaces, RoundingMode.HALF_UP);
        }
    }

    public static double round(double value, int decimalPlaces) {
        if (decimalPlaces < 0) {
            throw new IllegalArgumentException();
        } else {
            BigDecimal bd = new BigDecimal(value);
            bd = roundBigDecimal(bd, decimalPlaces);
            return bd.doubleValue();
        }
    }

    public static double roundToMultiple(double num, int multipleOf) {
        return Math.floor((num + (double)multipleOf) / (double)multipleOf) * (double)multipleOf;
    }

    public static double degreesToRadians(double degree) {
        return degree * Math.PI / 180.0;
    }

    public static double radiansToDegrees(double radian) {
        return radian * 180.0 / Math.PI;
    }

    public static double square(double f) {
        return f * f;
    }

    public static double linearInterpolation(double a, double b, double ratio) {
        return a + (b - a) * ratio;
    }
}
