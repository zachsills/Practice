package net.practice.practice.game.queue;

import org.bukkit.scheduler.BukkitRunnable;

public class QueueRunnable extends BukkitRunnable {

    @Override
    public void run() {
        for(Queue queue : Queue.getQueues()) {
            queue.handleRanges();
            if(queue.getSize() < 2)
                continue;

            queue.setup();
        }
    }
}
