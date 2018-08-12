package net.practice.practice.listener.listeners;

import net.practice.practice.game.duel.DuelType;
import net.practice.practice.game.duel.type.SoloDuel;
import net.practice.practice.game.player.Profile;
import net.practice.practice.util.RunnableShorthand;
import net.practice.practice.util.chat.C;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SpectatorListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);
        if(!profile.isSpectating())
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(!event.getAction().name().contains("RIGHT"))
            return;

        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);
        if(!profile.isSpectating())
            return;

        ItemStack item = event.getItem();
        if(item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName())
            return;

        event.setCancelled(true);

        switch(item.getType()) {
            case PAPER: {
                boolean ranked = profile.getSpectating().getType() == DuelType.ONE_VS_ONE && ((SoloDuel) profile.getSpectating()).isRanked();
                player.sendMessage(C.color("&f&m---------------------------------"));
                player.sendMessage(C.color("&6Match Type: &e" + profile.getSpectating().getType().getFriendlyName() + (ranked ? " (R)" : "")));
                player.sendMessage(C.color("&6Ladder: &e" + profile.getSpectating().getLadder().getName()));
                player.sendMessage(C.color("&f&m---------------------------------"));
                return;
            }
            case INK_SACK: {
                RunnableShorthand.runNextTick(() -> {
                    profile.setSpectating(null);
                });
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Profile profile = Profile.getByPlayer(player);
        if(!profile.isSpectating())
            return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent event) {
        if(event.getRightClicked().getType() != EntityType.PLAYER)
            return;

        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);
        if(!profile.isSpectating())
            return;

        if(player.getItemInHand() == null || player.getItemInHand().getType() != Material.BOOK)
            return;

        Player other = (Player) event.getRightClicked();
        player.openInventory(other.getInventory());
    }
}
