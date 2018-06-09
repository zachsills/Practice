package net.practice.practice.game.duel.type;

import lombok.Getter;
import net.practice.practice.game.arena.Arena;
import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.game.duel.Duel;
import net.practice.practice.game.duel.DuelEndReason;
import net.practice.practice.game.duel.DuelType;
import net.practice.practice.game.team.Team;
import org.bukkit.entity.Player;

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
        super.end(reason);
    }

    @Override
    public void sendMessage(String message) {
        teamOne.sendMessage(message);
        teamTwo.sendMessage(message);
    }

    @Override
    public void kill(Player player) {

    }

    @Override
    public void quit(Player player) {

    }

    @Override
    public boolean hasPlayer(Player player) {
        return teamOne.contains(player) || teamTwo.contains(player);
    }

    @Override
    public void saveInventories() {
        teamOne.getPlayers().stream()
                .filter(player -> !hasSnapshot(player))
                .forEach(this::saveInventory);

        teamTwo.getPlayers().stream()
                .filter(player -> !hasSnapshot(player))
                .forEach(this::saveInventory);
    }
}
