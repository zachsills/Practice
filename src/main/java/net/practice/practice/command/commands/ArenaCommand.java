package net.practice.practice.command.commands;

import net.practice.practice.Practice;
import net.practice.practice.game.arena.Arena;
import net.practice.practice.util.LocUtils;
import net.practice.practice.util.chat.C;
import net.practice.practice.util.command.Command;
import net.practice.practice.util.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ArenaCommand {

    @Command(name = "arena", permission = "practice.arena", playerOnly = true, description = "Manage arenas.")
    public void onArena(CommandArgs args) {
        sendHelp(args.getPlayer());
    }

    @Command(name = "arena.create", permission = "practice.arena", playerOnly = true, description = "Manage arenas.")
    public void onArenaCreate(CommandArgs args) {
        if (args.length() != 1) {
            sendHelp(args.getPlayer());
            return;
        }

        String name = args.getArgs(0);
        if (Arena.getArena(name) != null) {
            args.getPlayer().sendMessage(ChatColor.RED + "That arena already exists.");
            return;
        }

        new Arena(name);
        args.getPlayer().sendMessage(C.color("&eCreated a new arena called &a" + args.getArgs(0) + "."));
    }

    @Command(name = "arena.remove", permission = "practice.arena", playerOnly = true, description = "Manage arenas.")
    public void onArenaRemove(CommandArgs args) {
        if(args.length() != 1) {
            sendHelp(args.getPlayer());
            return;
        }

        Arena arena = Arena.getArena(args.getArgs(0));
        if (arena != null) {
            Practice.getInstance().getArenaManager().removeArena(arena);
            args.getPlayer().sendMessage(C.color("&eRemoved arena " + arena.getDisplayName() + "&e."));
        } else {
            args.getPlayer().sendMessage(C.color("&cThat arena doesn't exist! Create it with /arena create"));
        }
    }

    @Command(name = "arena.spawn1", permission = "practice.arena", playerOnly = true, description = "Manage arenas.")
    public void onArenaSpawn1(CommandArgs args) {
        if (args.length() != 1) {
            sendHelp(args.getPlayer());
            return;
        }

        Arena arena = Arena.getArena(args.getArgs(0));
        if (arena != null) {
            arena.setSpawnOne(args.getPlayer().getLocation());
            args.getPlayer().sendMessage(C.color("&eSet spawnOne in arena " + arena.getName() + " to &7(&a" + LocUtils.serializeLocation(args.getPlayer().getLocation()) + "&7)&e."));
        } else {
            args.getPlayer().sendMessage(C.color("&cThat arena doesn't exist! Create it with /arena create"));
        }
    }

    @Command(name = "arena.spawn2", permission = "practice.arena", playerOnly = true, description = "Manage arenas.")
    public void onArenaSpawn2(CommandArgs args) {
        if (args.length() != 1) {
            sendHelp(args.getPlayer());
            return;
        }

        Arena arena = Arena.getArena(args.getArgs(0));
        if (arena != null) {
            arena.setSpawnTwo(args.getPlayer().getLocation());
            args.getPlayer().sendMessage(C.color("&eSet spawnTwo in arena " + arena.getName() + " to &7(&a" + LocUtils.serializeLocation(args.getPlayer().getLocation()) + "&7)&e."));
        } else {
            args.getPlayer().sendMessage(C.color("&cThat arena doesn't exist! Create it with /arena create"));
        }
    }

    @Command(name = "arena.builder", permission = "practice.arena", playerOnly = true, description = "Manage arenas.")
    public void onArenaSetBuilder(CommandArgs args) {
        if (args.length() != 2) {
            sendHelp(args.getPlayer());
            return;
        }

        Arena arena = Arena.getArena(args.getArgs(0));
        String builderName = args.getArgs(1);
        if (arena != null) {
            arena.setBuilder(builderName);
            args.getPlayer().sendMessage(C.color("&eSet builder for arena " + arena.getName() + " to &7" + builderName + "&e."));
        } else {
            args.getPlayer().sendMessage(C.color("&cThat arena doesn't exist! Create it with /arena create"));
        }
    }

    public void sendHelp(Player player) {
        player.sendMessage(C.color("&eArena Help"));
        player.sendMessage(C.color("&a/arena create <name>"));
        player.sendMessage(C.color("&a/arena remove <name>"));
        player.sendMessage(C.color("&a/arena spawn1 <name>"));
        player.sendMessage(C.color("&a/arena spawn2 <name>"));
        player.sendMessage(C.color("&a/arena builder <name> <builder's name>"));
    }
}
