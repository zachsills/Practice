package net.practice.practice.cosmetic;

import net.practice.practice.inventory.inventories.cosmetics.DeathEffectInv;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class CosmeticListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null
                || event.getClickedInventory().getTitle() == null
                || event.getClickedInventory().getType() == InventoryType.PLAYER
                || event.getCurrentItem() == null
                || !event.getCurrentItem().hasItemMeta()
                || !event.getCurrentItem().getItemMeta().hasDisplayName()
                || !(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        String title = event.getClickedInventory().getTitle();
        ItemStack item = event.getCurrentItem();
        String display = item.getItemMeta().getDisplayName();

        if (title.contains("Cosmetics")) {
            if (display.contains("Death Effects")) {
                event.setCancelled(true);
                DeathEffectInv.openInventory(player);
            }
        }
    }
}
