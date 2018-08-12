package land.nub.practice.inventory.inventories.cosmetics;

import land.nub.practice.Practice;
import land.nub.practice.util.chat.C;
import land.nub.practice.util.itemstack.I;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class CosmeticInv {

    public static void openInventory(Player player) {
        Inventory inventory = Practice.getInstance().getServer().createInventory(null, 27, C.color("&eCosmetics"));
        int index = 13;
        inventory.setItem(index, new I(Material.NETHER_STAR).name("&cDeath Effects").clearLore()
                .lore(" ")
                .lore("&7Choose a death effect!")
                .lore(" "));

        player.openInventory(inventory);
    }
}
