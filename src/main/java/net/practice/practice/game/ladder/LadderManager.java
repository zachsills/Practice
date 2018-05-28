package net.practice.practice.game.ladder;

import lombok.Getter;
import net.practice.practice.util.file.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class LadderManager {

    @Getter private final LadderConfig config;

    @Getter private FileConfiguration configuration;

    public LadderManager() {
        config = new LadderConfig();

        configuration = config.getConfig();
    }

    public void loadLadders() {

    }

    public void saveLadder(Ladder ladder) {

    }

    public void saveLadders() {
        Ladder.getLadders().values().forEach(this::saveLadder);
    }

    private final class LadderConfig extends Configuration {

        LadderConfig() {
            super("ladders");
        }

        public ConfigurationSection getLadderSection() {
            return getConfig().getConfigurationSection("ladders");
        }
    }
}
