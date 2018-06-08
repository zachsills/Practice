package net.practice.practice.game.queue.type;

import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.game.queue.Queue;
import net.practice.practice.game.queue.QueueType;

public class RankedQueue extends Queue {

    public RankedQueue(Ladder ladder) {
        super(ladder, QueueType.RANKED);
    }

    @Override
    public void setup() {

    }
}
