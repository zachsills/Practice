package net.practice.practice.board.provider;

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
import java.util.stream.Collectors;

public class ProviderResolver implements BoardProvider {

    private static final String BREAKER = C.color("&f&m------------------");

    private Map<ProfileState, BoardProvider> providers;
    private BoardProvider lobbyProvider;

    public ProviderResolver() {
        providers = new HashMap<>();

        providers.put(ProfileState.PLAYING, new PlayingProvider());
        providers.put(ProfileState.SPECTATING, new SpectatingProvider());

        lobbyProvider = new LobbyProvider();
        providers.put(ProfileState.LOBBY, lobbyProvider);
        providers.put(ProfileState.BUILDING, lobbyProvider);
        providers.put(ProfileState.EDITING, lobbyProvider);
        providers.put(ProfileState.QUEUING, lobbyProvider);
    }

    @Override
    public String getTitle() {
        return C.color(ConfigValues.SCOREBOARD_TITLE);
    }

    @Override
    public List<String> getLines(Player player) {
        Profile profile = Profile.getByPlayer(player);
        if(!((boolean) profile.getSetting(ProfileSetting.SCOREBOARD)))
            return Collections.emptyList();

        BoardProvider provider = providers.getOrDefault(profile.getProfileState(), lobbyProvider);
        List<String> lines = provider.getLines(player);
        if(!lines.isEmpty()) {
            lines.add(0, BREAKER);
            lines.add(BREAKER);
        }

        return lines.stream()
                .map(C::color)
                .collect(Collectors.toList());
    }
}
