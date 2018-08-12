package land.nub.practice.task;

import land.nub.practice.inventory.inventories.PartiesInventory;
import land.nub.practice.inventory.inventories.RankedInv;
import land.nub.practice.inventory.inventories.UnrankedInv;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdateInventoryTask extends BukkitRunnable {

    @Override
    public void run() {
        RankedInv.updateInventory();
        UnrankedInv.updateInventory();
        PartiesInventory.updateInventory();
    }
}
