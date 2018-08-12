package land.nub.practice.spawn;

import land.nub.practice.inventory.item.ItemStorage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PartyHandler {

    public static void spawn(Player player, boolean leader) {
        player.getInventory().setContents(getPartyInventory(leader, false));
        player.updateInventory();
    }

    public static void spawn(Player player, boolean leader, boolean leave) {
        player.getInventory().setContents(getPartyInventory(leader, leave));
        player.updateInventory();
    }

    public static ItemStack[] getPartyInventory(boolean leader, boolean leave) {
        ItemStack[] items = new ItemStack[36];

        if(leader) {
            if(leave) {
                items[4] = ItemStorage.LEAVE_QUEUE;
            } else {
                items[0] = ItemStorage.PARTY_UNRANKED_DUOS;
                items[1] = ItemStorage.PARTY_RANKED_DUOS;

                items[3] = ItemStorage.PARTY_INFO;

                items[5] = ItemStorage.PARTY_FIGHT_OTHERS;
                items[6] = ItemStorage.PARTY_EVENT;

                items[8] = ItemStorage.PARTY_LOBBY_LEAVE;
            }
        } else {
            items[0] = ItemStorage.PARTY_INFO;

            items[8] = ItemStorage.PARTY_LOBBY_LEAVE;
        }

        return items;
    }
}
