package land.nub.practice.listener;

import land.nub.practice.cosmetic.CosmeticListener;
import land.nub.practice.cosmetic.deatheffect.DeathEffectListener;
import land.nub.practice.listener.listeners.*;
import land.nub.practice.Practice;
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
                new WorldListener(),
                new CosmeticListener(),
                new DeathEffectListener()
        ).forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, Practice.getInstance()));

        new ServerListener();
    }
}
