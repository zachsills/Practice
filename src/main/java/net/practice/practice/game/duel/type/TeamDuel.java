package net.practice.practice.game.duel.type;

import lombok.Getter;
import net.practice.practice.game.arena.Arena;
import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.game.duel.Duel;
import net.practice.practice.game.duel.DuelEndReason;
import net.practice.practice.game.duel.DuelType;
import net.practice.practice.game.party.Party;
import org.bukkit.entity.Player;

public class TeamDuel extends Duel {

    @Getter private Party partyOne, partyTwo;

    @Getter private Party winner;

    public TeamDuel(Arena arena, Ladder ladder, Party partyOne, Party partyTwo) {
        super(arena, ladder, DuelType.TEAM_VS_TEAM);

        this.partyOne = partyOne;
        this.partyTwo = partyTwo;
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
        partyOne.sendMessage(message);
        partyTwo.sendMessage(message);
    }

    @Override
    public void kill(Player player) {

    }

    @Override
    public void quit(Player player) {

    }

    @Override
    public boolean hasPlayer(Player player) {
        return partyOne.contains(player) || partyTwo.contains(player);
    }

    @Override
    public void saveInventories() {
        partyOne.getPlayers().stream()
                .filter(player -> !hasSnapshot(player))
                .forEach(this::saveInventory);

        partyTwo.getPlayers().stream()
                .filter(player -> !hasSnapshot(player))
                .forEach(this::saveInventory);
    }
}
