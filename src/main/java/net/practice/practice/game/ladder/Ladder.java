package net.practice.practice.game.ladder;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Ladder {

    @Getter private final static Map<String, Ladder> ladders = new HashMap<>();

    @Getter private final String name;

    @Getter @Setter private String displayName;
    @Getter @Setter private Material displayIcon;
    @Getter @Setter private boolean buildable, editable, combo, ranked;

    public Ladder(String name) {
        this.name = name;
        this.displayName = ChatColor.GREEN + name;

        ladders.putIfAbsent(name, this);
    }

    public static Ladder getLadder(String name) {
        return ladders.get(name);
    }

    public static Collection<Ladder> getAllLadders() {
        return Collections.unmodifiableCollection(ladders.values());
    }

    public void load(ConfigurationSection section) {
        setDisplayIcon(Material.valueOf(section.getString("displayIcon")));

        setBuildable(section.getBoolean("options.buildable"));
        setEditable(section.getBoolean("options.editable"));
        setCombo(section.getBoolean("options.combo"));
        setRanked(section.getBoolean("options.ranked"));
    }
}
