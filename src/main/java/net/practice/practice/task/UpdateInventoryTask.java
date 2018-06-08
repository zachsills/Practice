package net.practice.practice.task;

import net.practice.practice.inventory.inventories.RankedInv;
import net.practice.practice.inventory.inventories.UnrankedInv;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdateInventoryTask extends BukkitRunnable {

    @Override
    public void run() {
        UnrankedInv.updateInventory();
        RankedInv.updateInventory();
    }
}