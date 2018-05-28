package net.practice.practice.util;

import net.practice.practice.Practice;
import org.bukkit.Bukkit;

public class RunnableShorthand {

    public static void runAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(Practice.getInstance(), runnable);
    }

    public static void runNextTick(Runnable runnable) {
        Bukkit.getScheduler().runTaskLater(Practice.getInstance(), runnable, 1L);
    }
}
