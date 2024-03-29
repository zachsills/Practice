package land.nub.practice.game.player.data;

import land.nub.practice.util.InvUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class PlayerInv {

    @Getter @Setter private ItemStack[] items;
    @Getter @Setter private ItemStack[] armor;

    public PlayerInv(ItemStack[] items, ItemStack[] armor) {
        this.items = items;
        this.armor = armor;
    }

    public PlayerInv() {
        this(null, null);
    }

    public static PlayerInv fromPlayer(PlayerInventory inventory) {
        return new PlayerInv(inventory.getContents(), inventory.getArmorContents());
    }

    public ItemStack getHelmet() {
        return this.armor[0];
    }

    public ItemStack getChestPlate() {
        return this.armor[1];
    }

    public ItemStack getLeggings() {
        return this.armor[2];
    }

    public ItemStack getBoots() {
        return this.armor[3];
    }

    public ItemStack getSword() {
        return this.items[0];
    }

    public void apply(Player player) {
        player.getInventory().setArmorContents(armor);
        player.getInventory().setContents(items);

        player.updateInventory();
    }

    @Override
    public String toString() {
        return InvUtils.invToString(this);
    }
}
