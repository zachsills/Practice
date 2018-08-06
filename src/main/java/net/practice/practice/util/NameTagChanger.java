package net.practice.practice.util;

import net.practice.practice.util.chat.C;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.Collection;
import java.util.Collections;

public class NameTagChanger {

    private static Team team;
    private static Scoreboard scoreboard;

    public static void changePlayerName(Player player, String prefix, TeamAction action, Player target) {
        changePlayerName(player, prefix, action, Collections.singleton(target));
    }

    public static void changePlayerName(Player player, String prefix, TeamAction action, Collection<? extends Player> targets) {
        for(Player target : targets) {
            if(target.getScoreboard() == null || prefix == null || action == null)
                return;

            scoreboard = target.getScoreboard();

            if(scoreboard.getTeam(player.getName()) == null)
                scoreboard.registerNewTeam(player.getName());

            team = scoreboard.getTeam(player.getName());
            team.setPrefix(C.color(prefix));
            team.setNameTagVisibility(NameTagVisibility.ALWAYS);

            switch(action) {
                case CREATE:
                    team.addPlayer(player);
                    break;
                case UPDATE:
                    team.unregister();
                    scoreboard.registerNewTeam(player.getName());
                    team = scoreboard.getTeam(player.getName());
                    team.setPrefix(C.color(prefix));
                    team.setNameTagVisibility(NameTagVisibility.ALWAYS);
                    team.addPlayer(player);
                    break;
                case DESTROY:
                    team.unregister();
                    break;
            }
        }
    }

    public enum TeamAction {
        
        CREATE,
        DESTROY,
        UPDATE
    }
}
