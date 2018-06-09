package net.practice.practice.board.provider;

import net.practice.practice.board.BoardProvider;
import net.practice.practice.game.player.Profile;
import net.practice.practice.game.queue.Queue;
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
        lines.add(" ");
        lines.add("&eQueue: &7" + Profile.getTotalQueueing());
        lines.add("&eFighting: &7" + Profile.getTotalInGame());

        Profile profile = Profile.getByPlayer(player);
        if(profile.isQueueing()) {
            Queue queue = profile.getCurrentQueue();
            lines.add(" ");
            lines.add("&eCurrent: " + queue.getLadder().getDisplayName());
            lines.add("  &7Queue: &c" + queue.getSize());
            lines.add("  &7Position: &c" + (queue.getQueued().indexOf(player.getUniqueId()) + 1));
        }

        return lines;
    }
}
