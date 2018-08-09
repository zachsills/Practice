package net.practice.practice.cosmetic;

import lombok.Getter;
import net.practice.practice.cosmetic.deatheffect.DeathEffect;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class CosmeticProfile {

    private static Map<UUID, CosmeticProfile> cosmeticProfiles = new HashMap<>();

    @Getter private List<Cosmetic> unlockedCosmetics, enabledCosmetics;
    @Getter private Player player;

    public CosmeticProfile(UUID uuid) {
        if (Bukkit.getPlayer(uuid) == null || !Bukkit.getPlayer(uuid).isOnline()) return;
        this.player = Bukkit.getPlayer(uuid);

        unlockedCosmetics = new ArrayList<>();
        enabledCosmetics = new ArrayList<>();



        cosmeticProfiles.put(uuid, this);
    }

    public static CosmeticProfile getCosmeticProfile(UUID uuid) {
        if (cosmeticProfiles.containsKey(uuid))
            return cosmeticProfiles.get(uuid);

        return new CosmeticProfile(uuid);
    }

    private void setUnlockedCosmetics(Player player) {
        unlockedCosmetics.clear();
        if (player.isOp() || player.hasPermission("practice.cosmetic.deatheffects")) {
            unlockedCosmetics.addAll(Arrays.asList(DeathEffect.values()));
        }
    }

    public boolean isEnabled(Cosmetic cosmetic) {
        return enabledCosmetics.contains(cosmetic);
    }

    public void setEnabled(Cosmetic cosmetic, boolean b) {
        if (b) {
            if (isTypeAlreadyEnabled(cosmetic))
                removeAllOfSameType(cosmetic);
            enabledCosmetics.add(cosmetic);
        } else {
            enabledCosmetics.remove(cosmetic);
        }
    }

    public boolean isUnlocked(Cosmetic cosmetic) {
        setUnlockedCosmetics(player);
        return unlockedCosmetics.contains(cosmetic);
    }

    public boolean isTypeAlreadyEnabled(Cosmetic cosmetic) {
        if (enabledCosmetics.isEmpty()) return false;
        for (Cosmetic enabled : enabledCosmetics) {
            if (enabled instanceof DeathEffect && cosmetic instanceof DeathEffect) {
                return true;
            }
        }
        return false;
    }

    public void removeAllOfSameType(Cosmetic cosmetic) {
        List<Cosmetic> toRemove = new ArrayList<>();
        for (Cosmetic enabled : enabledCosmetics) {
            if (enabled instanceof DeathEffect && cosmetic instanceof DeathEffect) {
                toRemove.add(enabled);
            }
        }
        enabledCosmetics.removeAll(toRemove);
    }
}
