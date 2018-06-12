package net.practice.practice.game.party;

import lombok.Getter;
import net.practice.practice.game.player.Profile;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PartyManager {

    @Getter private static final Map<UUID, Party> teams = new HashMap<>();

    public static boolean isInTeam(Player player) {
        Profile profile = Profile.getByPlayer(player);

        return profile.getParty() != null && teams.values().contains(profile.getParty());
    }
}
