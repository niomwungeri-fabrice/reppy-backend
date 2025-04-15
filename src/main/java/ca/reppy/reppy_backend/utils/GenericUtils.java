package ca.reppy.reppy_backend.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenericUtils {
    private static final Pattern WEIGHT_PATTERN = Pattern.compile("(\\d+(\\.\\d+)?)\\s?(lb|lbs)");

    public static ParsedEquipment parse(String rawName) {
        String lower = rawName.toLowerCase();

        // Try to extract weight
        Double weight = null;
        Matcher matcher = WEIGHT_PATTERN.matcher(lower);
        if (matcher.find()) {
            weight = Double.parseDouble(matcher.group(1));
        }

        // Remove weight from name to extract type
        String cleaned = lower.replaceAll("(\\d+(\\.\\d+)?)\\s?(lb|lbs)", "").replaceAll("\\s+", " ").trim();

        return new ParsedEquipment(cleaned, weight);
    }

    public record ParsedEquipment(String type, Double weight) {}
}
