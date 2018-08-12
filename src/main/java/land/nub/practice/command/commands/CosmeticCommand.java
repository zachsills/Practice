package net.practice.practice.command.commands;

import net.practice.practice.inventory.inventories.cosmetics.CosmeticInv;
import net.practice.practice.util.command.Command;
import net.practice.practice.util.command.CommandArgs;

public class CosmeticCommand {

    @Command(name = "cosmetics", aliases = { "cosmetic", "c" }, playerOnly = true, description = "View your cosmetic menu.")
    public void onInventory(CommandArgs args) {
        CosmeticInv.openInventory(args.getPlayer());
    }
}
