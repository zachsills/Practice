package net.practice.practice.listener.listeners;

import net.practice.practice.Practice;
import net.practice.practice.game.arena.map.MapLoc;
import net.practice.practice.game.duel.Duel;
import net.practice.practice.game.duel.DuelState;
import net.practice.practice.game.player.Profile;
import net.practice.practice.game.player.data.PlayerKit;
import net.practice.practice.task.EnderPearlTask;
import net.practice.practice.util.chat.C;
import net.practice.practice.util.itemstack.I;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.text.DecimalFormat;
import java.util.List;

public class DuelListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Profile profile = Profile.getByPlayer(event.getEntity());
        if(profile.getCurrentDuel() == null)
            return;

        Duel duel = profile.getCurrentDuel();
        event.getDrops().clear();
        event.setDroppedExp(0);
        event.setDeathMessage(null);

        duel.saveInventory(profile.getUuid());
        profile.handleDeath();

        if(duel.getState() != DuelState.STARTING && duel.getState() != DuelState.PLAYING)
            return;

        duel.kill(event.getEntity());

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
        if(profile.getCurrentDuel().getState() != DuelState.PLAYING)
            return;

        if(event.getEntity().getEffects().stream().noneMatch(potionEffect -> potionEffect.getType().equals(PotionEffectType.HEAL)))
            return;

        boolean missed = event.getIntensity(player) < .5;
        if(missed) {
            int missedPots = profile.getCurrentDuel().getMissedPots().getOrDefault(player, 0);
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
    public void onInteractPot(PlayerInteractEvent event) {
        if(!event.getAction().name().contains("RIGHT"))
            return;

        ItemStack item = event.getItem();
        if(item == null || item.getType() != Material.POTION)
            return;

        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);
        if(!profile.isInGame()) {
            event.setCancelled(true);
            return;
        }

        if(profile.getCurrentDuel().getState() != DuelState.PLAYING)
            return;

        if(item.getDurability() == 16421) {
            int thrownPots = profile.getCurrentDuel().getThrownPots().getOrDefault(player, 0);
            profile.getCurrentDuel().getThrownPots().put(player, thrownPots + 1);
        }
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

    @EventHandler
    public void enderPearlDamageDecrease(EntityDamageByEntityEvent event) {
        if (event.getEntityType() == EntityType.PLAYER) {
            if (event.getDamager().getType() == EntityType.ENDER_PEARL) {
                event.setDamage(2.0);
            }
        }
    }

    @EventHandler
    public void arrowHealthDisplay(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player) {
            Player target = (Player)event.getEntity();
            if (event.getDamager() instanceof Arrow) {
                Arrow arrow = (Arrow)event.getDamager();
                if (arrow.getShooter() instanceof Player) {
                    Player shooter = (Player)arrow.getShooter();
                    if (target.getHealth() - event.getFinalDamage() > 0.0) {
                        double n = (target.getHealth() - event.getFinalDamage()) / 2.0;
                        double rounded = Math.round(n * 2.0) / 2.0;
                        shooter.sendMessage(C.color("&6%player% &eis now at &6%health%&4%heartEmoji%")
                                .replace("%player%", target.getName())
                                .replace("%health%", new DecimalFormat("#0.0").format(rounded))
                                .replace("%heartEmoji%", StringEscapeUtils.unescapeJava("\u2764")));
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void spleefEvent(BlockBreakEvent event) {
        Profile profile = Profile.getByPlayer(event.getPlayer());
        if (profile != null && profile.isInGame() && profile.getCurrentDuel().getState().equals(DuelState.PLAYING)) {
            if (profile.getCurrentDuel() != null && profile.getCurrentDuel().getLadder().isSpleef()) {
                if (event.getBlock().getType().name().contains("SNOW")) {
                    event.setCancelled(true);

                    // This is required because it cancels the event.
                    Duel duel = profile.getCurrentDuel();
                    if (duel.getState() == DuelState.PLAYING && duel.getMap() != null) {
                        MapLoc mapLoc = duel.getMap();
                        mapLoc.addChangedBlock(event.getBlock().getState());
                    }

                    event.getBlock().setType(Material.AIR);
                    event.getPlayer().getInventory().addItem(new I(Material.SNOW_BALL).amount(1));
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void spleefDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType() == EntityType.PLAYER && event.getEntity().getType() == EntityType.PLAYER) {
            Profile profile = Profile.getByPlayer((Player) event.getDamager());
            if (profile != null && profile.getCurrentDuel() != null && profile.getCurrentDuel().getLadder().isSpleef()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void spleefHunger(FoodLevelChangeEvent event) {
        if (event.getEntity().getType() == EntityType.PLAYER) {
            Profile profile = Profile.getByPlayer((Player) event.getEntity());
            if (profile != null && profile.getCurrentDuel() != null && (profile.getCurrentDuel().getLadder().isSpleef() || profile.getCurrentDuel().getLadder().getName().contains("Soup"))) {
                ((Player) event.getEntity()).setFoodLevel(20);
                ((Player) event.getEntity()).setSaturation(0);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        if (MapLoc.getArenaWorld() != null) {
            if (event.getWorld().getUID().equals(MapLoc.getArenaWorld().getUID())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void enderPearlGlitchFix(PlayerTeleportEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            Location to = event.getTo();
            if (to.clone().add(0.5, 0, 0).getBlock().getType() != Material.AIR || to.clone().add(1.0, 0, 0).getBlock().getType() != Material.AIR) {
                event.setTo(to.clone().add(-0.5, 0, 0));
            }
            if (to.clone().add(-0.5, 0, 0).getBlock().getType() != Material.AIR || to.clone().add(-1.0, 0, 0).getBlock().getType() != Material.AIR) {
                event.setTo(to.clone().add(0.5, 0, 0));
            }
            if (to.clone().add(0, 0, 0.5).getBlock().getType() != Material.AIR || to.clone().add(0, 0, 1.0).getBlock().getType() != Material.AIR) {
                event.setTo(to.clone().add(0, 0, -0.5));
            }
            if (to.clone().add(0, 0, -0.5).getBlock().getType() != Material.AIR || to.clone().add(0, 0, -1.0).getBlock().getType() != Material.AIR) {
                event.setTo(to.clone().add(0, 0, 0.5));
            }
            if (to.clone().add(0, 1, 0).getBlock().getType() != Material.AIR) {
                event.setTo(to.clone().add(0, -0.5, 0));
            }
        }
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

    @EventHandler
    public void onConsume(PlayerItemConsumeEvent event) {
        if(event.getItem().getType() != Material.GOLDEN_APPLE)
            return;
        if(!event.getItem().hasItemMeta() || !event.getItem().getItemMeta().hasDisplayName())
            return;

        event.setCancelled(true);

        Player player = event.getPlayer();
        if(player.getItemInHand().getAmount() > 1)
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - 1);
        else
            player.setItemInHand(new ItemStack(Material.AIR));
        player.updateInventory();

        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 120, 1), true);
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20 * 10, 1), true);
    }

    @EventHandler
    public void onInteractSoup(PlayerInteractEvent event) {
        if(!event.getAction().name().contains("RIGHT"))
            return;

        Player player = event.getPlayer();
        if(player.getItemInHand() != null && player.getItemInHand().getType() != Material.MUSHROOM_SOUP)
            return;

        Profile profile = Profile.getByPlayer(player);
        if(!profile.isInGame())
            return;
        if(player.getHealth() >= player.getMaxHealth())
            return;

        event.setCancelled(true);
        event.setUseItemInHand(Event.Result.DENY);

        player.setItemInHand(new ItemStack(Material.BOWL));
        player.updateInventory();

        double healAmount = 7.0;
        if(player.getHealth() + healAmount >= player.getMaxHealth())
            player.setHealth(player.getMaxHealth());
        else
            player.setHealth(player.getHealth() + healAmount);
    }
}
