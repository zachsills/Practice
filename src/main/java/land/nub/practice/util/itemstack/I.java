package land.nub.practice.util.itemstack;

import land.nub.practice.util.chat.C;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a chainable builder for {@link ItemStack}s in {@link Bukkit}
 * <br>
 * Example Usage:<br>
 * {@code ItemStack is = new ItemBuilder(Material.LEATHER_HELMET).amount(2).data(4).durability(4).enchantment(Enchantment.ARROW_INFINITE).enchantment(Enchantment.LUCK, 2).name(ChatColor.RED + "the name").lore(ChatColor.GREEN + "line 1").lore(ChatColor.BLUE + "line 2").color(Color.MAROON);
 *
 * @author MiniDigger, computerwizjared
 * @version 1.2
 */
public class I extends ItemStack {
    /**
     * Initializes the builder with the given {@link Material}
     *
     * @param mat the {@link Material} to start the builder from
     * @since 1.0
     */
    public I(final Material mat) {
        super(mat);
    }

    /**
     * Inits the builder with the given {@link ItemStack}
     *
     * @param is the {@link ItemStack} to start the builder from
     * @since 1.0
     */
    public I(final ItemStack is) {
        super(is);
    }

    /**
     * Changes the amount of the {@link ItemStack}
     *
     * @param amount the new amount to set
     * @return this builder for chaining
     * @since 1.0
     */
    public I amount(final int amount) {
        setAmount(amount);
        return this;
    }

    /**
     * Changes the display name of the {@link ItemStack}
     *
     * @param name the new display name to set
     * @return this builder for chaining
     * @since 1.0
     */
    public I name(final String name) {
        final ItemMeta meta = getItemMeta();
        meta.setDisplayName(C.color(name));
        setItemMeta(meta);
        return this;
    }

    /**
     * Adds a new line to the lore of the {@link ItemStack}
     *
     * @param text the new line to add
     * @return this builder for chaining
     * @since 1.0
     */
    public I lore(final String text) {
        final ItemMeta meta = getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }
        lore.add(C.color(text));
        meta.setLore(lore);
        setItemMeta(meta);
        return this;
    }

    /**
     * Changes the durability of the {@link ItemStack}
     *
     * @param durability the new durability to set
     * @return this builder for chaining
     * @since 1.0
     */
    public I durability(final int durability) {
        setDurability((short) durability);
        return this;
    }

    /**
     * Changes the data of the {@link ItemStack}
     *
     * @param data the new data to set
     * @return this builder for chaining
     * @since 1.0
     */
    @SuppressWarnings("deprecation")
    public I data(final int data) {
        setData(new MaterialData(getType(), (byte) data));
        return this;
    }

    /**
     * Adds an {@link Enchantment} with the given level to the {@link ItemStack}
     *
     * @param enchantment the enchantment to add
     * @param level       the level of the enchantment
     * @return this builder for chaining
     * @since 1.0
     */
    public I enchantment(final Enchantment enchantment, final int level) {
        addUnsafeEnchantment(enchantment, level);
        return this;
    }

    /**
     * Adds an {@link Enchantment} with the level 1 to the {@link ItemStack}
     *
     * @param enchantment the enchantment to add
     * @return this builder for chaining
     * @since 1.0
     */
    public I enchantment(final Enchantment enchantment) {
        addUnsafeEnchantment(enchantment, 1);
        return this;
    }

    /**
     * Changes the {@link Material} of the {@link ItemStack}
     *
     * @param material the new material to set
     * @return this builder for chaining
     * @since 1.0
     */
    public I type(final Material material) {
        setType(material);
        return this;
    }

    /**
     * Clears the lore of the {@link ItemStack}
     *
     * @return this builder for chaining
     * @since 1.0
     */
    public I clearLore() {
        final ItemMeta meta = getItemMeta();
        meta.setLore(new ArrayList<>());
        setItemMeta(meta);
        return this;
    }

    /**
     * Clears the list of {@link Enchantment}s of the {@link ItemStack}
     *
     * @return this builder for chaining
     * @since 1.0
     */
    public I clearEnchantments() {
        getEnchantments().keySet().forEach(this::removeEnchantment);
        return this;
    }

    /**
     * Sets the {@link Color} of a part of leather armor
     *
     * @param color the {@link Color} to use
     * @return this builder for chaining
     * @since 1.1
     */
    public I color(Color color) {
        if (getType() == Material.LEATHER_BOOTS || getType() == Material.LEATHER_CHESTPLATE || getType() == Material.LEATHER_HELMET
                || getType() == Material.LEATHER_LEGGINGS) {
            LeatherArmorMeta meta = (LeatherArmorMeta) getItemMeta();
            meta.setColor(color);
            setItemMeta(meta);
            return this;
        } else {
            throw new IllegalArgumentException("color() only applicable for leather armor!");
        }
    }

    /**
     * Adds an {@link ItemFlag} to the {@link ItemStack}
     *
     * @param flag the flag to add
     * @return this builder for chaining
     */
    public I flag(ItemFlag flag) {
        final ItemMeta meta = getItemMeta();
        meta.addItemFlags(flag);
        setItemMeta(meta);
        return this;
    }

    /**
     * Clears the list of {@link ItemFlag}s of the {@link ItemStack}
     *
     * @return this builder for chaining
     */
    public I clearFlags() {
        final ItemMeta meta = getItemMeta();
        meta.getItemFlags().forEach(meta::removeItemFlags);
        setItemMeta(meta);
        return this;
    }
}
