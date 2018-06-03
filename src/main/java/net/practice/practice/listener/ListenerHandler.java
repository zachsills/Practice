package net.practice.practice.listener;

import net.practice.practice.Practice;
import net.practice.practice.listener.listeners.PlayerListener;
import net.practice.practice.listener.listeners.WeatherListener;
import org.bukkit.Bukkit;

import java.util.stream.Stream;

public class ListenerHandler {

    public static void registerListeners() {
        Stream.of(
                new PlayerListener(),
                new WeatherListener()
        ).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, Practice.getInstance()));
    }
}
