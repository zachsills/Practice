package net.practice.practice.board;

import lombok.Getter;
import net.practice.practice.Practice;
import net.practice.practice.util.chat.C;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.List;
import java.util.stream.Collectors;

public class Board {
    @Getter private static final Practice plugin = Practice.getInstance();

    @Getter private static final String[] ENTRY_NAMES = new String[15];

    @Getter private final Player player;
    @Getter private Scoreboard scoreboard;
    @Getter private Objective objective;

    @Getter private boolean ready;

    public Board(Player player) {
        this.player = player;
        this.scoreboard = player.getScoreboard();

        Objective objective = scoreboard.getObjective("practice");
        if(objective == null)
            objective = scoreboard.registerNewObjective("practice", "dummy");

        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(plugin.getBoardManager().getProvider().getTitle());

        this.objective = objective;
        this.ready = true;

        send();
    }

    public void send() {
        if(player == null || !player.isOnline() || !ready)
            return;

        BoardProvider provider = plugin.getBoardManager().getProvider();
        if(provider == null)
            return;

        List<String> lines = provider.getLines(player);
        if(lines == null || lines.isEmpty())
            return;

        if(scoreboard.getEntries().size() != lines.size())
            scoreboard.getEntries().forEach(this::removeEntry);

        int index = 0;
        for(String entry : lines) {
            Entry split = split(entry);

            Team team = scoreboard.getTeam(ENTRY_NAMES[index]);
            if(team == null) {
                try {
                    team = scoreboard.registerNewTeam(ENTRY_NAMES[index]);
                } catch(IllegalArgumentException exception) { }

                team.addEntry(team.getName());
            }

            team.setPrefix(split.left);
            team.setSuffix(split.right);
            objective.getScore(team.getName()).setScore(15 - index);
            index++;
        }
    }

    private Entry split(String text) {
        Entry entry = new Entry();
        if(text.length() <= 16) {
            entry.left = text;
        } else {
            String prefix = text.substring(0, 16), suffix = "";

            if(prefix.endsWith("\u00a7")) {
                prefix = prefix.substring(0, prefix.length() - 1);
                suffix = "\u00a7" + suffix;
            }

            suffix = StringUtils.left(ChatColor.getLastColors(prefix) + suffix + text.substring(16), 16);
            entry.left = prefix;
            entry.right = suffix;
        }
        return entry;
    }

    public void clear() {
        ready = false;
        player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());

        scoreboard.getEntries().forEach(scoreboard::resetScores);
        scoreboard.getObjectives().forEach(Objective::unregister);
        scoreboard.getTeams().forEach(Team::unregister);
    }

    private void removeEntry(String id) {
        removeEntry(id, false);
    }

    private void removeEntry(String id, boolean deleteTeam) {
        scoreboard.resetScores(id);

        if(deleteTeam) {
            Team team = scoreboard.getTeam(id);
            if(team != null)
                team.unregister();
        }
    }

    private class Entry {
        private String left = "", right = "";
    }

    static {
        for(int i = 0; i < 15; i++)
            ENTRY_NAMES[i] = ChatColor.AQUA + ChatColor.values()[i].toString() + ChatColor.RESET;
    }
}
