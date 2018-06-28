package net.practice.practice.util;

import net.practice.practice.game.player.data.PlayerInv;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
        for(final PotionEffect potion : player.getActivePotionEffects())
            player.removePotionEffect(potion.getType());

        player.setGameMode(GameMode.SURVIVAL);
        player.setAllowFlight(false);
        player.setFlying(false);
        player.setMaximumNoDamageTicks(20);
        player.setFoodLevel(20);
        player.setSaturation(5);
        player.setHealth(player.getMaxHealth());
        player.setFireTicks(0);
        player.getInventory().clear();
        player.getInventory().setArmorContents(null);

        player.updateInventory();
    }

    /**
     *
     * A method to serialize an {@link ItemStack} array to Base64 String.
     *
     * <p />
     *
     * Based off of {@link #toBase64(Inventory)}.
     *
     * @param items to turn into a Base64 String.
     * @return Base64 string of the items.
     * @throws IllegalStateException
     */
    public static String itemStackArrayToBase64(ItemStack[] items) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(items.length);

            // Save every element in the list
            for(int i = 0; i < items.length; i++)
                dataOutput.writeObject(items[i]);

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    /**
     * A method to serialize an inventory to Base64 string.
     *
     * <p />
     *
     * Special thanks to Comphenix in the Bukkit forums or also known
     * as aadnk on GitHub.
     *
     * <a href="https://gist.github.com/aadnk/8138186">Original Source</a>
     *
     * @param inventory to serialize
     * @return Base64 string of the provided inventory
     * @throws IllegalStateException
     */
    public static String toBase64(Inventory inventory) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(inventory.getSize());

            // Save every element in the list
            for (int i = 0; i < inventory.getSize(); i++) {
                dataOutput.writeObject(inventory.getItem(i));
            }

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

    /**
     *
     * A method to get an {@link Inventory} from an encoded, Base64, string.
     *
     * <p />
     *
     * Special thanks to Comphenix in the Bukkit forums or also known
     * as aadnk on GitHub.
     *
     * <a href="https://gist.github.com/aadnk/8138186">Original Source</a>
     *
     * @param data Base64 string of data containing an inventory.
     * @return Inventory created from the Base64 string.
     * @throws IOException
     */
    public static Inventory fromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            Inventory inventory = Bukkit.getServer().createInventory(null, dataInput.readInt());

            // Read the serialized inventory
            for(int i = 0; i < inventory.getSize(); i++)
                inventory.setItem(i, (ItemStack) dataInput.readObject());

            dataInput.close();
            return inventory;
        } catch(ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

    /**
     * Gets an array of ItemStacks from Base64 string.
     *
     * <p />
     *
     * Base off of {@link #fromBase64(String)}.
     *
     * @param data Base64 string to convert to ItemStack array.
     * @return ItemStack array created from the Base64 string.
     * @throws IOException
     */
    public static ItemStack[] itemStackArrayFromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            // Read the serialized inventory
            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }
}
