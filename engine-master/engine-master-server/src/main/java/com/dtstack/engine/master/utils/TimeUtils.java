package com.dtstack.engine.master.utils;

import com.dtstack.engine.common.util.DateUtil;
import com.dtstack.engine.common.util.StringUtils;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Collection of utilities about time intervals.
 */
public class TimeUtils {

    private static final Map<String, ChronoUnit> LABEL_TO_UNIT_MAP =
            Collections.unmodifiableMap(initMap());

    private static final Pattern TIME_PATTERN = Pattern.compile("([0-1][0-9]|2[0-3]):[0-5][0-9]");

    /**
     * Parse the given string to a java {@link Duration}. The string is in format "{length
     * value}{time unit label}", e.g. "123ms", "321 s". If no time unit label is specified, it will
     * be considered as milliseconds.
     *
     * <p>Supported time unit labels are:
     *
     * <ul>
     *   <li>DAYS： "d", "day"
     *   <li>HOURS： "h", "hour"
     *   <li>MINUTES： "min", "minute"
     *   <li>SECONDS： "s", "sec", "second"
     *   <li>MILLISECONDS： "ms", "milli", "millisecond"
     *   <li>MICROSECONDS： "µs", "micro", "microsecond"
     *   <li>NANOSECONDS： "ns", "nano", "nanosecond"
     * </ul>
     *
     * @param text string to parse.
     */
    public static Duration parseDuration(String text) {
        Objects.requireNonNull(text);

        final String trimmed = text.trim();
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("argument is an empty- or whitespace-only string");
        }

        final int len = trimmed.length();
        int pos = 0;

        char current;
        while (pos < len && (current = trimmed.charAt(pos)) >= '0' && current <= '9') {
            pos++;
        }

        final String number = trimmed.substring(0, pos);
        final String unitLabel = trimmed.substring(pos).trim().toLowerCase(Locale.US);

        if (number.isEmpty()) {
            throw new NumberFormatException("text does not start with a number");
        }

