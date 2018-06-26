package net.practice.practice.game.ladder;

import lombok.Getter;
import lombok.Setter;
import net.practice.practice.game.player.data.PlayerInv;
import net.practice.practice.game.queue.Queue;
import net.practice.practice.game.queue.QueueType;
import net.practice.practice.game.queue.type.RankedSoloQueue;
import net.practice.practice.game.queue.type.UnkrankedSoloQueue;
import net.practice.practice.util.InvUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Ladder {

    @Getter private final static Map<String, Ladder> ladders = new HashMap<>();

    @Getter private final String name;

    @Getter private Queue[] queues;

    @Getter @Setter private String displayName;
    @Getter @Setter private ItemStack displayIcon;
    @Getter @Setter private boolean buildable, editable, combo, ranked;
    @Getter @Setter private PlayerInv defaultInv;
    @Getter @Setter private Inventory editor;

    public Ladder(String name) {
        this.name = name;
        this.displayName = ChatColor.GREEN + name;

        this.queues = new Queue[3];
        queues[0] = new UnkrankedSoloQueue(this);

        this.editor = Bukkit.createInventory(null, 54, "Editing: " + name);

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

        if(section.contains("editorInventory")) {
            try {
                Inventory inventory = InvUtils.fromBase64(section.getString("editorInventory"));

                editor.setContents(inventory.getContents());
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public Queue getUnrankedQueue() {
        return queues[0];
    }

    public Queue getRankedQueue() {
        return queues[1];
    }

    public int getTotalQueuing(QueueType type) {
        switch(type) {
            case UNRANKED:
                return getUnrankedQueue().getSize();
            case RANKED:
                return getRankedQueue().getSize();
        }

        return 0;
    }

    public void setRanked(boolean value) {
        ranked = value;

        if(ranked)
            queues[1] = new RankedSoloQueue(this);
        else
            queues[1] = null;
    }

    public boolean isSpleef() {
        return name.contains("Spleef");
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
