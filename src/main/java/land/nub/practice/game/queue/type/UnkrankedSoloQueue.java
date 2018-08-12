package land.nub.practice.game.queue.type;

import land.nub.practice.util.chat.C;
import land.nub.practice.game.arena.map.MapLoc;
import land.nub.practice.game.duel.Duel;
import land.nub.practice.game.duel.type.SoloDuel;
import land.nub.practice.game.ladder.Ladder;
import land.nub.practice.game.player.Profile;
import land.nub.practice.game.queue.Queue;
import land.nub.practice.game.queue.QueueType;
import land.nub.practice.spawn.SpawnHandler;
import org.bukkit.Bukkit;

public class UnkrankedSoloQueue extends Queue {

    public UnkrankedSoloQueue(Ladder ladder) {
        super(ladder, QueueType.UNRANKED);
    }

    @Override
    public void setup() {
        //Arena oldarena = Arena.getRandomArena(getLadder());
        if(Bukkit.getPlayer(getQueued().get(0)) != null && Bukkit.getPlayer(getQueued().get(1)) != null) {
            Profile profileOne = Profile.getByUuid(getQueued().get(0)), profileTwo = Profile.getByUuid(getQueued().get(1));
            profileOne.leaveQueue(false);
            profileTwo.leaveQueue(false);

            //Duel duel = new SoloDuel(oldarena, getLadder(), profileOne.getPlayer(), profileTwo.getPlayer());
            MapLoc map = MapLoc.getRandomMap(getLadder().isBuildable(), getLadder().isSpleef());
            if (map != null) {
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
