package net.practice.practice.game.duel.type;

import lombok.Getter;
import lombok.Setter;
import net.practice.practice.Practice;
import net.practice.practice.game.arena.Arena;
import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.game.duel.Duel;
import net.practice.practice.game.duel.DuelEndReason;
import net.practice.practice.game.duel.DuelType;
import net.practice.practice.game.player.Profile;
import net.practice.practice.util.chat.C;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class SoloDuel extends Duel {

    @Getter private final Player playerOne, playerTwo;

    @Getter @Setter private UUID winner;

    private BukkitRunnable countDownTask;
    private int countDown = 5;

    public SoloDuel(Arena arena, Ladder ladder, Player playerOne, Player playerTwo) {
        super(arena, ladder, DuelType.ONE_VS_ONE);

        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
    }

    @Override
    public void preStart() {
        super.preStart();

        playerOne.teleport(getArena().getPosOne());
        playerTwo.teleport(getArena().getPosTwo());

        Profile profileOne = Profile.getByPlayer(playerOne);
        Profile profileTwo = Profile.getByPlayer(playerTwo);
        profileOne.setCurrentDuel(this);
        profileTwo.setCurrentDuel(this);

        countDownTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (countDown > 0) {
                    playerOne.sendMessage(C.color("&7Duel starting in &e" + countDown));
                    countDown--;
                } else {
                    start();
                    this.cancel();
                }
            }
        };
        countDownTask.runTaskTimer(Practice.getInstance(), 0, 20L);
    }

    @Override
    public void start() {
        super.start();


    }

    @Override
    public void end(DuelEndReason reason) {
        if (countDownTask != null) {
            countDownTask.cancel();
        }
    }
}
