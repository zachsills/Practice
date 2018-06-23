package net.practice.practice.game.arenatest;

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

    @Getter @Setter private String displayName, builder, schematicName;
    @Getter @Setter private Location relSpawnOne, relSpawnTwo;
    @Getter @Setter private Location pastePoint;

    public Arena(String name) {
        this.name = name;
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

    public boolean setRelSpawnOneRelative(Location location) {
        if (pastePoint == null) return false;

        relSpawnOne = (new Location(location.getWorld(), location.getX() - pastePoint.getX(), location.getY() - pastePoint.getY(), location.getZ() - pastePoint.getZ(),
                location.getYaw(), location.getPitch()));
        return true;
    }
    public boolean setRelSpawnTwoRelative(Location location) {
        if (pastePoint == null) return false;

        relSpawnTwo = (new Location(location.getWorld(), location.getX() - pastePoint.getX(), location.getY() - pastePoint.getY(), location.getZ() - pastePoint.getZ(),
                location.getYaw(), location.getPitch()));
        return true;
    }

    public void load(ConfigurationSection section) {
        setSchematicName(section.getString("schematicName"));
        setRelSpawnOne(LocUtils.deserializeLocation(section.getString("relSpawn.1")));
        setRelSpawnTwo(LocUtils.deserializeLocation(section.getString("relSpawn.2")));
        setPastePoint(LocUtils.deserializeLocation(section.getString("pastePoint")));
    }
}
