package net.practice.practice.game.queue;

public class QueueRange {

    private int middle;
    private int min;
    private int max;

    public QueueRange(int middle) {
        this.middle = middle;
        this.min = middle - 10;
        this.max = middle + 10;
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

    public void expand() {
        min -= 10;
        max += 10;
    }

    public boolean isInRange(int i, boolean inclusive) {
        return inclusive ? (i <= max && i >= min) : (i < max && i < min);
    }

    public boolean isInRange(int i) {
        return isInRange(i, true);
    }
}