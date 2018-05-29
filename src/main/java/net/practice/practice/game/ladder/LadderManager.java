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
        for(String id : config.getLadderSection().getKeys(false)) {
            Ladder ladder = new Ladder(configuration.getString("ladders." + id + ".name"));

            ladder.load(config.getLadderSection().getConfigurationSection(id));
        }
    }

    public void saveLadder(Ladder ladder) {
        String id = ladder.getName();

        config.getLadderSection().set(id + ".name", id);
        config.getLadderSection().set(id + ".options.buildable", ladder.isBuildable());
        config.getLadderSection().set(id + ".options.editable", ladder.isEditable());
        config.getLadderSection().set(id + ".options.combo", ladder.isCombo());
        config.getLadderSection().set(id + ".options.ranked", ladder.isRanked());

        config.save();
    }

    public void removeLadder(Ladder ladder) {
        String id = ladder.getName();

        config.getLadderSection().set(id, null);
        config.save();

        Ladder.getLadders().remove(id);
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
