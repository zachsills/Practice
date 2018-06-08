package net.practice.practice.board.provider;

import net.practice.practice.board.BoardProvider;
import net.practice.practice.game.player.Profile;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LobbyProvider implements BoardProvider {

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public List<String> getLines(Player player) {
        List<String> lines = new ArrayList<>();

        lines.add("&ePlayer: &c" + player.getName());
        lines.add("");
        lines.add("&eQueue: &7" + Profile.getTotalQueueing());
        lines.add("&eFighting: &7" + Profile.getTotalInGame());

        /*Profile profile = Profile.getByPlayer(player);
        Queue queue = profile.getCurrentQueue();
        if (queue != null) {
            lines.add("");
            lines.add("&eCurrent: " + queue.getLadder().getDisplayName());
            lines.add("  &7Queue: &c" + Profile.getNumberQueuing(queue.getLadder()));
            lines.add("  &7Fighting: &c" + Profile.getNumberInGame(queue.getLadder()));
            lines.add("  &7Time: &c" + queue.getTimeQueuingFormatted());
        }*/

        return lines;
    }
}
