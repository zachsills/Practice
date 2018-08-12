package land.nub.practice.command.commands;

import land.nub.practice.inventory.inventories.cosmetics.CosmeticInv;
import land.nub.practice.util.command.Command;
import land.nub.practice.util.command.CommandArgs;

public class CosmeticCommand {

    @Command(name = "cosmetics", aliases = { "cosmetic", "c" }, playerOnly = true, description = "View your cosmetic menu.")
    public void onInventory(CommandArgs args) {
        CosmeticInv.openInventory(args.getPlayer());
    }
}
