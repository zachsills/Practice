package land.nub.practice.task;

import lombok.Getter;
import land.nub.practice.game.arena.map.MapLoc;
import land.nub.practice.game.arena.map.MapState;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MapCleanRunnable extends BukkitRunnable {

    @Getter private static List<MapCleanRunnable> running = new ArrayList<>();
    @Getter private MapLoc mapLoc;
    private Iterator<BlockState> changedBlocksIterator;

    public MapCleanRunnable(MapLoc mapLoc) {
        this.mapLoc = mapLoc;
        this.changedBlocksIterator = mapLoc.getChangedBlocks().iterator();
        running.add(this);
    }

    @Override
    public void run() {
        if (changedBlocksIterator.hasNext()) {
            BlockState oldBlock = changedBlocksIterator.next();
            Block replace = MapLoc.getArenaWorld().getBlockAt(oldBlock.getLocation());
            //if (replace.getState().getType() == oldBlock.getType()) return;
            replace.setType(oldBlock.getType());
            replace.setData(oldBlock.getRawData());
            //Bukkit.broadcastMessage(oldBlock.getType().name());
        } else {
            //Bukkit.broadcastMessage("done");
            mapLoc.setState(MapState.READY);
            mapLoc.getChangedBlocks().clear();
            this.cancel();
            running.remove(this);
        }
    }
}
