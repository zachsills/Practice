package net.practice.practice.game.duel.type;

import lombok.Getter;
import lombok.Setter;
import net.practice.practice.game.arena.Arena;
import net.practice.practice.game.duel.Duel;
import net.practice.practice.game.duel.DuelEndReason;
import net.practice.practice.game.duel.DuelType;
import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.game.player.Profile;
import net.practice.practice.util.chat.C;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class DuoDuel extends Duel {

    @Getter private List<Player> duoOne, duoTwo;

    @Getter @Setter private UUID winner;

    public DuoDuel(Arena arena, Ladder ladder, List<Player> duoOne, List<Player> duoTwo) {
        super(arena, ladder, DuelType.TWO_VS_TWO);

        this.duoOne = duoOne;
        this.duoTwo = duoTwo;
    }

    @Override
    public void preStart() {
        super.preStart();

        Profile.totalInGame += 4;
    }

    @Override
    public void start() {
        super.start();


    }

    @Override
    public void end(DuelEndReason reason) {

        Profile.totalInGame -= 4;
    }

    @Override
    public void kill(Player player) {

    }

    @Override
    public void quit(Player player) {

    }

    @Override
    public void sendMessage(String message) {
        super.sendMessage(message);

        duoOne.forEach(player -> player.sendMessage(C.color(message)));
        duoTwo.forEach(player -> player.sendMessage(C.color(message)));
    }

    @Override
    public boolean hasPlayer(Player player) {
        return getDuoOne().contains(player) || getDuoTwo().contains(player);
    }

    @Override
    public void saveInventories() {
        duoOne.stream()
                .filter(player -> !hasSnapshot(player))
                .forEach(this::saveInventory);

        duoTwo.stream()
                .filter(player -> !hasSnapshot(player))
                .forEach(this::saveInventory);
    }
}
