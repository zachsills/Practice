package net.practice.practice.game.duel.type;

import lombok.Getter;
import net.practice.practice.game.arena.map.MapLoc;
import net.practice.practice.game.duel.Duel;
import net.practice.practice.game.duel.DuelEndReason;
import net.practice.practice.game.duel.DuelType;
import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.game.party.Party;
import net.practice.practice.game.player.Profile;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PartyDuel extends Duel {

    @Getter private Party partyOne, partyTwo;

    @Getter private Party winner;

    private int initialSize;

    public PartyDuel(MapLoc map, Ladder ladder, Party partyOne, Party partyTwo) {
        super(map, ladder, DuelType.TEAM_VS_TEAM);

        this.partyOne = partyOne;
        this.partyTwo = partyTwo;

        this.initialSize = partyOne.getSize() + partyTwo.getSize();
    }

    @Override
    public void preStart() {
        super.preStart();

        Profile.totalInGame += initialSize;
    }

    @Override
    public void start() {
        super.start();


    }

    @Override
    public Collection<Player> getPlayers() {
        return Stream.concat(partyOne.getPlayers().stream(), partyTwo.getPlayers().stream())
                .collect(Collectors.toList());
    }

    @Override
    public void end(DuelEndReason reason) {
        super.end(reason);

        Profile.totalInGame -= initialSize;
    }

    @Override
    public void sendMessage(String message) {
        super.sendMessage(message);

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
