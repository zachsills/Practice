package net.practice.practice.command.commands;

import net.practice.practice.Practice;
import net.practice.practice.game.arena.map.MapLoc;
import net.practice.practice.task.MapCreateTask;
import net.practice.practice.util.LocUtils;
import net.practice.practice.util.chat.C;
import net.practice.practice.util.command.Command;
import net.practice.practice.util.command.CommandArgs;
import org.bukkit.entity.Player;

public class PracticeCommand {

    @Command(name = "practice", permission = "practice.admin", playerOnly = true, description = "Manage practice.")
    public void onPractice(CommandArgs args) {
        sendHelp(args.getPlayer());
    }

    @Command(name = "practice.set", permission = "practice.admin", playerOnly = true, description = "Manage practice.")
    public void onPracticeSet(CommandArgs args) {
        if(args.length() != 1) {
            sendHelp(args.getPlayer());
            return;
        }

        String attribute = args.getArgs(0);
        switch(attribute.toLowerCase()) {
            case "spawn": {
                Practice.getInstance().setSpawn(args.getPlayer().getLocation());
                break;
            }
            case "editor": {
                Practice.getInstance().setEditor(args.getPlayer().getLocation());
                break;
            }
            default: {
                sendHelp(args.getPlayer());
                return;
            }
        }

        args.getPlayer().sendMessage(C.color("&eSet the &a" + attribute.toLowerCase() + "&e to &ayour location &7(&a" + LocUtils.serializeLocation(args.getPlayer().getLocation()) + "&7)&e."));
    }

    public void sendHelp(Player player) {
        player.sendMessage(C.color("&ePractice Help"));
        player.sendMessage(C.color("&a/practice set <spawn;editor>"));
    }
}
