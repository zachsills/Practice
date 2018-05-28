package net.practice.practice.board.provilder;

import net.practice.practice.board.BoardProvider;
import net.practice.practice.game.player.Profile;
import net.practice.practice.game.player.data.ProfileSetting;
import net.practice.practice.game.player.data.ProfileState;
import net.practice.practice.util.ConfigValues;
import net.practice.practice.util.chat.C;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProviderResolver implements BoardProvider {

    private Map<ProfileState, BoardProvider> providers;

    public ProviderResolver() {
        providers = new HashMap<>();

        providers.put(ProfileState.PLAYING, new PlayingProvider());
    }

    @Override
    public String getTitle() {
        return C.color(ConfigValues.SCOREBOARD_TITLE);
    }

    @Override
    public List<String> getLines(Player player) {
        Profile profile = Profile.getByPlayer(player);
        if((boolean) profile.getSetting(ProfileSetting.SCOREBOARD))
            return Collections.emptyList();

        BoardProvider provider = providers.get(profile.getProfileState());
        return provider != null ? provider.getLines(player) : Collections.emptyList();
    }
}
