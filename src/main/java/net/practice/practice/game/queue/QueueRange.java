package net.practice.practice.game.queue;

import lombok.Getter;

public class QueueRange {

    @Getter private int middle, min, max;

    public QueueRange(int middle) {
        this.middle = middle;
        this.min = middle - 10;
        this.max = middle + 10;
    }

    public void expand() {
        min -= 10;
        max += 10;
    }

    public boolean isExpired() {
        return max - middle >= 500 && middle - min >=  500;
    }

    public boolean isInRange(int i, boolean inclusive) {
        return inclusive ? (i <= max && i >= min) : (i < max && i < min);
    }

    public boolean isInRange(int i) {
        return isInRange(i, true);
    }

    public boolean isInRange(QueueRange range) {
        return isInRange(range.getMiddle(), true);
    }
}