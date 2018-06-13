package net.practice.practice.listener.listeners;

import net.practice.practice.game.player.Profile;
import net.practice.practice.game.player.data.ProfileState;
import net.practice.practice.inventory.inventories.EditorInv;
import net.practice.practice.util.RunnableShorthand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;

public class EditorListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);
        if(profile.getState() != ProfileState.EDITING)
            return;

        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);

        switch(event.getClickedBlock().getType()) {
            case WALL_SIGN:
            case SIGN_POST:
            case SIGN: {
                RunnableShorthand.runNextTick(profile::stopEditing);
                return;
            }
            case ANVIL: {

                return;
            }
            case CHEST: {
                EditorInv.openLadderItems(player, profile.getEditing());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent event) {
        if(event.getClickedInventory() == null || event.getClickedInventory().getTitle() == null)
            return;

        Player player = (Player) event.getWhoClicked();
        Profile profile = Profile.getByPlayer(player);
        if(profile.getState() != ProfileState.EDITING)
            return;
        if(event.getClickedInventory().getType() == InventoryType.PLAYER) {
            event.setCancelled(false);
            return;
        }

        String title = event.getClickedInventory().getTitle();

        if(title.contains("Editing")) {
            event.setCancelled(false);
            return;
        }
    }
}
