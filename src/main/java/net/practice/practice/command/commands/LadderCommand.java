package net.practice.practice.command.commands;

import net.practice.practice.Practice;
import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.game.player.data.PlayerInv;
import net.practice.practice.util.chat.C;
import net.practice.practice.util.command.Command;
import net.practice.practice.util.command.CommandArgs;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class LadderCommand {

    @Command(name = "ladder", aliases = { "game" }, permission = "practice.ladder", playerOnly = true, description = "Manage ladders.")
    public void onLadder(CommandArgs args) {
        sendHelp(args.getPlayer());
    }

    @Command(name = "ladder.create", aliases = { "game.create"}, permission = "practice.ladder", playerOnly = true, description = "Manage ladders.")
    public void onLadderCreate(CommandArgs args) {
        if(args.length() != 1) {
            sendHelp(args.getPlayer());
            return;
        }

        String name = args.getArgs(0);
        if(Ladder.getLadder(name) != null) {
            args.getPlayer().sendMessage(ChatColor.RED + "That ladder already exists.");
            return;
        }

        Ladder ladder = new Ladder(name);
        ladder.setDisplayIcon(Material.DIAMOND_SWORD);
        args.getPlayer().sendMessage(C.color("&eCreated ladder " + ladder.getDisplayName() + "&e."));
    }

    @Command(name = "ladder.remove", aliases = { "game.remove"}, permission = "practice.ladder", playerOnly = true, description = "Manage ladders.")
    public void onLadderRemove(CommandArgs args) {
        if(args.length() != 1) {
            sendHelp(args.getPlayer());
            return;
        }

        String name = args.getArgs(0);
        if(Ladder.getLadder(name) == null) {
            args.getPlayer().sendMessage(ChatColor.RED + "That ladder doesn't exist.");
            return;
        }

        Ladder ladder = Ladder.getLadder(name);
        Practice.getInstance().getLadderManager().removeLadder(ladder);
        args.getPlayer().sendMessage(C.color("&eRemoved ladder " + ladder.getDisplayName() + "&e."));
    }

    @Command(name = "ladder.set", aliases = { "game.set"}, permission = "practice.ladder", playerOnly = true, description = "Manage ladders.")
    public void onLadderSet(CommandArgs args) {
        if(args.length() != 2) {
            sendHelp(args.getPlayer());
            return;
        }

        String name = args.getArgs(0);
        if(Ladder.getLadder(name) == null) {
            args.getPlayer().sendMessage(ChatColor.RED + "That ladder doesn't exist.");
            return;
        }

        Ladder ladder = Ladder.getLadder(name);
        boolean newValue;
        switch(args.getArgs(1).toLowerCase()) {
            case "ranked": {
                ladder.setRanked(!ladder.isRanked());
                newValue = ladder.isRanked();
                break;
            }
            case "editable": {
                ladder.setEditable(!ladder.isEditable());
                newValue = ladder.isEditable();
                break;
            }
            case "build": {
                ladder.setBuildable(!ladder.isBuildable());
                newValue = ladder.isBuildable();
                break;
            }
            case "combo": {
                ladder.setCombo(!ladder.isCombo());
                newValue = ladder.isCombo();
                break;
            }
            default: {
                args.getPlayer().sendMessage(ChatColor.RED + "The attribute '" + args.getArgs(1) + "' doesn't exist for ladders.");
                return;
            }
        }

        args.getPlayer().sendMessage(C.color("&eSet attribute '&7" + args.getArgs(1).toLowerCase() + "&e' of " + ladder.getDisplayName() + "&e to " + newValue + "."));
    }

    @Command(name = "ladder.seticon", aliases = { "game.seticon"}, permission = "practice.ladder", playerOnly = true, description = "Manage ladders.")
    public void onLadderSetIcon(CommandArgs args) {
        if(args.length() != 2) {
            sendHelp(args.getPlayer());
            return;
        }

        String name = args.getArgs(0);
        if(Ladder.getLadder(name) == null) {
            args.getPlayer().sendMessage(ChatColor.RED + "That ladder doesn't exist.");
            return;
        }

        Ladder ladder = Ladder.getLadder(name);
        Material material = Material.getMaterial(args.getArgs(1).toUpperCase());
        if(material == null) {
            args.getPlayer().sendMessage(ChatColor.RED + "The material '" + args.getArgs(1) + "' doesn't exist.");
            return;
        }

        args.getPlayer().sendMessage(C.color("&eSet material icon of " + ladder.getDisplayName() + "&e to " + WordUtils.capitalizeFully(material.name()) + "."));
    }

    @Command(name = "ladder.inv", aliases = { "game.inv"}, permission = "practice.ladder", playerOnly = true, description = "Manage ladders.")
    public void onLadderInv(CommandArgs args) {
        if(args.length() != 2) {
            sendHelp(args.getPlayer());
            return;
        }

        String name = args.getArgs(0);
        if(Ladder.getLadder(name) == null) {
            args.getPlayer().sendMessage(ChatColor.RED + "That ladder doesn't exist.");
            return;
        }

        Ladder ladder = Ladder.getLadder(name);
        switch(args.getArgs(1)) {
            case "set": {
                ladder.setDefaultInv(PlayerInv.fromPlayer(args.getPlayer().getInventory()));
                args.getPlayer().sendMessage(C.color("&eSet inventory of " + ladder.getDisplayName() + "&e to your current inventory."));
                return;
            }
            case "load": {
                if(ladder.getDefaultInv() == null) {
                    args.getPlayer().sendMessage(C.color("&cThat ladder doesn't have a default inventory."));
                    return;
                }

                ladder.getDefaultInv().apply(args.getPlayer());
                args.getPlayer().sendMessage(C.color("&eLoaded the inventory of " + ladder.getDisplayName() + "&e."));
                return;
            }
            default:
                sendHelp(args.getPlayer());
        }
    }

    public void sendHelp(Player player) {
        player.sendMessage(C.color("&eLadder Help"));
        player.sendMessage(C.color("&a/ladder create <name>"));
        player.sendMessage(C.color("&a/ladder remove <name>"));
        player.sendMessage(C.color("&a/ladder set <name> <ranked;editable;build;combo>"));
        player.sendMessage(C.color("&a/ladder inv <name> <set;load>"));
        player.sendMessage(C.color("&a/ladder seticon <name> <material>"));
    }
}
