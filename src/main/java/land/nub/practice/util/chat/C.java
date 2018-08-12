package land.nub.practice.util.chat;

import org.bukkit.ChatColor;

public class C {

    public static String strip(final String string) {
        return ChatColor.stripColor(string);
    }

    public static String color(final String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
}
