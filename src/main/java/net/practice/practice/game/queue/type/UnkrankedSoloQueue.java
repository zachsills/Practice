package net.practice.practice.game.queue.type;

import net.practice.practice.game.arena.ArenaType;
import net.practice.practice.game.arenatest.Arena;
import net.practice.practice.game.arenatest.map.MapLoc;
import net.practice.practice.game.duel.Duel;
import net.practice.practice.game.duel.type.SoloDuel;
import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.game.player.Profile;
import net.practice.practice.game.queue.Queue;
import net.practice.practice.game.queue.QueueType;
import net.practice.practice.spawn.SpawnHandler;
import net.practice.practice.util.chat.C;
import org.bukkit.Bukkit;

public class UnkrankedSoloQueue extends Queue {

    public UnkrankedSoloQueue(Ladder ladder) {
        super(ladder, QueueType.UNRANKED);
    }

    @Override
    public void setup() {
        //Arena arena = Arena.getRandomArena(getLadder());
        if(Bukkit.getPlayer(getQueued().get(0)) != null && Bukkit.getPlayer(getQueued().get(1)) != null) {
            Profile profileOne = Profile.getByUuid(getQueued().get(0)), profileTwo = Profile.getByUuid(getQueued().get(1));
            profileOne.leaveQueue(false);
            profileTwo.leaveQueue(false);

            //Duel duel = new SoloDuel(arena, getLadder(), profileOne.getPlayer(), profileTwo.getPlayer());
            MapLoc map = MapLoc.getRandomMap(getLadder().isBuildable());
            if (map != null) {
                if(getLadder().isBuildable())
                    map.setArena(getLadder().isSpleef() ? Arena.getArenaByType(ArenaType.SPLEEF) : Arena.getArenaByType(ArenaType.SINGLE));
                else
                    map.setArena(Arena.getArenaByType(ArenaType.MULTI));
                Duel duel = new SoloDuel(map, getLadder(), profileOne.getPlayer(), profileTwo.getPlayer());
                duel.sendMessage(C.color("&eUnranked match found: &6" + profileOne.getPlayer().getName() + " &evs. &6" + profileTwo.getPlayer().getName()));
                duel.preStart();
            } else {
                profileOne.getPlayer().sendMessage("No open arenas available!");
                profileTwo.getPlayer().sendMessage("No open arenas available!");
                SpawnHandler.spawn(profileOne.getPlayer());
                SpawnHandler.spawn(profileTwo.getPlayer());
            }
        }
    }
}
