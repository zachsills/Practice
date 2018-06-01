package net.practice.practice.game.duel.type;

import lombok.Getter;
import lombok.Setter;
import net.practice.practice.game.arena.Arena;
import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.game.duel.Duel;
import net.practice.practice.game.duel.DuelEndReason;
import net.practice.practice.game.duel.DuelType;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SoloDuel extends Duel {

    @Getter private final Player playerOne, playerTwo;

    @Getter @Setter private UUID winner;

    public SoloDuel(Arena arena, Ladder ladder, Player playerOne, Player playerTwo) {
        super(arena, ladder, DuelType.ONE_VS_ONE);

        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
    }

    @Override
    public void preStart() {
        super.preStart();


    }

    @Override
    public void start() {
        super.start();


    }

    @Override
    public void end(DuelEndReason reason) {

    }
}
