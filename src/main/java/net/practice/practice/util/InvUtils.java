package net.practice.practice.util;

import net.practice.practice.game.player.data.PlayerInv;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class InvUtils {

    public static String invToString(final PlayerInv inv) {
        if(inv == null)
            return null;

        StringBuilder builder = new StringBuilder();
        ItemStack[] armor = inv.getArmor();

        for(int i = 0; i < armor.length; i++) {
            if(i == 3) {
                if(armor[i] == null) {
                    builder.append(itemStackToString(new ItemStack(Material.AIR)));
                } else {
                    builder.append(itemStackToString(armor[3]));
                }
            } else {
                if(armor[i] == null) {
                    builder.append(itemStackToString(new ItemStack(Material.AIR))).append(";");
                } else {
                    builder.append(itemStackToString(armor[i])).append(";");
                }
            }
        }

        builder.append("|");

        for(int i = 0; i < inv.getItems().length; ++i) {
            builder.append(i)
                    .append("#")
                    .append(itemStackToString(inv.getItems()[i]))
                    .append((i == inv.getItems().length - 1) ? "" : ";");
        }

        return builder.toString();
    }

    public static PlayerInv invFromString(final String in) {
        if(in == null || in.equals("unset") || in.equals("null") || in.equals("'null'"))
            return null;

        final PlayerInv inv = new PlayerInv();
        final String[] data = in.split("\\|");
        final ItemStack[] armor = new ItemStack[data[0].split(";").length];

        for(int i = 0; i < data[0].split(";").length; ++i)
            armor[i] = itemStackFromString(data[0].split(";")[i]);

        inv.setArmor(armor);
        ItemStack[] contents = new ItemStack[data[1].split(";").length];

        for(String s : data[1].split(";")) {
            int slot = Integer.parseInt(s.split("#")[0]);
            if(s.split("#").length == 1) {
                contents[slot] = null;
            } else {
                contents[slot] = itemStackFromString(s.split("#")[1]);
            }
        }

        inv.setItems(contents);
        return inv;
    }

    @SuppressWarnings("deprecation")
    public static String itemStackToString(final ItemStack item) {
        final StringBuilder builder = new StringBuilder();

        if(item != null) {
            String isType = String.valueOf(item.getType().getId());
            builder.append("t@").append(isType);

            if(item.getDurability() != 0) {
                String isDurability = String.valueOf(item.getDurability());
                builder.append(":d@").append(isDurability);
            }

            if(item.getAmount() != 1) {
                String isAmount = String.valueOf(item.getAmount());
                builder.append(":a@").append(isAmount);
            }

            Map<Enchantment, Integer> isEnch = item.getEnchantments();
            if(isEnch.size() > 0) {
                for(Map.Entry<Enchantment, Integer> ench : isEnch.entrySet()) {
                    builder.append(":e@").append(ench.getKey().getId()).append("@").append(ench.getValue());
                }
            }

            if(item.hasItemMeta()) {
                ItemMeta imeta = item.getItemMeta();
                if(imeta.hasDisplayName()) {
                    builder.append(":dn@").append(imeta.getDisplayName());
                }
                if(imeta.hasLore()) {
                    builder.append(":l@").append(imeta.getLore());
                }
            }
        }

        return builder.toString();
    }

    @SuppressWarnings("deprecation")
    public static ItemStack itemStackFromString(final String in) {
        ItemStack item = null;
        ItemMeta meta = null;
        String[] split;

        if(in == null || in.equals("unset") || in.equals("null") || in.equals("'null'"))
            return new ItemStack(Material.AIR);

        String[] data = split = in.split(":");
        for(String itemInfo : split) {
            final String[] itemAttribute = itemInfo.split("@");
            final String s2 = itemAttribute[0];

            switch(s2) {
                case "t": {
                    item = new ItemStack(Material.getMaterial(Integer.valueOf(itemAttribute[1])));
                    meta = item.getItemMeta();
                    break;
                }
                case "d":
                    if(item != null) {
                        item.setDurability(Short.valueOf(itemAttribute[1]));
                        break;
                    }

                    break;
                case "a":
                    if(item != null) {
                        item.setAmount(Integer.valueOf(itemAttribute[1]));
                        break;
                    }

                    break;
                case "e":
                    if(item != null) {
                        item.addEnchantment(Enchantment.getById(Integer.valueOf(itemAttribute[1])), Integer.valueOf(itemAttribute[2]));
                        break;
                    }

                    break;
                case "dn":
                    if(meta != null) {
                        meta.setDisplayName(itemAttribute[1]);
                        break;
                    }

                    break;
                case "l":
                    itemAttribute[1] = itemAttribute[1].replace("[", "");
                    itemAttribute[1] = itemAttribute[1].replace("]", "");
                    List<String> lore = Arrays.asList(itemAttribute[1].split(","));

                    for(int x = 0; x < lore.size(); ++x) {
                        String s = lore.get(x);

                        if(s != null) {
                            if(s.toCharArray().length != 0) {
                                if(s.charAt(0) == ' ')
                                    s = s.replaceFirst(" ", "");
                                lore.set(x, s);
                            }
                        }
                    }

                    if(meta != null) {
                        meta.setLore(lore);
                        break;
                    }
                    break;
            }
        }

        if(meta != null && (meta.hasDisplayName() || meta.hasLore()))
            item.setItemMeta(meta);

        return item;
    }

    public static void clear(final Player player) {
        for (final PotionEffect potion : player.getActivePotionEffects()) {
            player.removePotionEffect(potion.getType());
        }
        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setMaximumNoDamageTicks(20);
        player.setFoodLevel(20);
        player.setHealth(player.getMaxHealth());
        player.setFireTicks(0);
        player.setFallDistance(0.0f);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
        player.updateInventory();
    }
}
