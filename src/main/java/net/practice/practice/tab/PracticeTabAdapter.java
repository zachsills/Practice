package net.practice.practice.tab;

import com.bizarrealex.azazel.tab.TabAdapter;
import com.bizarrealex.azazel.tab.TabTemplate;
import net.practice.practice.game.duel.Duel;
import net.practice.practice.game.duel.DuelState;
import net.practice.practice.game.duel.DuelType;
import net.practice.practice.game.duel.type.SoloDuel;
import net.practice.practice.game.player.Profile;
import net.practice.practice.game.player.data.ProfileState;
import net.practice.practice.util.PlayerUtils;
import net.practice.practice.util.TimeUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

public class PracticeTabAdapter implements TabAdapter {

    private static DecimalFormat decimalFormat = new DecimalFormat("0.0");

    @Override
    public TabTemplate getTemplate(Player player, int protocolVersion) {
        TabTemplate template = new TabTemplate();

        // Header
        if (protocolVersion >= 47) { // 1.8+
            template.middle(0, " &6&lNub Land &7&l| &f&lUS");
            template.left(0, "&f&m---------------");
            template.right(0, "&f&m---------------");
            template.farRight(0, "&f&m---------------");
        } else { // 1.7
            template.middle(0, "  &6&lNub Land &7&l| &f&lUS");
            template.left(0, "&f&m--------------");
            template.right(0, "&f&m--------------");
        }

        // Far right
        template.farRight(8, "&7Well hello there,");
        template.farRight(9, "&71.8+ user!");

        String highlightC = "&6", infoC = "&f";

        Profile profile = Profile.getByPlayer(player);
        if (profile.getState() == ProfileState.LOBBY) {

            int online = Bukkit.getOnlinePlayers().size();
            long inGame = Profile.getTotalInGame();
            long inQueue = Profile.getTotalQueueing();
            int ping = PlayerUtils.getPing(player);

            template.left(2, highlightC + "Online:");
            template.left(3, infoC + online);
            template.left(5, highlightC + "In Game:");
            template.left(6, infoC + inGame);
            template.left(8, highlightC + "In Queue:");
            template.left(9, infoC + inQueue);

            template.middle(2, highlightC + "Player:");
            template.middle(3, infoC + player.getName());
            template.middle(5, highlightC + "Rank:");
            template.middle(6, infoC + "ur rank xd");
            template.middle(8, highlightC + "Ping:");
            template.middle(9, infoC + ping + "ms");

            template.right(2, highlightC + "IP:");
            template.right(3, infoC + "nub.land");
            template.right(5, highlightC + "Website:");
            template.right(6, infoC + "www.nub.land");
            template.right(8, highlightC + "Donate:");
            template.right(9, infoC + "donate.nub.land");

        } else if (profile.getState() == ProfileState.QUEUING) {

            int online = Bukkit.getOnlinePlayers().size();
            long inGame = Profile.getTotalInGame();
            long inQueue = Profile.getTotalQueueing();
            int ping = PlayerUtils.getPing(player);

            template.left(2, highlightC + "Online:");
            template.left(3, infoC + online);
            template.left(5, highlightC + "In Game:");
            template.left(6, infoC + inGame);
            template.left(8, highlightC + "In Queue:");
            template.left(9, infoC + inQueue);

            template.middle(2, highlightC + "Player:");
            template.middle(3, infoC + player.getName());
            template.middle(5, highlightC + "Rank:");
            template.middle(6, infoC + "ur rank xd");
            template.middle(8, highlightC + "Ping:");
            template.middle(9, infoC + ping + "ms");

            template.right(2, highlightC + "IP:");
            template.right(3, infoC + "nub.land");
            template.right(5, highlightC + "Website:");
            template.right(6, infoC + "www.nub.land");
            template.right(8, highlightC + "Donate:");
            template.right(9, infoC + "donate.nub.land");

            String ladder = profile.getCurrentQueue().getLadder().getDisplayName();
            int inLadderQueue = profile.getCurrentQueue().getSize();
            int position = profile.getCurrentQueue().getQueued().indexOf(player.getUniqueId()) + 1;

            // Left
            template.left(11, highlightC + "Ladder:");
            template.left(12, infoC + ladder);

            // Middle
            template.middle(11, highlightC + "In Queue:");
            template.middle(12, infoC + inLadderQueue);

            // Right
            template.right(11, highlightC + "Position:");
            template.right(12, infoC + position);
        } else if (profile.getState() == ProfileState.PLAYING) {
            Duel duel = profile.getCurrentDuel();
            if (duel != null) {
                String ladder = duel.getLadder().getDisplayName();
                String duration = TimeUtils.msToMMSS(System.currentTimeMillis() - duel.getStartTime());

                if (duel.getType() == DuelType.ONE_VS_ONE) {
                    SoloDuel soloDuel = (SoloDuel) duel;
                    Player opponent = (soloDuel.getPlayerOne() == player ? soloDuel.getPlayerTwo() : soloDuel.getPlayerOne());

                    if (duel.getState() == DuelState.STARTING) {
                        template.left(2, highlightC + "You:");
                        template.left(3, "&a" + player.getName());

                        template.middle(2, highlightC + "Starting:");
                        template.middle(3, infoC + duel.getCountDown());

                        template.right(2, highlightC + "Opponent:");
                        template.right(3, "&c" + opponent.getName());
                    } else {
                        double accuracy;
                        if (duel.getThrownPots().getOrDefault(player, 0) == 0) {
                            accuracy = 100;
                        } else {
                            accuracy = ((duel.getThrownPots().get(player) - duel.getMissedPots().get(player))
                                    / (double) duel.getThrownPots().get(player)) * 100.0;
                        }

                        template.left(2, highlightC + "You:");
                        template.left(3, "&a" + player.getName());

                        template.middle(2, highlightC + "Ladder:");
                        template.middle(3, infoC + ladder);
                        template.middle(5, highlightC + "Duration:");
                        template.middle(6, infoC + duration);
                        template.middle(8, highlightC + "Thrown | Missed:");
                        template.middle(9, infoC + duel.getThrownPots().get(player) + " | " + duel.getMissedPots().get(player));
                        template.middle(11, highlightC + "Pot Accuracy:");
                        template.middle(12, infoC + decimalFormat.format(accuracy) + "%");

                        template.right(2, highlightC + "Opponent:");
                        template.right(3, "&c" + opponent.getName());
                    }
                }
            }
        }

        // Footer
        if (protocolVersion >= 47) { // 1.8+
            template.left(19, "&f&m---------------");
            template.middle(19, "&f&m---------------");
            template.right(19, "&f&m---------------");
            template.farRight(19, "&f&m---------------");
        } else { // 1.7
            template.left(19, "&f&m--------------");
            template.middle(19, "&f&m--------------");
            template.right(19, "&f&m--------------");
        }

        return template;
    }
}
