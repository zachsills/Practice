package net.practice.practice.command.commands;

import net.practice.practice.Practice;
import net.practice.practice.game.arena.Arena;
import net.practice.practice.game.arena.ArenaType;
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
            Practice.getInstance().getTestArenaManager().removeArena(arena);
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
            if (arena.setRelSpawnOneRelative(args.getPlayer().getLocation())) {
                args.getPlayer().sendMessage(C.color("&eSet spawnOne in arena " + arena.getName() + " to &7(&a"
                        + LocUtils.serializeLocation(args.getPlayer().getLocation()) + "&7)&e."));
            } else {
                args.getPlayer().sendMessage(C.color("&cYou must set the paste point first!"));
            }
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
            if (arena.setRelSpawnTwoRelative(args.getPlayer().getLocation())) {
                args.getPlayer().sendMessage(C.color("&eSet spawnTwo in arena " + arena.getName() + " to &7(&a"
                        + LocUtils.serializeLocation(args.getPlayer().getLocation()) + "&7)&e."));
            } else {
                args.getPlayer().sendMessage(C.color("&cYou must set the paste point first!"));
            }
        } else {
            args.getPlayer().sendMessage(C.color("&cThat arena doesn't exist! Create it with /arena create"));
        }
    }

    @Command(name = "arena.pastePoint", permission = "practice.arena", playerOnly = true, description = "Manage arenas.")
    public void onArenaPastePoint(CommandArgs args) {
        if (args.length() != 1) {
            sendHelp(args.getPlayer());
            return;
        }

        Arena arena = Arena.getArena(args.getArgs(0));
        if (arena != null) {
            arena.setPastePoint(args.getPlayer().getLocation());
            args.getPlayer().sendMessage(C.color("&eSet paste point in arena " + arena.getName() + " to &7(&a"
                    + LocUtils.serializeLocation(args.getPlayer().getLocation()) + "&7)&e."));
        } else {
            args.getPlayer().sendMessage(C.color("&cThat arena doesn't exist! Create it with /arena create"));
        }
    }

    @Command(name = "arena.list", permission = "practice.arena", playerOnly = true, description = "Manage arenas.")
    public void onArenaList(CommandArgs args) {
        args.getPlayer().sendMessage(ChatColor.YELLOW + "Current Arenas: ");
        for(Arena arena : Arena.getArenas().values())
            args.getPlayer().sendMessage(C.color("&7- " + arena.getDisplayName()));
    }

    @Command(name = "arena.schemName", permission = "practice.arena", playerOnly = true, description = "Manage arenas.")
    public void onArenaSchemName(CommandArgs args) {
        if (args.length() != 2) {
            sendHelp(args.getPlayer());
            return;
        }

        Arena arena = Arena.getArena(args.getArgs(0));
        if (arena != null) {
            arena.setSchematicName(args.getArgs(1));
            args.getPlayer().sendMessage(C.color("&eSet the schematic name for arena " + arena.getName() + " to '" + args.getArgs(1) + "'"));
        } else {
            args.getPlayer().sendMessage(C.color("&cThat arena doesn't exist! Create it with /arena create"));
        }
    }

    @Command(name = "arena.type", permission = "practice.arena", playerOnly = true, description = "Manage arenas.")
    public void onArenaType(CommandArgs args) {
        if (args.length() != 2) {
            sendHelp(args.getPlayer());
            return;
        }

        Arena arena = Arena.getArena(args.getArgs(0));
        if (arena != null) {
            arena.setType(ArenaType.valueOf(args.getArgs(1)));
            args.getPlayer().sendMessage(C.color("&eSet the type of arena " + arena.getName() + " to '" + ArenaType.valueOf(args.getArgs(1)) + "'"));
        } else {
            args.getPlayer().sendMessage(C.color("&cThat arena doesn't exist! Create it with /arena create"));
        }
    }

    public void sendHelp(Player player) {
        player.sendMessage(C.color("&eArena Help"));
        player.sendMessage(C.color("&a/arena create <arena>"));
        player.sendMessage(C.color("&a/arena remove <arena>"));
        player.sendMessage(C.color("&a/arena schemName <arena> <schematic name>"));
        player.sendMessage(C.color("&a/arena type <arena> <type>"));
        player.sendMessage(C.color("&a/arena pastePoint <arena>"));
        player.sendMessage(C.color("&a/arena spawn1 <arena>"));
        player.sendMessage(C.color("&a/arena spawn2 <arena>"));
        player.sendMessage(C.color("&a/arena list <arena>"));
    }
}
