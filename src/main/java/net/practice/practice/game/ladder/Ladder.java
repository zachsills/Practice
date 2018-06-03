package net.practice.practice.game.ladder;

import lombok.Getter;
import lombok.Setter;
import net.practice.practice.game.duel.DuelType;
import net.practice.practice.game.player.data.PlayerInv;
import net.practice.practice.util.InvUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Ladder {

    @Getter private final static Map<String, Ladder> ladders = new HashMap<>();

    @Getter private final String name;

    @Getter @Setter private String displayName;
    @Getter @Setter private ItemStack displayIcon;
    @Getter @Setter private boolean buildable, editable, combo, ranked;
    @Getter @Setter private PlayerInv defaultInv;
    @Getter @Setter private DuelType duelType;

    public Ladder(String name) {
        this.name = name;
        this.displayName = ChatColor.GREEN + name;
        this.duelType = DuelType.ONE_VS_ONE;

        ladders.putIfAbsent(name, this);
    }

    public static Ladder getLadder(String name) {
        return ladders.get(name);
    }

    public static Collection<Ladder> getAllLadders() {
        return Collections.unmodifiableCollection(ladders.values());
    }

    public void load(ConfigurationSection section) {
        setDisplayIcon(InvUtils.itemStackFromString(section.getString("options.displayIcon")));

        setBuildable(section.getBoolean("options.buildable"));
        setEditable(section.getBoolean("options.editable"));
        setCombo(section.getBoolean("options.combo"));
        setRanked(section.getBoolean("options.ranked"));

        if(section.contains("defaultInventory"))
            setDefaultInv(InvUtils.invFromString(section.getString("defaultInventory")));
    }

    public boolean isEqual(Ladder other) {
        if (!getName().equals(other.getName()))
            return false;
        if (isBuildable() != other.isBuildable())
            return false;
        if (isEditable() != other.isEditable())
            return false;
        if (isCombo() != other.isCombo())
            return false;
        if (isRanked() != other.isRanked())
            return false;

        return true;
    }
}
