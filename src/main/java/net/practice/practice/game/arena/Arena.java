package net.practice.practice.game.arena;

import lombok.Getter;
import lombok.Setter;
import net.practice.practice.util.LocUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

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

    public void load(ConfigurationSection section) {
        setBuilder(section.contains("builder") ? section.getString("builder") : "");

        setPosOne(LocUtils.deserializeLocation(section.getString("pos.1")));
        setPosTwo(LocUtils.deserializeLocation(section.getString("pos.2")));

        setSpawnOne(LocUtils.deserializeLocation(section.getString("spawn.1")));
        setSpawnTwo(LocUtils.deserializeLocation(section.getString("spawn.2")));
    }
}
