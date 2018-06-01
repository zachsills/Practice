package net.practice.practice.game.duel;

import lombok.Getter;
import lombok.Setter;
import net.practice.practice.game.arena.Arena;
import net.practice.practice.game.ladder.Ladder;

public abstract class Duel {

    @Getter private Arena arena;
    @Getter private Ladder ladder;
    @Getter private DuelType type;

    @Getter private long startTime, timeUntilStart;

    @Getter @Setter private DuelState state;

    public Duel(Arena arena, Ladder ladder, DuelType type) {
        this.arena = arena;
        this.ladder = ladder;
        this.type = type;

        this.state = DuelState.STARTING;
    }

    public Duel(Ladder ladder, DuelType type) {
        this(null, ladder, type);
    }

    public void preStart() {
        this.timeUntilStart = System.currentTimeMillis() + 5000;
    }

    public void start() {
        this.startTime = System.currentTimeMillis();
    }

    public abstract void end(DuelEndReason reason);

    public void end() {
        end(DuelEndReason.DIED);
    }
}
