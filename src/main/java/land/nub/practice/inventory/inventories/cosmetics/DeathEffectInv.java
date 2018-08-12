package net.practice.practice.inventory.inventories.cosmetics;

import net.practice.practice.Practice;
import net.practice.practice.cosmetic.CosmeticProfile;
import net.practice.practice.cosmetic.deatheffect.DeathEffect;
import net.practice.practice.util.chat.C;
import net.practice.practice.util.itemstack.I;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;

public class DeathEffectInv {

    public static void updateInventory(Player player) {
        if (!player.getOpenInventory().getTitle().contains("Death Effects")) return;
        Inventory inventory = player.getOpenInventory().getTopInventory();

        inventory.setItem(0, new I(Material.INK_SACK).durability(1).clearLore().name(C.color("&cBack to Cosmetics")));

        int index = 11;
        for (DeathEffect deathEffect : DeathEffect.values()) {

            if((index + 2) % 9 == 0)
                index += 4;

            CosmeticProfile profile = CosmeticProfile.getCosmeticProfile(player.getUniqueId());
            boolean unlocked = profile.isUnlocked(deathEffect);
            boolean enabled = profile.isEnabled(deathEffect);

            I item = new I(deathEffect.getMaterial()).name("&6" + deathEffect.getName()).flag(ItemFlag.HIDE_POTION_EFFECTS).clearLore()
                    .lore(" ")
                    .lore(enabled ? "&aEnabled" : unlocked ? "&cDisabled" : "&cNot Unlocked!");
            if (enabled)
                item.enchantment(Enchantment.OXYGEN).flag(ItemFlag.HIDE_ENCHANTS);

            inventory.setItem(index, item);

            index++;
        }
    }

    public static void openInventory(Player player) {
        Inventory inventory = Practice.getInstance().getServer().createInventory(null, 36, C.color("&cDeath Effects"));
        player.openInventory(inventory);
        updateInventory(player);
    }
}
