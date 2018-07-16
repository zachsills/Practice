package net.practice.practice.listener.listeners;

import net.practice.practice.game.arena.map.MapLoc;
import net.practice.practice.game.duel.Duel;
import net.practice.practice.game.duel.DuelState;
import net.practice.practice.game.player.Profile;
import net.practice.practice.task.MapCleanRunnable;
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

public class MapCleaningListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) return;

        Profile profile = Profile.getByPlayer(event.getPlayer());

        if (profile.getCurrentDuel() != null) {
            Duel duel = profile.getCurrentDuel();
            if (duel.getState() == DuelState.PLAYING && duel.getMap() != null) {
                MapLoc mapLoc = duel.getMap();
                mapLoc.addChangedBlock(event.getBlockReplacedState());
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
                mapLoc.addChangedBlock(event.getBlock().getState());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBucketEmpty(PlayerBucketEmptyEvent event) {
        if (event.isCancelled()) return;

        Profile profile = Profile.getByPlayer(event.getPlayer());

        if (profile.getCurrentDuel() != null) {
            Duel duel = profile.getCurrentDuel();
            if (duel.getState() == DuelState.PLAYING && duel.getMap() != null) {
                MapLoc mapLoc = duel.getMap();
                mapLoc.addChangedBlock(event.getBlockClicked().getRelative(event.getBlockFace()).getState());
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBucketFill(PlayerBucketFillEvent event) {
        if (event.isCancelled()) return;

        Profile profile = Profile.getByPlayer(event.getPlayer());

        if (profile.getCurrentDuel() != null) {
            Duel duel = profile.getCurrentDuel();
            if (duel.getState() == DuelState.PLAYING && duel.getMap() != null) {
                MapLoc mapLoc = duel.getMap();
                mapLoc.addChangedBlock(event.getBlockClicked().getState());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockFromTo(BlockFromToEvent event) {
        if (event.isCancelled()) return;

        Block from = event.getBlock();
        Block to = event.getToBlock();
        if (to.getType() != Material.WATER && to.getType() != Material.STATIONARY_WATER && to.getType() != Material.LAVA && to.getType() != Material.STATIONARY_LAVA) {
            for (Profile profile : Profile.getProfiles().values()) {
                if (profile.getCurrentDuel() != null && profile.getCurrentDuel().getState() == DuelState.PLAYING && profile.getCurrentDuel().getMap() != null) {
                    MapLoc mapLoc = profile.getCurrentDuel().getMap();
                    for (BlockState blockState : mapLoc.getChangedBlocks()) {
                        if (blockState.getLocation().equals(from.getState().getLocation())) { // If the blocksToReplace contains the from block which is liquid (so the player placed it)
                            mapLoc.getChangedBlocks().add(to.getState()); // Then add that to block to the changed blocks for that mapLoc
                            break;
                        }
                    }
                }
                for (MapCleanRunnable runnables : MapCleanRunnable.getRunning()) {
                    for (BlockState blockState : runnables.getMapLoc().getChangedBlocks()) {
                        if (blockState.getLocation().equals(from.getState().getLocation())) {
                            event.setCancelled(true); // This is to prevent liquid from flowing while cleaning, which messes up the cleaning process.
                        }
                    }
                }
            }
        }
    }
}
