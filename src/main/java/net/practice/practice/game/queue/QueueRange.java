package net.practice.practice.game.queue;

public class QueueRange {

    private int middle;
    private int min;
    private int max;

    public QueueRange(int middle, int expand) {
        this.middle = middle;
        this.min = middle - expand;
        this.max = middle + expand;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getMiddle() {
        return middle;
    }

    public boolean isInRange(int i, boolean inclusive) {
        return inclusive ? (i <= max && i >= min) : (i < max && i < min);
    }

    public boolean isInRange(int i) {
        return isInRange(i, true);
    }
}