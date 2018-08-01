package net.practice.practice.listener;

import net.practice.practice.Practice;
import net.practice.practice.cosmetic.CosmeticListener;
import net.practice.practice.cosmetic.deatheffect.DeathEffectListener;
import net.practice.practice.listener.listeners.*;
import org.bukkit.Bukkit;

import java.util.stream.Stream;

public class ListenerHandler {

    public static void registerListeners() {
        Stream.of(
                new MapCleaningListener(),
                new BlockListener(),
                new DuelListener(),
                new EditorListener(),
                new PlayerListener(),
                new SpectatorListener(),
                new WeatherListener(),
                new WorldListener(),
                new CosmeticListener(),
                new DeathEffectListener()
        ).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, Practice.getInstance()));

        new ServerListener();
    }
}
