package net.practice.practice.game.arena;

import lombok.Getter;
import lombok.Setter;
import net.practice.practice.util.LocUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Arena {

    @Getter private static final Map<String, Arena> arenas = new HashMap<>();

    @Getter private final String name;

    @Getter @Setter private String displayName, builder;
    @Getter @Setter private Location posOne, posTwo;
    @Getter @Setter private Location spawnOne, spawnTwo;

    public Arena(String name) {
        this.name = name;
        this.displayName = ChatColor.YELLOW + this.getName();

        getArenas().putIfAbsent(name, this);
    }

    public static Arena getArena(String name) {
        return getArenas().get(name);
    }

    public static Arena getRandomArena() {
        if (getArenas().values().isEmpty()) {
            return null;
        }
        int random = ThreadLocalRandom.current().nextInt(0, getArenas().values().size());
        return (Arena) getArenas().values().toArray()[random];
    }

    public void load(ConfigurationSection section) {
        setBuilder(section.contains("builder") ? section.getString("builder") : "");

        setPosOne(LocUtils.deserializeLocation(section.getString("pos.1")));
        setPosTwo(LocUtils.deserializeLocation(section.getString("pos.2")));

        setSpawnOne(LocUtils.deserializeLocation(section.getString("spawn.1")));
        setSpawnTwo(LocUtils.deserializeLocation(section.getString("spawn.2")));
    }
}
