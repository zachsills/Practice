package net.practice.practice.game.arenatest.map;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import lombok.Getter;
import lombok.Setter;
import net.practice.practice.Practice;
import net.practice.practice.game.arenatest.Arena;
import org.bukkit.*;

import java.io.File;
import java.io.IOException;

public class MapLoc {

    @Getter @Setter private static World arenaWorld;
    @Getter @Setter private static int[] grid = null;
    @Getter @Setter private Location spawnOne, spawnTwo;
    @Getter @Setter private Arena arena;

    public MapLoc(Arena arena) {
        this.arena = arena;

        if (Bukkit.getWorld("arenas1") == null) {
            createWorld();
        } else if (arenaWorld == null) {
            setArenaWorld(Bukkit.getWorld("arenas1"));
        }

        Vector point = getNextPoint();
        pasteAndSet(point);
    }

    public static void createWorld() {
        World world = Practice.getInstance().getServer().createWorld(new WorldCreator("arenas1").generatorSettings("1;0;1").type(WorldType.FLAT).generateStructures(false));
        world.setAutoSave(false);
        world.setGameRuleValue("doMobSpawning", "false");
        world.setGameRuleValue("doDaylightCycle", "false");

        setArenaWorld(world);
    }

    private Vector getNextPoint() {

        if (grid == null) {
            grid = new int[3];
            grid[0] = 0;
            grid[1] = 100;
            grid[2] = 0;
        } else if (grid[0] < 1000000) {
            grid[0] += 500;
        } else if (grid[2] < 1000000) {
            if (grid[0] == 1000000)
                grid[0] = 0;
            grid[2] += 500;
        }

        return getCurrentPoint();
    }
    private Vector getCurrentPoint() {
        return new Vector(grid[0], grid[1], grid[2]);
    }

    private void pasteAndSet(Vector pastePoint) {
        File schem = new File("plugins/WorldEdit/schematics/" + arena.getSchematicName() + ".schematic");
        if (schem.exists()) {
            try {
                ClipboardFormat.SCHEMATIC.load(schem).paste(new BukkitWorld(getArenaWorld()), pastePoint, false, false, null)
                .setFastMode(true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.spawnOne = new Location(getArenaWorld(), pastePoint.getX() + arena.getRelSpawnOne().getX(), pastePoint.getY() + arena.getRelSpawnOne().getY(),
                    pastePoint.getZ() + arena.getRelSpawnOne().getZ(), arena.getRelSpawnOne().getYaw(), arena.getRelSpawnOne().getPitch());
            this.spawnTwo = new Location(getArenaWorld(), pastePoint.getX() + arena.getRelSpawnTwo().getX(), pastePoint.getY() + arena.getRelSpawnTwo().getY(),
                    pastePoint.getZ() + arena.getRelSpawnTwo().getZ(), arena.getRelSpawnTwo().getYaw(), arena.getRelSpawnTwo().getPitch());
        }
    }
}
