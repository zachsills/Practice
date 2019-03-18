package land.nub.practice.game.ladder;

import land.nub.practice.game.player.data.PlayerInv;
import land.nub.practice.game.queue.Queue;
import land.nub.practice.game.queue.QueueType;
import land.nub.practice.game.queue.type.RankedSoloQueue;
import land.nub.practice.game.queue.type.UnkrankedSoloQueue;
import land.nub.practice.game.queue.type.UnrankedPartyQueue;
import land.nub.practice.util.InvUtils;
import lombok.Getter;
import lombok.Setter;
import land.nub.practice.game.queue.type.RankedPartyQueue;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.*;

public class Ladder {

    @Getter private final static Map<String, Ladder> ladders = new LinkedHashMap<>();

    @Getter private final String name;

    @Getter private land.nub.practice.game.queue.Queue[] queues;

    @Getter @Setter private String displayName;
    @Getter @Setter private ItemStack displayIcon;
    @Getter @Setter private boolean buildable, editable, combo, ranked;
    @Getter @Setter private PlayerInv defaultInv;
    @Getter @Setter private Inventory editor;

    public Ladder(String name) {
        this.name = name;
        this.displayName = ChatColor.GREEN + name;

        this.queues = new land.nub.practice.game.queue.Queue[4]; // Change to 4
        queues[0] = new UnkrankedSoloQueue(this);
        queues[2] = new UnrankedPartyQueue(this);

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

    public land.nub.practice.game.queue.Queue getUnrankedQueue() {
        return queues[0];
    }

    public land.nub.practice.game.queue.Queue getRankedQueue() {
        return queues[1];
    }

    public land.nub.practice.game.queue.Queue getUnrankedPartyQueue() {
        return queues[2];
    }

    public Queue getRankedPartyQueue() {
        return queues[3];
    }

    public int getTotalQueuing(QueueType type) {
        switch(type) {
            case UNRANKED:
                return getUnrankedQueue().getSize();
            case RANKED:
                return getRankedQueue().getSize();
            case UNRANKED_TEAM:
                return getUnrankedPartyQueue().getSize();
            case RANKED_TEAM:
                return getRankedPartyQueue().getSize();
        }

        return 0;
    }

    public void setRanked(boolean value) {
        ranked = value;

        if(ranked) {
            queues[1] = new RankedSoloQueue(this);
            queues[3] = new RankedPartyQueue(this);
        } else {
            queues[1] = null;
            queues[3] = null;
        }
    }

    public boolean isSpleef() {
        return name.contains("Spleef");
    }

    public boolean isSumo() {
        return name.contains("Sumo");
    }
}
