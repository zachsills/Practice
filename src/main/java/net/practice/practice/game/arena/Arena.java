package net.practice.practice.game.arena;

import lombok.Getter;
import lombok.Setter;
import net.practice.practice.game.ladder.Ladder;
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

    @Getter @Setter private ArenaType type;
    @Getter @Setter private String displayName, builder;
    @Getter @Setter private Location spawnOne, spawnTwo;

    public Arena(String name) {
        this.name = name;
        this.type = ArenaType.MULTI;
        this.displayName = ChatColor.YELLOW + this.getName();

        getArenas().putIfAbsent(name, this);
    }

    public static Arena getArena(String name) {
        return getArenas().get(name);
    }

    public static Arena getRandomArena() {
        if(getArenas().values().isEmpty())
            return null;

        int random = ThreadLocalRandom.current().nextInt(0, getArenas().values().size());
        return (Arena) getArenas().values().toArray()[random];
    }

    public static Arena getRandomArena(Ladder ladder) {
        if(ladder.isBuildable()) {
            return getArenas().values().stream()
                    .filter(Arena::isSingle)
                    .findFirst()
                    .orElse(null);
        }

        return getRandomArena();
    }

    public boolean isMulti() {
        return type == ArenaType.MULTI;
    }

    public boolean isSingle() {
        return type == ArenaType.SINGLE;
    }

    public void load(ConfigurationSection section) {
        setBuilder(section.contains("builder") ? section.getString("builder") : "");

        setSpawnOne(LocUtils.deserializeLocation(section.getString("spawn.1")));
        setSpawnTwo(LocUtils.deserializeLocation(section.getString("spawn.2")));
    }
}
