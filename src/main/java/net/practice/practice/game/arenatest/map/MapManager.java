package net.practice.practice.game.arenatest.map;

import lombok.Getter;
import net.practice.practice.game.arenatest.Arena;
import net.practice.practice.util.LocUtils;
import net.practice.practice.util.file.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;

public class MapManager {

    @Getter private final MapConfig config;

    @Getter private FileConfiguration configuration;

    private int id = 0;

    public MapManager() {
        config = new MapConfig();
        configuration = config.getConfig();
        loadMaps();
    }

    public void loadMaps() {
        if (config.getMapSection() != null) {
            for (String id : config.getMapSection().getKeys(false)) {
                new MapLoc(LocUtils.deserializeLocation(configuration.getString("maps." + id + ".spawnOne")),
                        LocUtils.deserializeLocation(configuration.getString("maps." + id + ".spawnTwo")),
                        Arena.getArena(configuration.getString("maps." + id + ".arena")));
            }
        }
    }

    public void saveMap(MapLoc map) throws NullPointerException {
        int id = this.id++;

        config.getMapSection().set(id + ".spawnOne", LocUtils.serializeLocation(map.getSpawnOne()));
        config.getMapSection().set(id + ".spawnTwo", LocUtils.serializeLocation(map.getSpawnTwo()));
        config.getMapSection().set(id + ".arena", map.getArena().getName());

        config.save();
    }

    public void saveMaps() {
        try {
            for (MapLoc map : MapLoc.getMaps()) {
                saveMap(map);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private final class MapConfig extends Configuration {

        MapConfig() {
            super("maps");
        }

        public ConfigurationSection getMapSection() {
            return getConfig().getConfigurationSection("maps") != null ? getConfig().getConfigurationSection("maps") : getConfig().createSection("maps");
        }
    }
}
