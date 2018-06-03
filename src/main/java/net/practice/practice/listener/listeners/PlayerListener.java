package net.practice.practice.listener.listeners;

import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.game.player.Profile;
import net.practice.practice.game.player.data.ProfileState;
import net.practice.practice.game.queue.Queue;
import net.practice.practice.inventory.UnrankedInv;
import net.practice.practice.spawn.SpawnHandler;
import net.practice.practice.util.chat.C;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncLogin(AsyncPlayerPreLoginEvent event) {
        new Profile(event.getUniqueId(), true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {
        Profile profile = Profile.getByPlayer(event.getPlayer());

        SpawnHandler.spawn(event.getPlayer());
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) return;
        Player player = (Player) event.getEntity();
        Profile profile = Profile.getByPlayer(player);

        switch (profile.getProfileState()) {
            case PLAYING:
                break;
            default:
                player.setFoodLevel(20);
                player.setSaturation(0);
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event) {
        Profile profile = Profile.getByPlayer(event.getPlayer());

        switch (profile.getProfileState()) {
            case PLAYING:
                break;
            case BUILDING:
                break;
            default:
                event.setCancelled(true);
                event.getPlayer().updateInventory();
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if (!event.getEntityType().equals(EntityType.PLAYER)) return;
        Player player = (Player) event.getEntity();
        Profile profile = Profile.getByPlayer(player);

        switch (profile.getProfileState()) {
            case PLAYING:
                break;
            default:
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);
        ItemStack item = event.getItem();

        if (item == null || item.getItemMeta() == null
                || !(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))) return;

        if (item.getItemMeta().hasDisplayName()) {
            String display = item.getItemMeta().getDisplayName();

            switch (profile.getProfileState()) {
                case LOBBY: {
                    if (display.contains("Unranked")) {
                        UnrankedInv.openInventory(player);
                    }
                }
                case QUEUING: {
                    if (display.contains("Leave queue")) {
                        profile.leaveQueue(true, false);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getWhoClicked().getType().equals(EntityType.PLAYER)) return;
        Player player = (Player) event.getWhoClicked();
        Profile profile = Profile.getByPlayer(player);

        ItemStack item = event.getCurrentItem();

        if (profile.getProfileState().equals(ProfileState.LOBBY) && event.getClickedInventory().getName() != null) {
            event.setCancelled(true);

            if (item == null || item.getItemMeta() == null) return;

            if (item.getItemMeta().hasDisplayName()) {
                Ladder ladder = Ladder.getLadder(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
                if (ladder != null) {
                    profile.setQueue(new Queue(ladder, profile.getElo(ladder)));
                    player.sendMessage(C.color("&f\u00BB &eJoined the queue for " + (ladder.isRanked() ? "Ranked" : "Unranked") + " " + ladder.getDisplayName() + "."));
                    player.closeInventory();
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        handleLeave(event);
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        handleLeave(event);
    }

    private void handleLeave(PlayerEvent event) {
        Profile profile = Profile.getRemovedProfile(event.getPlayer());

        profile.save();
    }
}
