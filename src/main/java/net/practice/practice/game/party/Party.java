package net.practice.practice.game.party;

import lombok.Getter;
import lombok.Setter;
import net.practice.practice.util.chat.C;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Party {

    @Getter @Setter private UUID leader;

    @Getter private Set<Player> players;

    public Party(UUID leader) {
        this.leader = leader;

        this.players = new HashSet<>();
    }

    public void sendMessage(String message) {
        players.forEach(player -> player.sendMessage(C.color(message)));
    }

    public boolean contains(Player player) {
        for(Player ply : players)
            if(ply == player || player.getUniqueId().toString().equalsIgnoreCase(ply.getUniqueId().toString()))
                return true;

        return false;
    }
}
