package net.practice.practice.task;

import net.practice.practice.game.arenatest.map.MapLoc;
import net.practice.practice.util.chat.C;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class MapCreateTask extends BukkitRunnable {

    @Override
    public void run() {
        if (MapLoc.getMaps().size() < 10) {
            new MapLoc();

            if (MapLoc.getMaps().size() % 10 == 0) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.isOp() || player.hasPermission("practice.admin")) {
                        player.sendMessage(C.color("&6Map Gen: &eGenerated " + MapLoc.getMaps().size() + " maps."));
                    }
                }
            }
        } else {

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.isOp() || player.hasPermission("practice.admin")) {
                    player.sendMessage(C.color("&6Map Gen: &aGeneration completed! Generated a total of " + MapLoc.getMaps().size() + " maps."));
                }
            }

            this.cancel();
        }
    }
}
