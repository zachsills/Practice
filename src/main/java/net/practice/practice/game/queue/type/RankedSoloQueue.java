package net.practice.practice.game.queue.type;

import lombok.Getter;
import net.practice.practice.Practice;
import net.practice.practice.game.arena.map.MapLoc;
import net.practice.practice.game.duel.Duel;
import net.practice.practice.game.duel.type.SoloDuel;
import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.game.player.Profile;
import net.practice.practice.game.queue.Queue;
import net.practice.practice.game.queue.QueueRange;
import net.practice.practice.game.queue.QueueType;
import net.practice.practice.spawn.SpawnHandler;
import net.practice.practice.util.chat.C;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class RankedSoloQueue extends Queue {

    @Getter private Map<UUID, QueueRange> ranges;

    public RankedSoloQueue(Ladder ladder) {
        super(ladder, QueueType.RANKED);

        ranges = new HashMap<>();
    }

    @Override
    public void handleRanges() {
        Iterator<Map.Entry<UUID, QueueRange>> it = ranges.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<UUID, QueueRange> rangeEntry = it.next();
            if(!rangeEntry.getValue().isExpired())
                rangeEntry.getValue().expand();

//            Profile profile = Profile.getByUuid(rangeEntry.getKey());
//            profile.leaveQueue(true);
//
//            profile.getPlayer().sendMessage(C.color("&cYou have been removed from the queue since you're entered out of range."));
        }
    }

    @Override
    public void setup() {
        for(UUID uuid : getQueued()) {
            for(UUID otherUUID : getQueued()) {
                if(uuid == otherUUID)
                    continue;

                if(ranges.get(uuid).isInRange(ranges.get(otherUUID).getMiddle())) {
                    //Arena oldarena = Arena.getRandomArena(getLadder());
                    if(Bukkit.getPlayer(uuid) != null && Bukkit.getPlayer(otherUUID) != null) {
                        Profile profileOne = Profile.getByUuid(uuid), profileTwo = Profile.getByUuid(otherUUID);
                        profileOne.leaveQueue(false);
                        profileTwo.leaveQueue(false);

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                int eloOne = profileOne.getElo(getLadder()), eloTwo = profileTwo.getElo(getLadder());

                                //Duel duel = new SoloDuel(oldarena, getLadder(), profileOne.getPlayer(), profileTwo.getPlayer(), true);
                                MapLoc map = MapLoc.getRandomMap(getLadder().isBuildable(), getLadder().isSpleef());
                                if (map != null) {
                                    Duel duel = new SoloDuel(map, getLadder(), profileOne.getPlayer(), profileTwo.getPlayer(), true);
                                    duel.sendMessage(C.color("&eRanked match found: &6" + profileOne.getPlayer().getName() + " [" + eloOne + "]" + " &evs. &6" + profileTwo.getPlayer().getName() + " [" + eloTwo + "]"));
                                    duel.preStart();
                                } else {
                                    profileOne.getPlayer().sendMessage("No open arenas available!");
                                    profileTwo.getPlayer().sendMessage("No open arenas available!");
                                    SpawnHandler.spawn(profileOne.getPlayer());
                                    SpawnHandler.spawn(profileTwo.getPlayer());
                                }

                            }
                        }.runTaskLater(Practice.getInstance(), 10L);
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void add(UUID uuid) {
        super.add(uuid);

        int elo = Profile.getByUuid(uuid).getElo(getLadder());
        ranges.put(uuid, new QueueRange(elo));
    }

    @Override
    public void remove(UUID uuid) {
        super.remove(uuid);

        ranges.remove(uuid);
    }
}
