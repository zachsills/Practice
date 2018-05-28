package net.practice.practice.game.player.data;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public enum ProfileSetting {

    DUEL_REQUESTS("duelRequests", "Toggle Duel Requests", true),
    PLAYER_TIME("playerTime", "Set Your Time of Day", "Day"),
    PUBLIC_CHAT("publicChat", "Public Chat", true),
    SCOREBOARD("showScoreboard", "Show Scoreboard", true);

    @Getter private final String key, friendlyName;
    @Getter private final Object defaultValue;

    ProfileSetting(String key, String friendlyName, Object defaultValue) {
        this.key = key;
        this.friendlyName = friendlyName;
        this.defaultValue = defaultValue;
    }

    public static ProfileSetting getByKey(final String key) {
        return Arrays.stream(values())
                .filter(type -> type.getKey().equals(key))
                .findFirst()
                .orElse(null);
    }

    private static List<String> getTimeOptions() {
        return Arrays.asList("day", "night", "dusk");
    }

    public static Object getNextOption(ProfileSetting setting, Object value) {
        switch(setting) {
            case DUEL_REQUESTS:
            case PUBLIC_CHAT:
            case SCOREBOARD:
                return !((boolean) value);
            case PLAYER_TIME: {
                String current = String.valueOf(value).toLowerCase();
                if(!getTimeOptions().contains(current))
                    return getTimeOptions().get(0);

                return getTimeOptions().get(getTimeOptions().indexOf(current) + 1);
            }
        }

        return null;
    }
}
