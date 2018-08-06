package net.practice.practice.task;

import com.mojang.authlib.GameProfile;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class NameUpdateTask extends BukkitRunnable {

    @Getter private Player player;
    @Getter private String newName;

    @Getter private Collection<? extends Player> targets;

    public NameUpdateTask(Player player, String newName, Player... targets) {
        this.player = player;
        this.newName = newName;
        this.targets = Arrays.asList(targets);
    }

    public NameUpdateTask(Player player, String newName, Collection<? extends Player> targets) {
        this.player = player;
        this.newName = newName;
        this.targets = targets;
    }

    @Override
    public void run() {
        changeName(player, newName);
    }

    public static void changeName(Player player, String newName) {
        changeName(player, newName, Bukkit.getOnlinePlayers());
    }

    public static void changeName(Player player, String newName, Player target) {
        changeName(player, newName, Collections.singleton(target));
    }

    public static void changeName(Player player, String newName, Collection<? extends Player> targets) {
        for(Player pl : targets) {
            if(pl.getUniqueId().equals(player.getUniqueId()))
                continue;

            //REMOVES THE PLAYER
            ((CraftPlayer) pl).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, ((CraftPlayer) player).getHandle()));
            //CHANGES THE PLAYER'S GAME PROFILE
            GameProfile gp = ((CraftPlayer) player).getProfile();
            try {
                Field field = GameProfile.class.getDeclaredField("name");
                field.setAccessible(true);

                Field field2 = GameProfile.class.getDeclaredField("properties");
                field2.setAccessible(true);

                GameProfile newGp = new GameProfile(gp.getId(), gp.getName());

                field.set(newGp, "Thryl");
                field2.set(newGp, gp.getProperties());

                EntityPlayer fakePlayer = new EntityPlayer(
                        MinecraftServer.getServer(),
                        MinecraftServer.getServer().getWorldServer(0),
                        newGp,
                        new PlayerInteractManager(MinecraftServer.getServer().getWorldServer(0)));

                ((CraftPlayer)pl).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, fakePlayer));
                ((CraftPlayer)pl).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(player.getEntityId()));
                ((CraftPlayer)pl).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(fakePlayer));
            } catch (IllegalAccessException | NoSuchFieldException ex) {
                throw new IllegalStateException(ex);
            }
        }
    }
}
