package net.practice.practice.game.player.data;

import lombok.Getter;
import net.practice.practice.util.chat.C;
import net.practice.practice.util.itemstack.I;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ProfileSetting {

    DUEL_REQUESTS("duelRequests", "toggle duel requests", true, Material.DIAMOND_SWORD),
    PLAYER_TIME("playerTime", "set your time of day", "Day", Material.WATCH),
    PUBLIC_CHAT("publicChat", "see public chat", true, Material.PAPER),
    SCOREBOARD("showScoreboard", "show scoreboard", true, Material.ITEM_FRAME);

    @Getter private final String key, friendlyName;
    @Getter private final Object defaultValue;
    @Getter private final Material material;

    ProfileSetting(String key, String friendlyName, Object defaultValue, Material material) {
        this.key = key;
        this.friendlyName = friendlyName;
        this.defaultValue = defaultValue;
        this.material = material;
    }

    public static ProfileSetting getByKey(final String key) {
        return Arrays.stream(values())
                .filter(type -> type.getKey().equals(key))
                .findFirst()
                .orElse(null);
    }

    public static ProfileSetting getByMaterial(Material material) {
        return Arrays.stream(values())
                .filter(type -> type.getMaterial() == material)
                .findFirst()
                .orElse(null);
    }

    private static List<String> getTimeOptions() {
        return Arrays.asList("day", "night", "sunrise");
    }

    public static Object getNextOption(ProfileSetting setting, Object value) {
        switch(setting) {
            case DUEL_REQUESTS:
            case PUBLIC_CHAT:
            case SCOREBOARD:
                return !((boolean) value);
            case PLAYER_TIME: {
                String current = String.valueOf(value).toLowerCase();
                List<String> options = getTimeOptions();
                if(!options.contains(current))
                    return options.get(0);

                int index = options.indexOf(current);

                return index + 1 >= options.size() ? options.get(0) : options.get(index + 1);
            }
        }

        return null;
    }

    public static ItemStack getSettingItem(ProfileSetting setting, Object value) {
        String verboseName = WordUtils.capitalizeFully(setting.name().replace("_", " "));

        switch(setting) {
            case PLAYER_TIME: {
                List<String> lore = Stream.of(
                        " ",
                        "&4Do you want to",
                        "&4" + setting.getFriendlyName() + "?",
                        " "
                        ).collect(Collectors.toList());

                for(String option : getTimeOptions()) {
                    if(option.equals(String.valueOf(value).toLowerCase()))
                        lore.add("&4» &c" + WordUtils.capitalizeFully(option));
                    else
                        lore.add("&7" + WordUtils.capitalizeFully(option));
                }
                lore.add(" ");

                I item = new I(Material.WATCH)
                        .name(C.color("&b" + WordUtils.capitalizeFully(setting.getFriendlyName())))
                        .clearLore();

                for(String string : lore)
                    item.lore(C.color(string));

                return item;
            }
            default: {
                List<String> lore = Stream.of(
                                        " ",
                                        "&4Do you want to",
                                        "&4" + setting.getFriendlyName() + "?",
                                        " ",
                                        "  " + ((boolean) value ? "&4» &c" : " &7") + "Show " + verboseName,
                                        "  " + (!(boolean) value ? "&4» &c" : " &7") + "Hide " + verboseName,
                                        " "
                                        ).map(C::color).collect(Collectors.toList());

                I item = new I(setting.getMaterial())
                        .name(C.color("&b" + WordUtils.capitalizeFully(setting.getFriendlyName())))
                        .clearLore();

                lore.forEach(item::lore);

                return item;
            }
        }
    }

    public static void toggleFor(Player player, ProfileSetting setting, Object value) {
        switch(setting) {
            case PLAYER_TIME: {
                String option = String.valueOf(value).toLowerCase();
                if(option.equals("day"))
                    player.setPlayerTime(6000L, false);
                else if(option.equals("sunrise"))
                    player.setPlayerTime(23000L, false);
                else
                    player.setPlayerTime(18000L, false);
                break;
            }
        }
    }
}
