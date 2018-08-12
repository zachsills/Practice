package land.nub.practice.inventory.inventories;

import lombok.Getter;
import land.nub.practice.game.party.Party;
import land.nub.practice.game.party.PartyManager;
import land.nub.practice.game.party.PartyState;
import land.nub.practice.util.itemstack.I;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class PartiesInventory {

    @Getter private static final Inventory inventory = Bukkit.createInventory(null, 54, "Fight Other Parties");

    public static void openInventory(Player player) {
        player.openInventory(inventory);
    }

//    public static void updateInventory(Party party) {
//        Player leader = Bukkit.getPlayer(party.getLeader());
//        String owner = leader.getName();
//
//        I skull = new I(SpawnHandler.getSkull(owner)).name(C.color(""));
//    }

    public static void updateInventory() {
        inventory.clear();

        for(Party party : PartyManager.getParties().values()) {
            Player leader = Bukkit.getPlayer(party.getLeader());
            String owner = leader.getName();

            I yay;
            if(party.getState() == PartyState.PLAYING)
                yay = new I(Material.INK_SACK)
                        .amount(party.getSize())
                        .durability(1)
                        .name("&b" + owner + "'s Party")
                        .lore(" ").lore("&cCurrently In-Game.");
            else
                yay = new I(Material.INK_SACK)
                        .amount(party.getSize())
                        .durability(10)
                        .name("&b" + owner + "'s Party")
                        .lore(" ").lore("&ePlayers: " + party.getSize()).lore("&7Click here to send a duel request.");

            inventory.addItem(yay);
        }
    }
}
