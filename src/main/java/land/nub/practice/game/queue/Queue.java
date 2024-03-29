package land.nub.practice.game.queue;

import lombok.Getter;
import land.nub.practice.game.duel.DuelType;
import land.nub.practice.game.duel.type.DuoDuel;
import land.nub.practice.game.duel.type.SoloDuel;
import land.nub.practice.game.ladder.Ladder;
import land.nub.practice.game.party.Party;
import land.nub.practice.game.party.PartyManager;
import land.nub.practice.game.player.Profile;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Queue {

    @Getter private static Set<Queue> queues = new HashSet<>();

    @Getter private Ladder ladder;

    @Getter private QueueType type;

    @Getter private List<UUID> queued;

    public Queue(Ladder ladder, QueueType type) {
        this.ladder = ladder;
        this.type = type;

        this.queued = new ArrayList<>();

        queues.add(this);
    }

    public static Queue getQueue(Ladder ladder) {
        return queues.stream()
                .filter(queue -> queue.getLadder() == ladder)
                .findFirst()
                .orElse(null);
    }

    public static int getNumberQueuing(Ladder ladder) {
        int total = 0;

        for(int i : Arrays.stream(ladder.getQueues()).filter(Objects::nonNull).map(Queue::getSize).collect(Collectors.toList()))
            total += i;

        return total;
    }

    public static int getNumberInGame(Ladder ladder, boolean ranked) {
        int total = 0;

        if(ranked) {
            for(Profile profile : Profile.getProfiles().values().stream().filter(Profile::isInGame).filter(profile -> profile.getCurrentDuel().getType() == DuelType.ONE_VS_ONE && ((SoloDuel) profile.getCurrentDuel()).isRanked()).collect(Collectors.toList()))
                total += profile.getCurrentDuel().getLadder() == ladder ? 1 : 0;
        } else {
            for(Profile profile : Profile.getProfiles().values().stream().filter(Profile::isInGame).collect(Collectors.toList()))
                total += profile.getCurrentDuel().getLadder() == ladder ? 1 : 0;
        }

        return total;
    }
    public static int getPartiesInGame(Ladder ladder, boolean ranked) {
        int total = 0;

        if(ranked) {
            for(Party party : PartyManager.getParties().values())
                total += party.getCurrentDuel() != null
                        && party.getCurrentDuel().getLadder() == ladder
                        && party.getCurrentDuel() instanceof DuoDuel
                        && ((DuoDuel) party.getCurrentDuel()).isRanked() ? party.getSize() : 0;
        } else {
            for(Party party : PartyManager.getParties().values())
                total += party.getCurrentDuel() != null && party.getCurrentDuel().getLadder() == ladder ? party.getSize() : 0;
        }

        return total;
    }


    public void add(UUID uuid) {
        queued.add(uuid);

        Profile.totalQueueing++;
    }

    public void remove(UUID uuid) {
        queued.remove(uuid);

        Profile.totalQueueing--;
    }

    public int getSize() {
        return queued.size();
    }

    public void handleRanges() { }

    public abstract void setup();
}
