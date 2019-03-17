package land.nub.practice.util;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Objects;

public class PlayerUtils {

    public static int getPing(Player player) {
        return ((CraftPlayer) player).getHandle().ping;
    }

    public static ChatColor getHealthColor(double health) {
        if(health >= 7.0)
            return ChatColor.GREEN;

        if(health >= 4.0)
            return ChatColor.YELLOW;

        return ChatColor.RED;
    }

    public static double getHealth(Player player) {
        return roundHealth(player.getHealth());
    }

    public static double roundHealth(double health) {
        return Math.round(health * 2) / 2.0;
    }

    public static int getRemainingPots(Player player) {
        return (int) Arrays.stream(player.getInventory().getContents())
                .filter(Objects::nonNull)
                .filter(itemStack -> itemStack.getType() == Material.POTION && itemStack.getDurability() == 16421)
                .count();
    }
}
