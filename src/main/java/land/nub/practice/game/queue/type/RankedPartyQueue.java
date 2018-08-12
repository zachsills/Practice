package land.nub.practice.game.queue.type;

import land.nub.practice.game.arena.map.MapLoc;
import land.nub.practice.game.ladder.Ladder;
import land.nub.practice.game.party.PartyManager;
import land.nub.practice.game.player.Profile;
import land.nub.practice.game.queue.Queue;
import land.nub.practice.game.queue.QueueRange;
import land.nub.practice.game.queue.QueueType;
import lombok.Getter;
import land.nub.practice.game.duel.Duel;
import land.nub.practice.game.duel.type.DuoDuel;
import land.nub.practice.game.party.Party;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class RankedPartyQueue extends Queue {

    @Getter
    private Map<UUID, QueueRange> ranges;

    public RankedPartyQueue(Ladder ladder) {
        super(ladder, QueueType.RANKED_TEAM);

        ranges = new HashMap<>();
    }

    @Override
    public void handleRanges() {
        Iterator<Map.Entry<UUID, QueueRange>> it = ranges.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<UUID, QueueRange> rangeEntry = it.next();
            if(!rangeEntry.getValue().isExpired())
                rangeEntry.getValue().expand();
        }
    }

    @Override
    public void setup() {
        for(UUID uuid : getQueued()) {
            for(UUID otherUUID : getQueued()) {
                if(uuid == otherUUID)
                    continue;

                if(ranges.get(uuid).isInRange(ranges.get(otherUUID))) {
                    Party partyOne = PartyManager.getByUuid(getQueued().get(0)), partyTwo = PartyManager.getByUuid(getQueued().get(1));
                    if(partyOne == null || partyTwo == null)
                        return;

                    MapLoc mapLoc = MapLoc.getRandomMap(getLadder().isBuildable(), getLadder().isSpleef());
                    if(mapLoc == null)
                        return;

                    Duel duel = new DuoDuel(mapLoc, getLadder(), partyOne.getAllPlayers(), partyTwo.getAllPlayers());
                    duel.sendMessage("&eRanked party match found: &6" + partyOne.getLeaderName() + " &evs. &6" + partyTwo.getLeaderName());
                    duel.preStart();

                    remove(uuid);
                    remove(otherUUID);
                    return;
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
