package net.practice.practice.game.arena.map;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import lombok.Getter;
import lombok.Setter;
import net.practice.practice.Practice;
import net.practice.practice.game.arena.Arena;
import net.practice.practice.game.arena.ArenaType;
import net.practice.practice.spawn.SpawnHandler;
import net.practice.practice.task.MapCleanRunnable;
import net.practice.practice.util.CustomLoc;
import org.bukkit.*;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MapLoc {

    @Getter @Setter private static World arenaWorld;
    @Getter @Setter private static int[] grid = null;
    @Getter @Setter private static List<MapLoc> maps = new ArrayList<>();
    @Getter @Setter private CustomLoc spawnOne, spawnTwo;
    @Getter @Setter private Arena arena;
    @Getter @Setter private MapState state;
    @Getter @Setter private Set<BlockState> changedBlocks = new HashSet<>();
    @Getter @Setter private boolean generated = false;
    @Getter @Setter private CustomLoc pastePoint;

    public MapLoc() {
        this.arena = Arena.getNextArena();
        this.state = MapState.READY;

        if (Bukkit.getWorld("arenas1") == null) {
            loadWorld();
        } else if (arenaWorld == null) {
            setArenaWorld(Bukkit.getWorld("arenas1"));
        }
    }

    public void generate() {
        Vector point = getNextPoint();
        pasteAndSet(point);

        generated = true;

        getMaps().add(this);
    }

    public MapLoc(Location spawnOne, Location spawnTwo, Arena arena, Location pastePoint) {
        if (Bukkit.getWorld("arenas1") == null) {
            loadWorld();
        } else if (arenaWorld == null) {
            setArenaWorld(Bukkit.getWorld("arenas1"));
        }

        this.spawnOne = CustomLoc.fromBukkit(spawnOne, getArenaWorld());
        this.spawnTwo = CustomLoc.fromBukkit(spawnTwo, getArenaWorld());
        this.arena = arena;
        this.state = MapState.READY;
        this.pastePoint = CustomLoc.fromBukkit(pastePoint, getArenaWorld());
        getMaps().add(this);
    }

    public static MapLoc getRandomMap(boolean readyOnly, boolean isSpleef) {
        if (isSpleef) {
            return getReadySpleefRandomMap();
        } else if (readyOnly) {
            return getReadyRandomMap();
        } else {
            return getAnyRandomMap();
        }
    }

    public static MapLoc getReadyRandomMap() {

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

    public static MapLoc getReadySpleefRandomMap() {

        List<MapLoc> readySpleefMaps = new ArrayList<>();
        for (MapLoc map : getMaps()) {
            if (map.getState() == MapState.READY && map.getArena().getType() == ArenaType.SPLEEF) {
                readySpleefMaps.add(map);
            }
        }

        if (readySpleefMaps.isEmpty()) return null;

        int random = ThreadLocalRandom.current().nextInt(0, readySpleefMaps.size());
        return readySpleefMaps.get(random);
    }

    public static MapLoc getAnyRandomMap() {

        List<MapLoc> mapsThatAreNotSpleef = new ArrayList<>();
        for (MapLoc map : getMaps()) {
            if (map.getArena().getType() == ArenaType.NORMAL) {
                mapsThatAreNotSpleef.add(map);
            }
        }

        if (mapsThatAreNotSpleef.isEmpty()) return null;

        int random = ThreadLocalRandom.current().nextInt(0, mapsThatAreNotSpleef.size());
        return mapsThatAreNotSpleef.get(random);
    }

    public static List<MapLoc> getGeneratedMaps() {
        List<MapLoc> generated = new ArrayList<>();
        for (MapLoc map : getMaps()) {
            if (map.isGenerated()) {
                generated.add(map);
            }
        }
        return generated;
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
                this.pastePoint = new CustomLoc(getArenaWorld().getName(), pastePoint.getX(), pastePoint.getY(), pastePoint.getZ(), 0, 0);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.spawnOne = new CustomLoc(getArenaWorld().getName(), pastePoint.getX() + arena.getRelSpawnOne().getX(), pastePoint.getY() + arena.getRelSpawnOne().getY(),
                    pastePoint.getZ() + arena.getRelSpawnOne().getZ(), arena.getRelSpawnOne().getYaw(), arena.getRelSpawnOne().getPitch());
            this.spawnTwo = new CustomLoc(getArenaWorld().getName(), pastePoint.getX() + arena.getRelSpawnTwo().getX(), pastePoint.getY() + arena.getRelSpawnTwo().getY(),
                    pastePoint.getZ() + arena.getRelSpawnTwo().getZ(), arena.getRelSpawnTwo().getYaw(), arena.getRelSpawnTwo().getPitch());
        }
    }

    public void addChangedBlock(BlockState blockState) {
        for (BlockState state : getChangedBlocks()) {
            if (state.getLocation().equals(blockState.getLocation())) {
                return;
            }
        }
        getChangedBlocks().add(blockState);
        Bukkit.broadcastMessage(blockState.getType() + "");
    }

    public boolean containsBlock(BlockState otherState) {
        for (BlockState state : getChangedBlocks()) {
            if (state.getLocation().equals(otherState.getLocation())) {
                return true;
            }
        }
        return false;
    }

//    @SuppressWarnings("deprecation")
//    public void clean() {
//        File schem = new File("plugins/WorldEdit/schematics/" + arena.getSchematicName() + ".schematic");
//        if (schem.exists()) {
//            try {
//                /*List<Block> blocksToChange = new ArrayList<>();
//
//                Clipboard c = ClipboardFormat.SCHEMATIC.load(schem).getClipboard();
//                if (c != null) {
//                    for (int x = c.getMinimumPoint().getBlockX(); x < c.getMaximumPoint().getBlockX(); x++) {
//                        for (int y = c.getMinimumPoint().getBlockY(); y < c.getMaximumPoint().getBlockY(); y++) {
//                            for (int z = c.getMinimumPoint().getBlockZ(); z < c.getMaximumPoint().getBlockZ(); z++) {
//                                Block realBlock = getArenaWorld().getBlockAt(x, y, z);
//                                BaseBlock clipBlock = c.getBlock(new Vector(x, y, z));
//                                if (realBlock.getTypeId() != clipBlock.getType()) {
//                                    realBlock.setTypeId(clipBlock.getType());
//                                    realBlock.setData((byte) clipBlock.getData());
//                                    blocksToChange.add(realBlock);
//                                }
//                            }
//                        }
//                    }
//                }
//
//                Iterator<Block> replaceBlocks = blocksToChange.iterator();
//                new BukkitRunnable() {
//                    @Override
//                    public void run() {
//                        if (replaceBlocks.hasNext()) {
//                            Block oldBlock = replaceBlocks.next();
//                            Block replace = getArenaWorld().getBlockAt(oldBlock.getLocation());
//                            replace.setType(oldBlock.getType());
//                            replace.setData(oldBlock.getData());
//                            Bukkit.broadcastMessage(oldBlock.getType().name());
//                        } else {
//                            Bukkit.broadcastMessage("done");
//                            setState(MapState.READY);
//                            getBlocksToReplace().clear();
//                            this.cancel();
//                        }
//                    }
//                }.runTaskTimer(Practice.getInstance(), 1L, 1L);*/
//
//                setState(MapState.CLEANING);
//                ClipboardFormat.SCHEMATIC.load(schem).paste(new BukkitWorld(getArenaWorld()), new Vector(pastePoint.getX(), pastePoint.getY(), pastePoint.getZ()),
//                        false, true, null).setFastMode(true);
//                setState(MapState.READY);
//                //Bukkit.broadcastMessage("ye ye");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    @SuppressWarnings("deprecation")
    public void clean() {
        setState(MapState.CLEANING);

        new MapCleanRunnable(this).runTaskTimer(Practice.getInstance(), 1L, 1L);
    }
}
