package land.nub.practice.game.arena.map;

import land.nub.practice.util.LocUtils;
import land.nub.practice.util.file.Configuration;
import lombok.Getter;
import land.nub.practice.game.arena.Arena;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

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
                        Arena.getArena(configuration.getString("maps." + id + ".oldarena")),
                        LocUtils.deserializeLocation(configuration.getString("maps." + id + ".pastePoint")));
            }
        }
    }

    public void saveMap(MapLoc map) throws NullPointerException {
        int id = this.id++;

        config.getMapSection().set(id + ".spawnOne", LocUtils.serializeLocation(map.getSpawnOne()));
        config.getMapSection().set(id + ".spawnTwo", LocUtils.serializeLocation(map.getSpawnTwo()));
        config.getMapSection().set(id + ".oldarena", map.getArena().getName());
        config.getMapSection().set(id + ".pastePoint", LocUtils.serializeLocation(map.getPastePoint()));

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
