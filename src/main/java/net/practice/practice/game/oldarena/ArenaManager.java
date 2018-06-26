package net.practice.practice.game.oldarena;

import lombok.Getter;
import net.practice.practice.util.LocUtils;
import net.practice.practice.util.file.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class ArenaManager {

    @Getter private final ArenaConfig config;

    @Getter private FileConfiguration configuration;

    public ArenaManager() {
        config = new ArenaConfig();
        configuration = config.getConfig();
        loadArenas();
    }

    public void loadArenas() {
        if (config.getArenaSection() != null) {
            for (String id : config.getArenaSection().getKeys(false)) {
                Arena arena = new Arena(configuration.getString("arenas." + id + ".name"));

                arena.load(config.getArenaSection().getConfigurationSection(id));
            }
        }
    }

    public void saveArena(Arena arena) throws NullPointerException {
        String id = arena.getName();

        config.getArenaSection().set(id + ".name", id);
        config.getArenaSection().set(id + ".spawn.1", LocUtils.serializeLocation(arena.getSpawnOne()));
        config.getArenaSection().set(id + ".spawn.2", LocUtils.serializeLocation(arena.getSpawnTwo()));

        config.save();
    }

    public void removeArena(Arena arena) {
        String id = arena.getName();

        config.getArenaSection().set(id, null);
        config.save();

        Arena.getArenas().remove(id);
    }

    public void saveArenas() {
        try {
            for (Arena arena : Arena.getArenas().values()) {
                saveArena(arena);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private final class ArenaConfig extends Configuration {

        ArenaConfig() {
            super("arenas");
        }

        public ConfigurationSection getArenaSection() {
            return getConfig().getConfigurationSection("arenas") != null ? getConfig().getConfigurationSection("arenas") : getConfig().createSection("arenas");
        }
    }
}
