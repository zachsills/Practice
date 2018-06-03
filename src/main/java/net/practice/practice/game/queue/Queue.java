package net.practice.practice.game.queue;

import lombok.Getter;
import net.practice.practice.game.duel.DuelType;
import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.util.TimeUtils;

public class Queue {

    @Getter public Ladder ladder;
    @Getter public QueueRange queueRange;
    @Getter private long startTime;

    public Queue(Ladder ladder, int elo) {
        this.ladder = ladder;
        this.queueRange = new QueueRange(elo, 5);
        this.startTime = System.currentTimeMillis();
    }

    public boolean canQueueWith(Queue other) {
        if (!getLadder().getName().equals(other.getLadder().getName()))
            return false;
        if (!getLadder().getDuelType().equals(other.getLadder().getDuelType()))
            return false;
        if (!getQueueRange().isInRange(other.getQueueRange().getMiddle()))
            return false;
        if (getLadder().isBuildable() != other.getLadder().isBuildable())
            return false;
        if (getLadder().isEditable() != other.getLadder().isEditable())
            return false;
        if (getLadder().isCombo() != other.getLadder().isCombo())
            return false;
        if (getLadder().isRanked() != other.getLadder().isRanked())
            return false;
        return true;
    }

    public String getTimeQueuingFormatted() {
        return TimeUtils.msToMMSS(Math.abs(System.currentTimeMillis() - startTime));
    }
}
