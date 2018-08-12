package net.practice.practice.listener.listeners;

import lombok.Getter;
import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.game.player.Profile;
import net.practice.practice.game.player.data.PlayerKit;
import net.practice.practice.game.player.data.ProfileState;
import net.practice.practice.inventory.inventories.EditorInv;
import net.practice.practice.util.RunnableShorthand;
import net.practice.practice.util.chat.C;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.painting.PaintingBreakByEntityEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EditorListener implements Listener {

    @Getter private Map<UUID, PlayerKit> renaming;

    public EditorListener() {
        renaming = new HashMap<>();
    }

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
            case WOOD_DOOR:
            case SPRUCE_DOOR:
            case WOODEN_DOOR:
            case WALL_SIGN:
            case SIGN_POST:
            case SIGN: {
                RunnableShorthand.runNextTick(profile::stopEditing);
                return;
            }
            case ANVIL: {
                EditorInv.openSavingInventory(player);
                return;
            }
            case CHEST: {
                EditorInv.openLadderItems(player, profile.getEditing());
            }
            default:
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPaintingBreak(HangingBreakByEntityEvent event) {
        if(!(event.getRemover() instanceof Player)) {
            event.setCancelled(true);
            return;
        }

        Player player = (Player) event.getRemover();
        if(!player.isOp()) {
            event.setCancelled(true);
            return;
        }

        if(player.getGameMode() != GameMode.CREATIVE)
            event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Profile profile = Profile.getByPlayer(player);
        if(profile.getState() != ProfileState.EDITING)
            return;

        if(event.getClickedInventory() == null || event.getClickedInventory().getTitle() == null)
            return;

        if(event.getClickedInventory().getType() == InventoryType.PLAYER) {
            event.setCancelled(false);
            return;
        }

        ItemStack item = event.getCurrentItem();
        if(item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName())
            return;

        String title = event.getClickedInventory().getTitle();

        if(title.contains("Editing")) {
            event.setCancelled(false);
        } else if(title.contains("Save Kits")) {
            event.setCancelled(true);

            Ladder editing = profile.getEditing();
            String display = item.getItemMeta().getDisplayName();
            if(item.getType() == Material.BOOK) {
                int kitIndex = Integer.valueOf(C.strip(display.split(" ")[1]).replace("#", ""));
                if(profile.getCustomKits().get(editing).get(kitIndex - 1).getPlayerInv() == null) {
                    profile.getCustomKits().get(editing).get(kitIndex - 1).save(player);
                    EditorInv.openSavingInventory(player);
                }
                player.sendMessage(C.color("&aSaved kit #" + kitIndex + "."));
            } else if(item.getType() == Material.NAME_TAG) {
                int kitIndex = Integer.valueOf(C.strip(display.split(" ")[2]).replace("#", ""));
                renaming.put(player.getUniqueId(), profile.getCustomKits().get(editing).get(kitIndex - 1));

                player.sendMessage(ChatColor.YELLOW + "Please type a new name for your kit in chat.");
                player.sendMessage(C.color("&eTip: You're able to use color codes (") + "&6&lTest" + C.color(") which could look like this: &b&lI love Huli"));

                player.closeInventory();
            } else if(item.getType() == Material.INK_SACK) {
                int kitIndex = Integer.valueOf(C.strip(display.split(" ")[2]).replace("#", ""));
                if(item.getDurability() == 10) {
                    profile.getCustomKits().get(editing).get(kitIndex - 1).getPlayerInv().apply(player);
                    player.closeInventory();
                    player.sendMessage(C.color("&aLoaded kit #" + kitIndex + "."));
                } else {
                    profile.getCustomKits().get(editing).get(kitIndex - 1).setPlayerInv(null);
                    EditorInv.openSavingInventory(player);
                    player.sendMessage(C.color("&aRemoved kit #" + kitIndex + "."));
                }
            }
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);
        if(profile.getState() != ProfileState.EDITING) {
            renaming.remove(player.getUniqueId());
            return;
        }

        if(!renaming.containsKey(player.getUniqueId()))
            return;

        event.setCancelled(true);

        PlayerKit kit = renaming.remove(player.getUniqueId());
        kit.setName(event.getMessage());

        player.sendMessage(C.color("&eKit name set to '" + kit.getName() + "&e'."));
    }
}
