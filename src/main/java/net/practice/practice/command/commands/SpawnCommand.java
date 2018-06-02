package net.practice.practice.command.commands;

import net.practice.practice.spawn.SpawnHandler;
import net.practice.practice.util.chat.C;
import net.practice.practice.util.command.Command;
import net.practice.practice.util.command.CommandArgs;

public class SpawnCommand {

    @Command(name = "spawn", playerOnly = true, description = "Teleport to practice spawn.")
    public void onSpawn(CommandArgs args) {
        SpawnHandler.spawn(args.getPlayer());
        args.getPlayer().sendMessage(C.color("&f\u00BB &7Teleported to spawn."));
    }
}
