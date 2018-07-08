package net.practice.practice.game.queue.type;

import net.practice.practice.game.arena.map.MapLoc;
import net.practice.practice.game.duel.Duel;
import net.practice.practice.game.duel.type.DuoDuel;
import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.game.party.Party;
import net.practice.practice.game.party.PartyManager;
import net.practice.practice.game.queue.Queue;
import net.practice.practice.game.queue.QueueType;

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
    }
}
