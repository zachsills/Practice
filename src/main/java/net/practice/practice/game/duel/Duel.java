package net.practice.practice.game.duel;

import lombok.Getter;
import lombok.Setter;
import net.practice.practice.game.arena.Arena;
import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.game.player.data.InventorySnapshot;
import org.bukkit.Bukkit;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class Duel {

    @Getter private Arena arena;
    @Getter private Ladder ladder;
    @Getter private DuelType type;

    @Getter private long startTime, timeUntilStart, endTime;

    @Getter @Setter private DuelState state;

    @Getter private Set<InventorySnapshot> snapshots;

    public Duel(Arena arena, Ladder ladder, DuelType type) {
        this.arena = arena;
        this.ladder = ladder;
        this.type = type;

        this.snapshots = new HashSet<>();

        this.state = DuelState.STARTING;
    }

    public Duel(Ladder ladder, DuelType type) {
        this(null, ladder, type);
    }

    public void preStart() {
        this.timeUntilStart = System.currentTimeMillis() + 5000;
        setState(DuelState.STARTING);
    }

    public void start() {
        this.startTime = System.currentTimeMillis();
        setState(DuelState.PLAYING);
    }

    public abstract void end(DuelEndReason reason);

    public void end() {
        end(DuelEndReason.DIED);
    }

    public void saveInventory(UUID uuid) {
        snapshots.add(new InventorySnapshot(Bukkit.getPlayer(uuid)));
    }
}
