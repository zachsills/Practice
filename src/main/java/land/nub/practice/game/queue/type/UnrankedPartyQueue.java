package land.nub.practice.game.queue.type;

import land.nub.practice.game.arena.map.MapLoc;
import land.nub.practice.game.duel.Duel;
import land.nub.practice.game.duel.type.DuoDuel;
import land.nub.practice.game.ladder.Ladder;
import land.nub.practice.game.party.Party;
import land.nub.practice.game.party.PartyManager;
import land.nub.practice.game.player.Profile;
import land.nub.practice.game.queue.Queue;
import land.nub.practice.game.queue.QueueType;

public class UnrankedPartyQueue extends Queue {

    public UnrankedPartyQueue(Ladder ladder) {
        super(ladder, QueueType.UNRANKED_TEAM);
    }

    @Override
    public void setup() {
        Party partyOne = PartyManager.getByUuid(getQueued().get(0)), partyTwo = PartyManager.getByUuid(getQueued().get(1));
        if(partyOne == null || partyTwo == null)
            return;

        MapLoc mapLoc = MapLoc.getRandomMap(getLadder().isBuildable(), getLadder().isSpleef());
        if(mapLoc == null)
            return;

//        if(partyOne.getSize() == 2 && partyTwo.getSize() == 2)
//            duel = new DuoDuel(mapLoc, getLadder(), partyOne.getAllPlayers(), partyTwo.getAllPlayers());
//        else
//            duel = new PartyDuel(mapLoc, getLadder(), partyOne, partyTwo);

        Duel duel = new DuoDuel(mapLoc, getLadder(), partyOne.getAllPlayers(), partyTwo.getAllPlayers());
        duel.sendMessage("&eUnranked party match found: &6" + partyOne.getLeaderName() + " &evs. &6" + partyTwo.getLeaderName());
        duel.preStart();

        getQueued().remove(0);
        getQueued().remove(1);

        Profile.totalQueueing -= 2;
    }
}
