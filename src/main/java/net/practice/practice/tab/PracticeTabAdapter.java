package net.practice.practice.tab;

import com.bizarrealex.azazel.tab.TabAdapter;
import com.bizarrealex.azazel.tab.TabTemplate;
import net.practice.practice.game.player.Profile;
import net.practice.practice.game.player.data.ProfileState;
import net.practice.practice.util.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PracticeTabAdapter implements TabAdapter {

    @Override
    public TabTemplate getTemplate(Player player, int protocolVersion) {
        TabTemplate template = new TabTemplate();

        if (protocolVersion >= 47) { // 1.8+

            // Header
            template.middle(0, " &6&lNub Land &7&l| &f&lUS");
            template.left(0, "&f&m---------------");
            template.right(0, "&f&m---------------");
            template.farRight(0, "&f&m---------------");

            Profile profile = Profile.getByPlayer(player);
            if (profile.getState() == ProfileState.LOBBY) {

                int online = Bukkit.getOnlinePlayers().size();
                long inGame = Profile.getTotalInGame();
                long inQueue = Profile.getTotalQueueing();
                int ping = PlayerUtils.getPing(player);

                String headColor = "&6", infoColor = "&f";

                // Left
                template.left(2, headColor + "Online:");
                template.left(3, infoColor + online);
                template.left(5, headColor + "In Game:");
                template.left(6, infoColor + inGame);
                template.left(8, headColor + "In Queue:");
                template.left(9, infoColor + inQueue);

                // Middle
                template.middle(2, headColor + "Player:");
                template.middle(3, infoColor + player.getName());
                template.middle(5, headColor + "Rank:");
                template.middle(6, infoColor + "ur rank xd");
                template.middle(8, headColor + "Ping:");
                template.middle(9, infoColor + ping + "ms");

                // Right
                template.right(2, headColor + "IP:");
                template.right(3, infoColor + "nub.land");
                template.right(5, headColor + "Website:");
                template.right(6, infoColor + "www.nub.land");
                template.right(8, headColor + "Donate:");
                template.right(9, infoColor + "donate.nub.land");

                // Far right
                template.farRight(8, "&7Well hello there,");
                template.farRight(9, "&71.8+ user!");
            }

            // Footer
            template.left(19, "&f&m---------------");
            template.middle(19, "&f&m---------------");
            template.right(19, "&f&m---------------");
            template.farRight(19, "&f&m---------------");

        } else { // 1.7

            // Header
            template.middle(0, "  &6&lNub Land &7&l| &f&lUS");
            template.left(0, "&f&m--------------");
            template.right(0, "&f&m--------------");

            Profile profile = Profile.getByPlayer(player);
            if (profile.getState() == ProfileState.LOBBY) {

                int online = Bukkit.getOnlinePlayers().size();
                long inGame = Profile.getTotalInGame();
                long inQueue = Profile.getTotalQueueing();
                int ping = PlayerUtils.getPing(player);

                String headColor = "&6", infoColor = "&f";

                // Left
                template.left(2, headColor + "Online:");
                template.left(3, infoColor + online);
                template.left(5, headColor + "In Game:");
                template.left(6, infoColor + inGame);
                template.left(8, headColor + "In Queue:");
                template.left(9, infoColor + inQueue);

                // Middle
                template.middle(2, headColor + "Player:");
                template.middle(3, infoColor + player.getName());
                template.middle(5, headColor + "Rank:");
                template.middle(6, infoColor + "ur rank xd");
                template.middle(8, headColor + "Ping:");
                template.middle(9, infoColor + ping + "ms");

                // Right
                template.right(2, headColor + "IP:");
                template.right(3, infoColor + "nub.land");
                template.right(5, headColor + "Website:");
                template.right(6, infoColor + "www.nub.land");
                template.right(8, headColor + "Donate:");
                template.right(9, infoColor + "donate.nub.land");

            }

            // Footer
            template.left(19, "&f&m--------------");
            template.middle(19, "&f&m--------------");
            template.right(19, "&f&m--------------");
        }

        return template;
    }
}
