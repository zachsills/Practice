package land.nub.practice.inventory.inventories;

import land.nub.practice.game.ladder.Ladder;
import land.nub.practice.game.player.Profile;
import land.nub.practice.game.player.data.PlayerKit;
import land.nub.practice.util.chat.C;
import land.nub.practice.util.itemstack.I;
import land.nub.practice.Practice;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;

public class EditorInv {

    public static void openInventory(Player player) {
        Inventory inventory = Practice.getInstance().getServer().createInventory(null, 27, C.color("&eSelect a Ladder..."));
        int index = 10;
        for(Ladder ladder : Ladder.getLadders().values()) {
            if(!ladder.isEditable())
                continue;
            if((index + 1) % 9 == 0)
                index += 2;

            inventory.setItem(index, new I(ladder.getDisplayIcon()).clearLore().flag(ItemFlag.HIDE_POTION_EFFECTS));
            index++;
        }

        player.openInventory(inventory);
    }

    public static void openSavingInventory(Player player) {
        Profile profile = Profile.getByPlayer(player);
        Ladder ladder = profile.getEditing();

        Inventory inventory = Bukkit.createInventory(null, 9 * 4, "Save Kits for " + ladder.getName());

        int i = 0, j = 1;
        for(PlayerKit kit : profile.getCustomKits().get(ladder)) {
            PlayerKit.addItem(inventory, kit, i, j);

            j += 1;
            i += 2;
        }

        player.openInventory(inventory);
    }

    public static void openLadderItems(Player player, Ladder ladder) {
        Inventory inventory = Bukkit.createInventory(null, 54, "Editing " + ladder.getName());
        inventory.setContents(ladder.getEditor().getContents());

        player.openInventory(inventory);
    }
}
