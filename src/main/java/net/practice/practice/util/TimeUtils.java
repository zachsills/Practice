package net.practice.practice.util;

import java.util.concurrent.TimeUnit;

public class TimeUtils {

    public static String msToMMSS(long ms) {
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(ms),
                TimeUnit.MILLISECONDS.toSeconds(ms) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(ms)));
    }
}
