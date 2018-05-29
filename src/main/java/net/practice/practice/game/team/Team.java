package net.practice.practice.game.team;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Team {

    @Getter @Setter private UUID leader;

    @Getter private Set<Player> players;

    public Team(UUID leader) {
        this.leader = leader;

        this.players = new HashSet<>();
    }
}
