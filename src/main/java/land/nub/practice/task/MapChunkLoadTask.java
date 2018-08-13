package land.nub.practice.task;

import land.nub.practice.Practice;
import land.nub.practice.game.arena.map.MapLoc;
import land.nub.practice.util.chat.C;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class MapChunkLoadTask extends BukkitRunnable {

    @Override
    public void run() {
        int delay = 0, radius = 3;

        for (MapLoc loc : MapLoc.getMaps()) {

            World world = MapLoc.getArenaWorld();
            int spawnOneStartX = loc.getSpawnOne().toBukkit(world).getChunk().getX();
            int spawnOneStartZ = loc.getSpawnOne().toBukkit(world).getChunk().getZ();

            new BukkitRunnable() {
                int x = -radius, z = -radius;

                @Override
                public void run() {
                    if (x < radius) {
                        world.getChunkAtAsync(spawnOneStartX + x, spawnOneStartZ + z, Chunk::load);
                        //world.getChunkAt(spawnOneStartX + x, spawnOneStartZ + z).load();
                        x++;
                    } else if (z < radius) {
                        z++;
                        x = -radius;
                    } else {
                        for (Player player : Bukkit.getOnlinePlayers())
                            if (player.isOp())
                                player.sendMessage(C.color("&cMap chunk loading stage one completed!"));
                        MapLoc.setFinishedLoadingChunks(true);
                        this.cancel();
                    }
                }
            }.runTaskTimer(Practice.getInstance(), delay++, MapLoc.getMaps().size());

            int spawnTwoStartX = loc.getSpawnTwo().toBukkit(world).getChunk().getX();
            int spawnTwoStartZ = loc.getSpawnTwo().toBukkit(world).getChunk().getZ();

            new BukkitRunnable() {
                int x = -radius, z = -radius;

                @Override
                public void run() {
                    if (x < radius) {
                        world.getChunkAtAsync(spawnTwoStartX + x, spawnTwoStartZ + z, Chunk::load);
                        //world.getChunkAt(spawnTwoStartX + x, spawnTwoStartZ + z).load();
                        x++;
                    } else if (z < radius) {
                        z++;
                        x = -radius;
                    } else {
                        for (Player player : Bukkit.getOnlinePlayers())
                            if (player.isOp())
                                player.sendMessage(C.color("&cMap chunk loading stage two completed!"));
                        MapLoc.setFinishedLoadingChunks(true);
                        this.cancel();
                    }
                }
            }.runTaskTimer(Practice.getInstance(), delay++, MapLoc.getMaps().size());
        }
    }
}
