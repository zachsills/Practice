package net.practice.practice.game.team;

import lombok.Getter;
import net.practice.practice.game.player.Profile;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeamManager {

    @Getter private static final Map<UUID, Team> teams = new HashMap<>();

    public static boolean isInTeam(Player player) {
        Profile profile = Profile.getByPlayer(player);

        return profile.getTeam() != null && teams.values().contains(profile.getTeam());
    }
}
