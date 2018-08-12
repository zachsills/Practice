package land.nub.practice.command.commands;

import land.nub.practice.Practice;
import land.nub.practice.game.arena.map.MapLoc;
import land.nub.practice.task.MapCreateTask;
import land.nub.practice.util.LocUtils;
import land.nub.practice.util.chat.C;
import land.nub.practice.util.command.Command;
import land.nub.practice.util.command.CommandArgs;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MapCommand {

    @Command(name = "map", permission = "practice.map", playerOnly = true, description = "Manage maps.")
    public void onMap(CommandArgs args) {
        sendHelp(args.getPlayer());
    }

    @Command(name = "map.generate", permission = "practice.map", playerOnly = true, description = "Manage maps.")
    public void onMapGenerate(CommandArgs args) {
        if (args.length() != 1) {
            sendHelp(args.getPlayer());
            return;
        }

        MapLoc.recreateWorld();
        new MapCreateTask(Integer.parseInt(args.getArgs(0))).runTaskTimer(Practice.getInstance(), 1L, 5L);
        args.getPlayer().sendMessage(C.color("&eStarted generating the arena world."));
    }

    @Command(name = "map.list", permission = "practice.map", playerOnly = true, description = "Manage maps.")
    public void onMapList(CommandArgs args) {
        args.getPlayer().sendMessage(ChatColor.YELLOW + "Current Maps: ");
        int index = 0;
        for(MapLoc map : MapLoc.getMaps()) {
            args.getPlayer().sendMessage(C.color("&6Index: " + index));
            args.getPlayer().sendMessage(C.color("  &eArena: &f" + map.getArena().getDisplayName()));
            args.getPlayer().sendMessage(C.color("  &eLocation: &f" + LocUtils.serializeLocation(map.getPastePoint())));
            args.getPlayer().sendMessage(C.color("  &eState: &f" + map.getState().name()));
            index++;
        }
    }

    public void sendHelp(Player player) {
        player.sendMessage(C.color("&eMap Help"));
        player.sendMessage(C.color("&a/map generate <number of maps>"));
        player.sendMessage(C.color("&a/map list"));
    }
}
