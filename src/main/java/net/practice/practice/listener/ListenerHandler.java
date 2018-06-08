package net.practice.practice.listener;

import net.practice.practice.Practice;
import net.practice.practice.listener.listeners.DuelListener;
import net.practice.practice.listener.listeners.PlayerListener;
import net.practice.practice.listener.listeners.WeatherListener;
import net.practice.practice.listener.listeners.WorldListener;
import org.bukkit.Bukkit;

import java.util.stream.Stream;

public class ListenerHandler {

    public static void registerListeners() {
        Stream.of(
                new DuelListener(),
                new PlayerListener(),
                new WeatherListener(),
                new WorldListener()
        ).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, Practice.getInstance()));
    }
}
