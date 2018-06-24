package net.practice.practice.game.arenatest.map;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import lombok.Getter;
import lombok.Setter;
import net.practice.practice.Practice;
import net.practice.practice.game.arenatest.Arena;
import net.practice.practice.spawn.SpawnHandler;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MapLoc {

    @Getter @Setter private static World arenaWorld;
    @Getter @Setter private static int[] grid = null;
    @Getter @Setter private static List<MapLoc> maps = new ArrayList<>();
    @Getter @Setter private Location spawnOne, spawnTwo;
    @Getter @Setter private Arena arena;
    @Getter @Setter private MapState state;
    @Getter @Setter private Set<Block> blocksToReplace = new HashSet<>();

    public MapLoc() {
        setArena(Arena.getRandomArena());
        setState(MapState.READY);

        if (Bukkit.getWorld("arenas1") == null) {
            loadWorld();
        } else if (arenaWorld == null) {
            setArenaWorld(Bukkit.getWorld("arenas1"));
        }

        Vector point = getNextPoint();
        pasteAndSet(point);

        getMaps().add(this);
    }

    public MapLoc(Location spawnOne, Location spawnTwo, Arena arena) {

        if (Bukkit.getWorld("arenas1") == null) {
            loadWorld();
        } else if (arenaWorld == null) {
            setArenaWorld(Bukkit.getWorld("arenas1"));
        }

        this.spawnOne = spawnOne;
        this.spawnTwo = spawnTwo;
        this.arena = arena;
        this.state = MapState.READY;
        getMaps().add(this);
    }

    public static MapLoc getRandomMap() {

        List<MapLoc> readyMaps = new ArrayList<>();
        for (MapLoc map : getMaps()) {
            if (map.getState() == MapState.READY) {
                readyMaps.add(map);
            }
        }

        if (readyMaps.isEmpty()) return null;

        int random = ThreadLocalRandom.current().nextInt(0, readyMaps.size());
        return readyMaps.get(random);
    }

    public static void recreateWorld() {

        deleteWorld("arenas1");

        World world = Practice.getInstance().getServer().createWorld(new WorldCreator("arenas1").generatorSettings("1;0;1").type(WorldType.FLAT).generateStructures(false));
        world.setAutoSave(false);
        world.setGameRuleValue("doMobSpawning", "false");
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setGameRuleValue("doFireTick", "false");

        setArenaWorld(world);
    }

    public static void loadWorld() {

        World world = Practice.getInstance().getServer().createWorld(new WorldCreator("arenas1").generatorSettings("1;0;1").type(WorldType.FLAT).generateStructures(false));
        world.setAutoSave(false);
        world.setGameRuleValue("doMobSpawning", "false");
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setGameRuleValue("doFireTick", "false");

        setArenaWorld(world);
    }

    private static void deleteWorld(String name) {
        if (Bukkit.getWorld(name) != null) {
            World world = Bukkit.getWorld(name);
            for (Player player : world.getPlayers()) {
                SpawnHandler.spawn(player);
            }
            /*for (Chunk chunk : world.getLoadedChunks()) {
                chunk.unload();
            }*/
            Bukkit.unloadWorld(name, false);

            /*File folder = new File(Bukkit.getWorldContainer().getAbsolutePath() + "/" + name);
            for (File file : folder.listFiles()) {
                file.delete();
            }*/
        }
    }

    private Vector getNextPoint() {

        if (grid == null) {
            grid = new int[3];
            grid[0] = 0;
            grid[1] = 100;
            grid[2] = 0;
        } else if (grid[0] < 25000) {
            grid[0] += 500;
        } else if (grid[2] < 25000) {
            if (grid[0] == 25000)
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
                ClipboardFormat.SCHEMATIC.load(schem).paste(new BukkitWorld(getArenaWorld()), pastePoint, false, false, null).setFastMode(true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.spawnOne = new Location(getArenaWorld(), pastePoint.getX() + arena.getRelSpawnOne().getX(), pastePoint.getY() + arena.getRelSpawnOne().getY(),
                    pastePoint.getZ() + arena.getRelSpawnOne().getZ(), arena.getRelSpawnOne().getYaw(), arena.getRelSpawnOne().getPitch());
            this.spawnTwo = new Location(getArenaWorld(), pastePoint.getX() + arena.getRelSpawnTwo().getX(), pastePoint.getY() + arena.getRelSpawnTwo().getY(),
                    pastePoint.getZ() + arena.getRelSpawnTwo().getZ(), arena.getRelSpawnTwo().getYaw(), arena.getRelSpawnTwo().getPitch());
        }
    }

    @SuppressWarnings("deprecation")
    public void clean() {
        setState(MapState.CLEANING);

        Iterator<Block> replaceBlocks = getBlocksToReplace().iterator();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (replaceBlocks.hasNext()) {
                    Block oldBlock = replaceBlocks.next();
                    Block replace = getArenaWorld().getBlockAt(oldBlock.getLocation());
                    replace.setType(oldBlock.getType());
                    replace.setData(oldBlock.getData());
                } else {
                    setState(MapState.READY);
                    this.cancel();
                }
            }
        }.runTaskTimer(Practice.getInstance(), 1L, 1L);
    }
}
