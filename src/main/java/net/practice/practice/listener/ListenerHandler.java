package net.practice.practice.listener;

import net.practice.practice.Practice;
import net.practice.practice.listener.listeners.*;
import org.bukkit.Bukkit;

import java.util.stream.Stream;

public class ListenerHandler {

    public static void registerListeners() {
        Stream.of(
                new BlockListener(),
                new DuelListener(),
                new EditorListener(),
                new PlayerListener(),
                new WeatherListener(),
                new WorldListener()
        ).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, Practice.getInstance()));
    }
}
