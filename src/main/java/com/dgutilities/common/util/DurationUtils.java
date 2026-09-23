package com.dgutilities.common.util;

public class DurationUtils {
    public static long parseDuration(String input) {
        if (input == null || input.length() < 2) return -1L;

        input = input.toLowerCase();
        char unit = input.charAt(input.length() - 1);
        String numberPart = input.substring(0, input.length() - 1);
        long value;

        try {
            value = Long.parseLong(numberPart);
        } catch (NumberFormatException e) {
            return -1L;
        }

        if (value <= 0) return -1L;

        try {
            return switch (unit) {
                case 's' -> Math.multiplyExact(value, 1000L);
                case 'm' -> Math.multiplyExact(value, 60_000L);
                case 'h' -> Math.multiplyExact(value, 3_600_000L);
                case 'd' -> Math.multiplyExact(value, 86_400_000L);
                default -> -1L;
            };
        } catch (ArithmeticException e) {
            return -1L;
        }
    }

    public static String formatRemaining(long expirationTime) {
        if (expirationTime == 0L) return "Permanent";
        long remaining = expirationTime - System.currentTimeMillis();
        if (remaining <= 0) return "Expired";
        long totalSeconds = remaining / 1000;
        long days = totalSeconds / 86400;
        totalSeconds %= 86400;
        long hours = totalSeconds / 3600;
        totalSeconds %= 3600;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        if (days > 0) return days + "d " + hours + "h";
        if (hours > 0) return hours + "h " + minutes + "m";
        if (minutes > 0) return minutes + "m " + seconds + "s";
        return seconds + "s";
    }
}