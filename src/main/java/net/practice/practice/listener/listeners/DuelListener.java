package net.practice.practice.listener.listeners;

import net.practice.practice.Practice;
import net.practice.practice.game.duel.Duel;
import net.practice.practice.game.duel.DuelState;
import net.practice.practice.game.player.Profile;
import net.practice.practice.game.player.data.PlayerKit;
import net.practice.practice.task.EnderPearlTask;
import net.practice.practice.util.chat.C;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DuelListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Profile profile = Profile.getByPlayer(event.getEntity());
        if(profile.getCurrentDuel() == null)
            return;

        Duel duel = profile.getCurrentDuel();
        if(duel.getState() != DuelState.PLAYING)
            return;

        //Location deathLoc = event.getEntity().getLocation();
        event.getDrops().clear();

        duel.saveInventory(profile.getUuid());
        duel.kill(event.getEntity());

        profile.handleDeath();

        /*new BukkitRunnable() {
            @Override
            public void run() {
                if(event.getEntity().isDead())
                    ((CraftPlayer) event.getEntity()).getHandle().playerConnection.a(new PacketPlayInClientCommand(PacketPlayInClientCommand.EnumClientCommand.PERFORM_RESPAWN));

                event.getEntity().teleport(deathLoc);
            }
        }.runTaskLater(Practice.getInstance(), 5L);*/
    }

    @EventHandler
    public void onPotionThrow(PotionSplashEvent event) {
        if(!(event.getEntity().getShooter() instanceof Player))
            return;

        Player player = (Player) event.getEntity().getShooter();
        Profile profile = Profile.getByPlayer(player);
        if(!profile.isInGame())
            return;

        boolean missed = true;
        if(event.getIntensity(player) >= .5)
            missed = false;

        if(missed) {
            int missedPots = profile.getCurrentDuel().getMissedPots().containsKey(player) ? profile.getCurrentDuel().getMissedPots().get(player) : 0;
            profile.getCurrentDuel().getMissedPots().put(player, missedPots + 1);
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if(!event.getAction().name().contains("RIGHT"))
            return;

        ItemStack item = event.getItem();
        if(item == null || item.getType() != Material.ENDER_PEARL)
            return;

        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);
        if(!profile.isInGame()) {
            event.setCancelled(true);
            return;
        }

        if(profile.getCurrentDuel().getState() != DuelState.PLAYING) {
            player.sendMessage(C.color("&cYou can not throw pearls unless the game is started."));
            event.setCancelled(true);
            return;
        }

        if(player.getExp() < 0.01F)
            return;

        event.setCancelled(true);

        player.sendMessage(C.color("&cYou are still on ender pearl cooldown."));
    }

    @EventHandler
    public void onKitSelect(PlayerInteractEvent event) {
        if(!event.getAction().name().contains("RIGHT"))
            return;

        ItemStack item = event.getItem();
        if(item == null || item.getType() != Material.ENCHANTED_BOOK || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName())
            return;

        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);
        if(!profile.isInGame()) {
            event.setCancelled(true);
            return;
        }

        event.setCancelled(true);

        if(event.getItem().getItemMeta().getDisplayName().contains("Default " + profile.getCurrentDuel().getLadder().getName() + " Kit")) {
            profile.getCurrentDuel().getLadder().getDefaultInv().apply(player);
            return;
        }

        List<PlayerKit> playerKits = profile.getCustomKits().get(profile.getCurrentDuel().getLadder());
        for(PlayerKit playerKit : playerKits) {
            if(C.strip(playerKit.getName()).contains(C.strip(event.getItem().getItemMeta().getDisplayName()))) {
                playerKit.getPlayerInv().apply(player);
                break;
            }
        }
    }

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        if(event.getEntity().getType() != EntityType.ENDER_PEARL)
            return;
        if(!((event.getEntity().getShooter() instanceof Player)))
            return;

        Player player = (Player) event.getEntity().getShooter();
        Profile profile = Profile.getByPlayer(player);
        if(!profile.isInGame()) {
            event.setCancelled(true);
            return;
        }

        player.setExp(1.0F);
        new EnderPearlTask(player).runTaskTimerAsynchronously(Practice.getInstance(), 2L, 1L);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onQuit(PlayerQuitEvent event) {
        handleLeave(event);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onKick(PlayerKickEvent event) {
        handleLeave(event);
    }

    private void handleLeave(PlayerEvent event) {
        Profile profile = Profile.getByPlayer(event.getPlayer());
        if(profile.getCurrentDuel() == null)
            return;

        Duel duel = profile.getCurrentDuel();
        if(duel.getState() != DuelState.PLAYING)
            return;

        duel.saveInventory(profile.getUuid());

        duel.quit(event.getPlayer());
    }
}
