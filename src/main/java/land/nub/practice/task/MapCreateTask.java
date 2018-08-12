package land.nub.practice.task;

import land.nub.practice.util.chat.C;
import land.nub.practice.game.arena.map.MapLoc;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Level;

public class MapCreateTask extends BukkitRunnable {

    private int maps;

    public MapCreateTask(int maps) {
        this.maps = maps;
    }

    @Override
    public void run() {
        if (MapLoc.getGeneratedMaps().size() < maps) {
            new MapLoc().generate();

            if (MapLoc.getGeneratedMaps().size() % 5 == 0) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.isOp() || player.hasPermission("practice.admin")) {
                        player.sendMessage(C.color("&6Map Gen: &eGenerated " + MapLoc.getGeneratedMaps().size() + " maps."));
                    }
                }
                Bukkit.getLogger().log(Level.INFO, C.color("&6Map Gen: &eGenerated " + MapLoc.getGeneratedMaps().size() + " maps."));
            }
        } else {

            MapLoc.getArenaWorld().save();

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.isOp() || player.hasPermission("practice.admin")) {
                    player.sendMessage(C.color("&6Map Gen: &aGeneration completed, and world saved! Generated a total of "
                            + MapLoc.getGeneratedMaps().size() + " maps, with a total of " + MapLoc.getMaps().size() + " maps."));
                }
            }
            Bukkit.getLogger().log(Level.INFO, C.color("&6Map Gen: &aGeneration completed, and world saved! Generated a total of "
                    + MapLoc.getGeneratedMaps().size() + " maps, with a total of " + MapLoc.getMaps().size() + " maps."));

            this.cancel();
        }
    }
}
