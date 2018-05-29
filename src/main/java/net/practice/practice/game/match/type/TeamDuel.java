package net.practice.practice.game.match.type;

import lombok.Getter;
import net.practice.practice.game.arena.Arena;
import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.game.match.Duel;
import net.practice.practice.game.match.DuelEndReason;
import net.practice.practice.game.match.DuelType;
import net.practice.practice.game.team.Team;

public class TeamDuel extends Duel {

    @Getter private Team teamOne, teamTwo;

    @Getter private Team winner;

    public TeamDuel(Arena arena, Ladder ladder, Team teamOne, Team teamTwo) {
        super(arena, ladder, DuelType.TEAM_VS_TEAM);

        this.teamOne = teamOne;
        this.teamTwo = teamTwo;
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
