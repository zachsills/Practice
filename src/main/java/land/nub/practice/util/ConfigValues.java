package land.nub.practice.util;

import land.nub.practice.Practice;

public class ConfigValues {

    private static Practice plugin = Practice.getInstance();

    /* Scoreboard Values */
    public static final String SCOREBOARD_TITLE = plugin.getConfig().getString("scoreboard.title");
}
