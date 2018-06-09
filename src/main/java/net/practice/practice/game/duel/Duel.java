package net.practice.practice.game.duel;

import lombok.Getter;
import lombok.Setter;
import net.practice.practice.Practice;
import net.practice.practice.game.arena.Arena;
import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.game.player.Profile;
import net.practice.practice.game.player.data.InventorySnapshot;
import net.practice.practice.util.chat.C;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public abstract class Duel {

    @Getter private Arena arena;
    @Getter private Ladder ladder;
    @Getter private DuelType type;

    @Getter private long startTime, endTime;

    @Getter @Setter private DuelState state;

    @Getter private Set<InventorySnapshot> snapshots;

    @Getter private Map<Player, Integer> missedPots;

    private BukkitRunnable countDownTask;
    @Getter private int countDown = 5;

    public Duel(Arena arena, Ladder ladder, DuelType type) {
        this.arena = arena;
        this.ladder = ladder;
        this.type = type;

        this.snapshots = new HashSet<>();

        this.missedPots = new HashMap<>();

        this.state = DuelState.STARTING;
    }

    public Duel(Ladder ladder, DuelType type) {
        this(null, ladder, type);
    }

    public void preStart() {
        setState(DuelState.STARTING);

        countDownTask = new BukkitRunnable() {
            @Override
            public void run() {
                if(countDown > 0) {
                    sendMessage(C.color("&7Duel starting in &e" + countDown + " &e" + (countDown == 1 ? "second" : "seconds") + "&7."));
                    countDown--;
                } else {
                    start();
                    this.cancel();
                }
            }
        };
        countDownTask.runTaskTimer(Practice.getInstance(), 0, 20L);
    }

    public void start() {
        this.startTime = System.currentTimeMillis();
        setState(DuelState.PLAYING);

        sendMessage(C.color("&aThe match has now started!"));
    }

    public void giveKits(Player player) {
        Profile profile = Profile.getByPlayer(player);

        if(!profile.hasCustomKits(ladder)) {
            if(ladder.getDefaultInv() != null)
                ladder.getDefaultInv().apply(player);

            player.sendMessage(C.color("&eYou were given the default " + ladder.getDisplayName() + " &ekit."));
        } else {
            // TODO: Give custom kit selection
        }
    }

    public void end(DuelEndReason reason) {
        endTime = System.currentTimeMillis();

        if(countDownTask != null)
            countDownTask.cancel();

        setState(DuelState.ENDED);

        saveInventories();
    }

    public boolean hasSnapshot(Player player) {
        return snapshots.stream()
                .anyMatch(snapshot -> snapshot.getName().equals(player.getName()));
    }

    public InventorySnapshot getSnapshot(Player player) {
        return snapshots.stream()
                .filter(snapshot -> snapshot.getName().equals(player.getName()))
                .findFirst()
                .orElse(null);
    }

    public abstract void sendMessage(String message);

    public abstract void kill(Player player);

    public abstract void quit(Player player);

    public abstract boolean hasPlayer(Player player);

    public void saveInventory(Player player) {
        snapshots.add(new InventorySnapshot(player));
    }

    public void saveInventory(UUID uuid) {
        saveInventory(Bukkit.getPlayer(uuid));
    }

    public abstract void saveInventories();
}
