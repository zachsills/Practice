package net.practice.practice.command.commands;

import net.practice.practice.util.chat.C;
import net.practice.practice.util.command.Command;
import net.practice.practice.util.command.CommandArgs;
import org.bukkit.entity.Player;

public class PartyCommand {

    @Command(name = "party", aliases = { "p" }, playerOnly = true, description = "Party commands.")
    public void onParty(CommandArgs args) {
        sendHelp(args.getPlayer());
    }

    private void sendHelp(Player player) {
        player.sendMessage(C.color("&e&lParty Help"));
        
    }
}
