package net.practice.practice.game.player.data;

import lombok.Getter;
import lombok.Setter;
import net.practice.practice.util.chat.C;
import net.practice.practice.util.itemstack.I;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class PlayerKit {

    @Getter @Setter private String name;
    @Getter @Setter private PlayerInv playerInv;

    public PlayerKit(String name) {
        this.name = name;
    }

    public String getDisplay() {
        return C.color(name);
    }

    public void save(Player player) {
        playerInv = PlayerInv.fromPlayer(player.getInventory());
    }

    public static void addItem(Inventory inventory, PlayerKit kit, int i, int j) {
        int[] indexes = new int[] {i, i + 9, i + 18, i + 27};

        if(kit.getPlayerInv() == null) {
            inventory.setItem(indexes[0], new I(Material.BOOK).name("&b&lKit #" + j).lore("&7Click me to save kit #" + j + "."));
            inventory.setItem(indexes[1], null);
            inventory.setItem(indexes[2], null);
            inventory.setItem(indexes[3], null);
        } else {
            inventory.setItem(indexes[0], new I(Material.BOOK).name("&b&lKit " + j).lore(kit.getName() == null ? "" : kit.getName()));
            inventory.setItem(indexes[1], new I(Material.INK_SACK).durability(10).name("&7Load Kit &e&l#" + j));
            inventory.setItem(indexes[2], new I(Material.NAME_TAG).name("&7Rename Kit &e&l#" + j));
            inventory.setItem(indexes[3], new I(Material.INK_SACK).durability(1).name("&7Remove Kit &e&l#" + j));
        }
    }
}
