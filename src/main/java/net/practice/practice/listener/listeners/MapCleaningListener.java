package net.practice.practice.listener.listeners;

import net.practice.practice.Practice;
import net.practice.practice.game.arenatest.map.MapLoc;
import net.practice.practice.game.duel.Duel;
import net.practice.practice.game.duel.DuelState;
import net.practice.practice.game.player.Profile;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class MapCleaningListener implements Listener {

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event) {
        if (MapLoc.getArenaWorld() != null) {
            if (event.getWorld().getUID().equals(MapLoc.getArenaWorld().getUID())) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;

        Profile profile = Profile.getByPlayer(event.getPlayer());

        if (profile.getCurrentDuel() != null) {
            Duel duel = profile.getCurrentDuel();
            if (duel.getState() == DuelState.PLAYING && duel.getMap() != null) {
                MapLoc mapLoc = duel.getMap();
                mapLoc.addBlockToReplace(event.getBlockReplacedState());
                //mapLoc.getBlocksToReplace().add(event.getBlockReplacedState().getBlock());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) return;

        Profile profile = Profile.getByPlayer(event.getPlayer());

        if (profile.getCurrentDuel() != null) {
            Duel duel = profile.getCurrentDuel();
            if (duel.getState() == DuelState.PLAYING && duel.getMap() != null) {
                MapLoc mapLoc = duel.getMap();
                mapLoc.addBlockToReplace(event.getBlock().getState());
                //mapLoc.getBlocksToReplace().add(event.getBlock());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if (event.isCancelled()) return;

        Profile profile = Profile.getByPlayer(event.getPlayer());

        if (profile.getCurrentDuel() != null) {
            Duel duel = profile.getCurrentDuel();
            if (duel.getState() == DuelState.PLAYING && duel.getMap() != null) {
                MapLoc mapLoc = duel.getMap();
                mapLoc.addBlockToReplace(event.getBlockClicked().getRelative(event.getBlockFace()).getState());
                //mapLoc.getBlocksToReplace().add(event.getBlockClicked().getRelative(event.getBlockFace()));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBucketFill(PlayerBucketFillEvent event) {
        if (event.isCancelled()) return;

        Profile profile = Profile.getByPlayer(event.getPlayer());

        if (profile.getCurrentDuel() != null) {
            Duel duel = profile.getCurrentDuel();
            if (duel.getState() == DuelState.PLAYING && duel.getMap() != null) {
                MapLoc mapLoc = duel.getMap();
                mapLoc.addBlockToReplace(event.getBlockClicked().getState());
                //mapLoc.getBlocksToReplace().add(event.getBlockClicked());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockFromTo(BlockFromToEvent event) {
        if (event.isCancelled()) return;

        Block from = event.getBlock();
        Block to = event.getToBlock();
        for (Profile profile : Profile.getProfiles().values()) {
            if (profile.getCurrentDuel() != null && profile.getCurrentDuel().getState() == DuelState.PLAYING && profile.getCurrentDuel().getMap() != null) {
                MapLoc mapLoc = profile.getCurrentDuel().getMap();
                for (BlockState blockState : mapLoc.getBlocksToReplace()) {
                    if (blockState.getLocation().equals(from.getState().getLocation())) { // If the blocksToReplace contains the from block which is liquid (so the player placed it)
                        //mapLoc.getBlocksToReplace().add(to.getState()); // Then add that to block to the blocks to replace for that player
                        //Bukkit.broadcastMessage(to.getType().name() + " " + to.getLocation());
                        BlockState xd = to.getState();
                        xd.setType(Material.AIR);
                        mapLoc.getBlocksToReplace().add(xd);
                        break;
                    }
                }
            }
        }

        /*new BukkitRunnable() { // Runnable is cus otherwise when cobble/obsidian generates it will just say AIR
            @Override
            public void run() {

            }
        }.runTaskLater(Practice.getInstance(), 2L);*/
    }
}