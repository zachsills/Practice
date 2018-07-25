package net.practice.practice.listener.listeners;

import net.practice.practice.Practice;
import net.practice.practice.game.duel.Duel;
import net.practice.practice.game.duel.DuelRequest;
import net.practice.practice.game.duel.DuelState;
import net.practice.practice.game.duel.PartyDuelRequest;
import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.game.party.Party;
import net.practice.practice.game.party.PartyState;
import net.practice.practice.game.player.Profile;
import net.practice.practice.game.player.data.ProfileSetting;
import net.practice.practice.game.queue.Queue;
import net.practice.practice.inventory.inventories.*;
import net.practice.practice.spawn.PartyHandler;
import net.practice.practice.spawn.SpawnHandler;
import net.practice.practice.util.RunnableShorthand;
import net.practice.practice.util.chat.C;
import net.practice.practice.util.chat.JsonMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncLogin(AsyncPlayerPreLoginEvent event) {
        new Profile(event.getUniqueId(), true);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onJoin(PlayerJoinEvent event) {
        Profile profile = Profile.getByPlayer(event.getPlayer());
        profile.setName(event.getPlayer().getName());

        RunnableShorthand.runNextTick(() -> {
            SpawnHandler.spawn(event.getPlayer());

            ProfileSetting.toggleFor(event.getPlayer(), ProfileSetting.PLAYER_TIME, profile.getSetting(ProfileSetting.PLAYER_TIME));
        });
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (event.getEntityType() != EntityType.PLAYER) return;
        Player player = (Player) event.getEntity();
        Profile profile = Profile.getByPlayer(player);

        switch(profile.getState()) {
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
        if(event.getPlayer().getGameMode() == GameMode.CREATIVE && event.getPlayer().isOp())
            return;

        switch(profile.getState()) {
            case PLAYING:
                if(event.getItemDrop().getItemStack().getType().name().contains("SWORD") || event.getItemDrop().getItemStack().getType().name().contains("AXE")
                        || event.getItemDrop().getItemStack().getType().name().contains("SHOVEL")) {
                    event.setCancelled(true);
                } else if(event.getItemDrop().getItemStack().getType() == Material.GLASS_BOTTLE || event.getItemDrop().getItemStack().getType() == Material.BOWL) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            event.getItemDrop().remove();
                        }
                    }.runTaskLater(Practice.getInstance(), 3L);
                } else {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if(event.getItemDrop().isValid() && !event.getItemDrop().isDead())
                                event.getItemDrop().remove();
                        }
                    }.runTaskLater(Practice.getInstance(), 20L * 5);
                }
                break;
            case EDITING: {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        event.getItemDrop().remove();
                    }
                }.runTaskLater(Practice.getInstance(), 2L);
                break;
            }
            default:
                event.setCancelled(true);
                event.getPlayer().updateInventory();
        }
    }

    @EventHandler
    public void onPlayerPickup(PlayerPickupItemEvent event) {
        Profile profile = Profile.getByPlayer(event.getPlayer());
        if(event.getPlayer().getGameMode() == GameMode.CREATIVE && event.getPlayer().isOp())
            return;

        switch(profile.getState()) {
            case PLAYING: {
                event.setCancelled(false);
                break;
            }
            default:
                event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        if(event.getEntityType() != EntityType.PLAYER)
            return;

        Player player = (Player) event.getEntity();
        Profile profile = Profile.getByPlayer(player);

        switch(profile.getState()) {
            case PLAYING: {
                if(event instanceof EntityDamageByEntityEvent) {
                    EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
                    if(e.getDamager().getType() != EntityType.PLAYER)
                        return;

                    Duel duel = profile.getCurrentDuel();
                    if(duel.getState() != DuelState.PLAYING) {
                        event.setCancelled(true);
                        return;
                    }

                    Player other = (Player) e.getDamager();
                    if(!duel.hasPlayer(other))
                        event.setCancelled(true);
                    else {
                        if(!duel.canHit(player, other)) {
                            event.setCancelled(true);
                            return;
                        }

                        Profile otherProfile = Profile.getByPlayer(other);
                        otherProfile.setLongestCombo(otherProfile.getLongestCombo() + 1);

                        if(profile.getLongestCombo() != 0) {
                            if(!duel.getComboes().containsKey(player))
                                duel.getComboes().put(player, new ArrayList<>());

                            List<Integer> comboes = duel.getComboes().get(player);
                            comboes.add(profile.getLongestCombo());
                            profile.setLongestCombo(0);
                            duel.getComboes().put(player, comboes);
                        }

                        event.setCancelled(false);
                    }
                }
                break;
            }
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
                || !event.getAction().name().contains("RIGHT")
                || player.getGameMode().equals(GameMode.CREATIVE)) return;

        if(item.getItemMeta().hasDisplayName()) {
            String display = item.getItemMeta().getDisplayName();

            switch(profile.getState()) {
                case LOBBY: {
                    if(!profile.isInParty()) {
                        if(display.contains("Unranked"))
                            UnrankedInv.openInventory(player);
                        else if(display.contains("Ranked"))
                            RankedInv.openInventory(player);
                        else if(display.contains("Edit"))
                            EditorInv.openInventory(player);
                        else if(display.contains("Stats"))
                            StatsInv.openInventory(player);
                        else if(display.contains("Rematch"))
                            profile.sendRematch();
                        else if(display.contains("Settings"))
                            profile.openSettings();
                        else if(display.contains("Party"))
                            profile.createParty();
                    } else {
                        if(display.contains("Unranked"))
                            UnrankedInv.openInventory(player);
                        else if(display.contains("Ranked"))
                            RankedInv.openInventory(player);
                        else if(display.contains("Info"))
                            profile.sendPartyInfo();
                        else if(display.contains("Event"))
                            player.sendMessage(C.color("&cParty events are temporarily disabled."));
                        else if(display.contains("Other Parties"))
                            PartiesInventory.openInventory(player);
                        else if(display.contains("Leave Queue"))
                            profile.getParty().leaveQueue();
                        else if(display.contains("Leave"))
                            Bukkit.dispatchCommand(player, "party leave");
                    }
                    event.setCancelled(true);
                    break;
                }
                case QUEUING: {
                    if(display.contains("Leave Queue"))
                        profile.leaveQueue(true, false);
                    event.setCancelled(true);
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.getCurrentItem() == null || event.getClickedInventory() == null)
            return;
        if(event.getWhoClicked().getType() != EntityType.PLAYER)
            return;

        Player player = (Player) event.getWhoClicked();
        Profile profile = Profile.getByPlayer(player);

        ItemStack item = event.getCurrentItem();

        if(event.getClickedInventory().getTitle() != null && event.getClickedInventory().getTitle().contains("'s Inventory")) {
            event.setCancelled(true);
            return;
        }

        if(event.getClickedInventory().getTitle() != null && event.getClickedInventory().getTitle().contains("Settings")) {
            event.setCancelled(true);
            ProfileSetting setting = ProfileSetting.getByMaterial(item.getType());
            profile.toggleSetting(setting);

            event.getClickedInventory().setItem(event.getSlot(), ProfileSetting.getSettingItem(setting, profile.getSetting(setting)));
            return;
        }

        if(event.getClickedInventory().getTitle() != null && event.getClickedInventory().getTitle().contains("Editing")) {
            event.setCancelled(false);
            return;
        }

        if(!profile.isInGame() && event.getClickedInventory().getName() != null && player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);

            if(item == null || item.getItemMeta() == null || !item.getItemMeta().hasDisplayName())
                return;

            if(event.getClickedInventory().getTitle().contains("Unranked")) {
                Ladder ladder = Ladder.getLadder(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
                if(ladder != null) {
                    if(!profile.isInParty()) {
                        profile.addToQueue(ladder.getUnrankedQueue());
                    } else {
                        Party party = profile.getParty();
                        if(party.getSize() != 2) {
                            player.sendMessage(C.color("&cYou must have 2 players to perform this action."));
                            return;
                        }

                        Queue queue = ladder.getUnrankedPartyQueue();
                        queue.add(party.getId());

                        party.setCurrentQueue(queue);
                        PartyHandler.spawn(player, true, true);
                    }
                    player.sendMessage(C.color("&f\u00BB &eJoined the queue for Unranked " + ladder.getDisplayName() + "."));
                    player.closeInventory();
                }
            } else if(event.getClickedInventory().getTitle().contains("Ranked")) {
                Ladder ladder = Ladder.getLadder(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
                if(ladder != null) {
                    if(!profile.isInParty()) {
                        profile.addToQueue(ladder.getRankedQueue());
                    } else {
                        Party party = profile.getParty();
                        if(party.getSize() != 2) {
                            player.sendMessage(C.color("&cYou must have 2 players to perform this action."));
                            return;
                        }

                    }
                    player.sendMessage(C.color("&f\u00BB &eJoined the queue for Ranked " + ladder.getDisplayName() + "."));
                    player.closeInventory();
                }
            } else if(event.getClickedInventory().getTitle().contains("Requesting")) {
                Player requested = Bukkit.getPlayer(event.getClickedInventory().getTitle().split(" ")[1]);
                if(requested == null || !requested.isOnline()) {
                    player.sendMessage(ChatColor.RED + "The player '" + event.getClickedInventory().getTitle().split(" ")[1] + "' is no longer online.");
                    return;
                }

                Profile target = Profile.getByPlayer(requested);
                Ladder ladder = Ladder.getLadder(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
                if(ladder != null) {
                    player.closeInventory();

                    if(!target.isInParty()) {
                        if(profile.getDuelRequests().containsKey(requested.getName())) {
                            player.sendMessage(C.color("&cYou have already sent this player a request."));
                            return;
                        }

                        if(target.isInGame()) {
                            player.sendMessage(C.color("&cThat player is currently occupied."));
                            return;
                        }

                        DuelRequest request = new DuelRequest(player, requested, ladder);
                        request.sendToRequested();

                        player.sendMessage(C.color("&aYou have sent a duel request to " + requested.getName() + "."));
                    } else {
                        if(profile.getParty().getRequests().containsKey(profile.getParty())) {
                            player.sendMessage(C.color("&cYou have already sent this player a request."));
                            return;
                        }

                        if(target.getParty().isInGame()) {
                            player.sendMessage(C.color("&cThat party is currently occupied."));
                            return;
                        }

                        new PartyDuelRequest(profile.getParty(), target.getParty(), ladder).sendToRequested();

                        player.sendMessage(C.color("&aYou have sent a duel request to " + requested.getName() + "'s party."));
                    }
                }
            } else if(event.getClickedInventory().getTitle().contains("Select a Ladder...")) {
                Ladder ladder = Ladder.getLadder(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
                if(ladder == null)
                    return;

                profile.beginEditing(ladder);
            } else if(event.getClickedInventory().getTitle().contains("Other Parties")) {
                Player owner = Bukkit.getPlayer(C.strip(item.getItemMeta().getDisplayName().split("'")[0]));
                Profile ownerProfile = Profile.getByPlayer(owner);
                if(!ownerProfile.isInParty()) {
                    PartiesInventory.updateInventory();
                    return;
                }

                Party party = ownerProfile.getParty();
                if(party.isInGame()) {
                    profile.sendMessage("&cThis party is currently occupied.");
                    return;
                }

                if(party == profile.getParty()) {
                    profile.sendMessage("&cYou cannot duel your own Party.");
                    return;
                }

                Bukkit.dispatchCommand(player, "duel " + owner.getName());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onQuit(PlayerQuitEvent event) {
        handleLeave(event);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onKick(PlayerKickEvent event) {
        handleLeave(event);
    }

    private void handleLeave(PlayerEvent event) {
        Profile profile = Profile.getRemovedProfile(event.getPlayer());

        if(profile.isQueueing())
            profile.removeFromQueue();

        if(profile.isInParty())
            Bukkit.dispatchCommand(profile.getPlayer(), "party leave");

        if(profile.isSpectating())
            profile.getSpectating().getSpectators().remove(profile);

        profile.save();
    }
}