        final long value;
        try {
            value = Long.parseLong(number); // this throws a NumberFormatException on overflow
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "The value '"
                            + number
                            + "' cannot be re represented as 64bit number (numeric overflow).");
        }

        if (unitLabel.isEmpty()) {
            return Duration.of(value, ChronoUnit.MILLIS);
        }

        ChronoUnit unit = LABEL_TO_UNIT_MAP.get(unitLabel);
        if (unit != null) {
            return Duration.of(value, unit);
        } else {
            throw new IllegalArgumentException(
                    "Time interval unit label '"
                            + unitLabel
                            + "' does not match any of the recognized units: "
                            + TimeUnit.getAllUnits());
        }
    }

    private static Map<String, ChronoUnit> initMap() {
        Map<String, ChronoUnit> labelToUnit = new HashMap<>();
        for (TimeUnit timeUnit : TimeUnit.values()) {
            for (String label : timeUnit.getLabels()) {
                labelToUnit.put(label, timeUnit.getUnit());
            }
        }
        return labelToUnit;
    }

    /**
     * @param duration to convert to string
     * @return duration string in millis
     */
    public static String getStringInMillis(final Duration duration) {
        return duration.toMillis() + TimeUnit.MILLISECONDS.labels.get(0);
    }

    /**
     * Pretty prints the duration as a lowest granularity unit that does not lose precision.
     *
     * <p>Examples:
     *
     * <pre>{@code
     * Duration.ofMilliseconds(60000) will be printed as 1 min
     * Duration.ofHours(1).plusSeconds(1) will be printed as 3601 s
     * }</pre>
     *
     * <b>NOTE:</b> It supports only durations that fit into long.
     */
    public static String formatWithHighestUnit(Duration duration) {
        long nanos = duration.toNanos();

        List<TimeUnit> orderedUnits =
                Arrays.asList(
                        TimeUnit.NANOSECONDS,
                        TimeUnit.MICROSECONDS,
                        TimeUnit.MILLISECONDS,
                        TimeUnit.SECONDS,
                        TimeUnit.MINUTES,
                        TimeUnit.HOURS,
                        TimeUnit.DAYS);

        TimeUnit highestIntegerUnit =
                IntStream.range(0, orderedUnits.size())
                        .sequential()
                        .filter(
                                idx ->
                                        nanos % orderedUnits.get(idx).unit.getDuration().toNanos()
                                                != 0)
                        .boxed()
                        .findFirst()
                        .map(
                                idx -> {
                                    if (idx == 0) {
                                        return orderedUnits.get(0);
                                    } else {
                                        return orderedUnits.get(idx - 1);
                                    }
                                })
                        .orElse(TimeUnit.MILLISECONDS);

        return String.format(
                "%d %s",
                nanos / highestIntegerUnit.unit.getDuration().toNanos(),
                highestIntegerUnit.getLabels().get(0));
    }

    /**
     * Enum which defines time unit, mostly used to parse value from configuration file.
     */
    private enum TimeUnit {
        DAYS(ChronoUnit.DAYS, singular("d"), plural("day")),
        HOURS(ChronoUnit.HOURS, singular("h"), plural("hour")),
        MINUTES(ChronoUnit.MINUTES, singular("min"), plural("minute")),
        SECONDS(ChronoUnit.SECONDS, singular("s"), plural("sec"), plural("second")),
        MILLISECONDS(ChronoUnit.MILLIS, singular("ms"), plural("milli"), plural("millisecond")),
        MICROSECONDS(ChronoUnit.MICROS, singular("µs"), plural("micro"), plural("microsecond")),
        NANOSECONDS(ChronoUnit.NANOS, singular("ns"), plural("nano"), plural("nanosecond"));

        private static final String PLURAL_SUFFIX = "s";

        private final List<String> labels;

        private final ChronoUnit unit;

        TimeUnit(ChronoUnit unit, String[]... labels) {
            this.unit = unit;
            this.labels =
                    Arrays.stream(labels)
                            .flatMap(ls -> Arrays.stream(ls))
                            .collect(Collectors.toList());
        }

        /**
         * @param label the original label
         * @return the singular format of the original label
         */
        private static String[] singular(String label) {
            return new String[]{label};
        }

        /**
         * @param label the original label
         * @return both the singular format and plural format of the original label
         */
        private static String[] plural(String label) {
            return new String[]{label, label + PLURAL_SUFFIX};
        }

        public List<String> getLabels() {
            return labels;
        }

        public ChronoUnit getUnit() {
            return unit;
        }

        public static String getAllUnits() {
            return Arrays.stream(TimeUnit.values())
                    .map(TimeUnit::createTimeUnitString)
                    .collect(Collectors.joining(", "));
        }

        private static String createTimeUnitString(TimeUnit timeUnit) {
            return timeUnit.name() + ": (" + String.join(" | ", timeUnit.getLabels()) + ")";
        }
    }

    private static ChronoUnit toChronoUnit(java.util.concurrent.TimeUnit timeUnit) {
        switch (timeUnit) {
            case NANOSECONDS:
                return ChronoUnit.NANOS;
            case MICROSECONDS:
                return ChronoUnit.MICROS;
            case MILLISECONDS:
                return ChronoUnit.MILLIS;
            case SECONDS:
                return ChronoUnit.SECONDS;
            case MINUTES:
                return ChronoUnit.MINUTES;
            case HOURS:
                return ChronoUnit.HOURS;
            case DAYS:
                return ChronoUnit.DAYS;
            default:
                throw new IllegalArgumentException(
                        String.format("Unsupported time unit %s.", timeUnit));
        }
    }

    /**
     * 截取时间到日 exp:2022-06-30 11:11:11 => 2022-06-30
     *
     * @param date
     * @return
     */
    public static String getOnlyDate(String date) {
        String str = DateUtil.addTimeSplit(date);
        if (str.length() != 19) {
            return str;
        }
        return str.substring(0, 11);
    }


    public static String formatTimeDiff(Date startTime, Date endTime) {
        LocalDateTime startDateTime = LocalDateTime.ofInstant(startTime.toInstant(), ZoneId.systemDefault());
        LocalDateTime endDateTime = LocalDateTime.ofInstant(endTime.toInstant(), ZoneId.systemDefault());
        Duration diff = Duration.between(startDateTime, endDateTime);
        long minutes = diff.toMinutes();
        if (minutes < 60) {
            return minutes + "分钟";
        }
        if (minutes < 60 * 24) {
            return String.format("%s小时%s分钟", minutes / 60, minutes % 60);
        } else {
            return String.format("%s天%s小时%s分钟", minutes / (60 * 24), (minutes % (60 * 24)) / 60, minutes % 60);
        }
    }

    public static boolean isTime(String text){
        if (text == null || text.length() != 5){
            return false;
        }
        return TIME_PATTERN.matcher(text).find();
    }


    public static List<String> parseTimes(String text,String split){
        if (StringUtils.isEmpty(text)){
            return Lists.newArrayList();
        }
        if (null == split){
            return Lists.newArrayList(text);
        }
        List<String> times = Lists.newArrayList();
        String[] splitTimes = text.split(split);
        for (String time:splitTimes){
             time = StringUtils.trim(time);
            if (TIME_PATTERN.matcher(time).find()){
                times.add(time);
            }
        }
        return times;
    }
}
