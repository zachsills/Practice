package net.practice.practice.cosmetic.deatheffect;

import net.practice.practice.Practice;
import net.practice.practice.cosmetic.CosmeticProfile;
import net.practice.practice.cosmetic.Cosmetic;
import net.practice.practice.game.player.Profile;
import net.practice.practice.inventory.inventories.cosmetics.CosmeticInv;
import net.practice.practice.inventory.inventories.cosmetics.DeathEffectInv;
import net.practice.practice.util.chat.C;
import net.practice.practice.util.particle.ParticleEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class DeathEffectListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null
                || event.getClickedInventory().getTitle() == null
                || event.getClickedInventory().getType() == InventoryType.PLAYER
                || !event.getClickedInventory().getTitle().contains("Death Effects")
                || event.getCurrentItem() == null
                || !event.getCurrentItem().hasItemMeta()
                || !event.getCurrentItem().getItemMeta().hasDisplayName()
                || !(event.getWhoClicked() instanceof Player)) return;

        event.setCancelled(true);


        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        String display = item.getItemMeta().getDisplayName();

        if (display.contains("Back to Cosmetics")) {
            CosmeticInv.openInventory(player);
        }

        for (DeathEffect deathEffect : DeathEffect.values()) {
            if (display.contains(deathEffect.getName())) {
                CosmeticProfile cosmeticProfile = CosmeticProfile.getCosmeticProfile(player.getUniqueId());

                if (!cosmeticProfile.isEnabled(deathEffect)) {
                    if (cosmeticProfile.isUnlocked(deathEffect)) {
                        cosmeticProfile.setEnabled(deathEffect, true);
                        DeathEffectInv.updateInventory(player);
//                        player.sendMessage(C.color("&f\u00BB &7" + deathEffect.getName() + " &aEnabled!"));
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 0.7F, 1.4F);
                    } else {
                        player.sendMessage(C.color("&f\u00BB &7" + deathEffect.getName() + " &cis not unlocked! (Purchase Cosmetics @ donate.nub.land)"));
                        player.playSound(player.getLocation(), Sound.NOTE_BASS, 1.0F, 1.4F);
                    }
                } else {
                    cosmeticProfile.setEnabled(deathEffect, false);
                    DeathEffectInv.updateInventory(player);
//                    player.sendMessage(C.color("&f\u00BB &7" + deathEffect.getName() + " &cDisabled!"));
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 0.7F, 0.8F);
                }
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        CosmeticProfile cosmeticProfile = CosmeticProfile.getCosmeticProfile(player.getUniqueId());
        for (Cosmetic cosmetic : cosmeticProfile.getEnabledCosmetics()) {
            if (cosmetic instanceof DeathEffect) {
                DeathEffect deathEffect = ((DeathEffect) cosmetic);

                Practice plugin = Practice.getInstance();

                Profile profile = Profile.getByPlayer(player);
                if (profile.getCurrentDuel() == null) return;
                List<Player> duel = profile.getCurrentDuel().getAllPlayers();

                if (deathEffect.getType() == DeathEffectType.PARTICLE) {
                    BukkitTask task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () ->
                            new ParticleEffect(deathEffect.getData()).sendToPlayers(player.getLocation(), duel), 2L, 1L);
                    plugin.getServer().getScheduler().runTaskLater(Practice.getInstance(), task::cancel, 15L);
                } else if (deathEffect.getType() == DeathEffectType.WORLD) {
                    if (deathEffect == DeathEffect.LIGHTNING) {
                        for (Player dueler : duel) {
                            sendLightning(dueler, player.getLocation());
                        }
                    }
                }
            }
        }
    }

    /**
     * From gaelitoelquesito  -  https://www.spigotmc.org/threads/fake-lightning-strike.97700/
     */
    public static void sendLightning(Player p, Location l){
        Class<?> light = getNMSClass("EntityLightning");
        try {
            Constructor<?> constu =
                    light
                            .getConstructor(getNMSClass("World"),
                                    double.class, double.class,
                                    double.class, boolean.class, boolean.class);
            Object wh  = p.getWorld().getClass().getMethod("getHandle").invoke(p.getWorld());
            Object lighobj = constu.newInstance(wh, l.getX(), l.getY(), l.getZ(), false, false);

            Object obj =
                    getNMSClass("PacketPlayOutSpawnEntityWeather")
                            .getConstructor(getNMSClass("Entity")).newInstance(lighobj);

            sendPacket(p, obj);
            p.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 1, 1);
        } catch (NoSuchMethodException | SecurityException |
                IllegalAccessException | IllegalArgumentException |
                InvocationTargetException | InstantiationException e) {
            e.printStackTrace();
        }
    }

    public static Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet"))
                    .invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
