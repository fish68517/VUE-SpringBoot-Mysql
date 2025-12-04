package com.shenyang.musicfestival.util;

/**
 * Utility class for data masking operations
 */
public class MaskingUtil {

    /**
     * Mask ID number to show only last 4 digits
     * Example: 110101199003071234 -> ****3071234
     */
    public static String maskIdNumber(String idNumber) {
        if (idNumber == null || idNumber.length() < 4) {
            return idNumber;
        }
        int length = idNumber.length();
        String lastFour = idNumber.substring(length - 4);
        String masked = "*".repeat(length - 4) + lastFour;
        return masked;
    }

}
