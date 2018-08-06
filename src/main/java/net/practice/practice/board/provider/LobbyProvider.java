package net.practice.practice.board.provider;

import net.practice.practice.board.BoardProvider;
import net.practice.practice.game.party.Party;
import net.practice.practice.game.player.Profile;
import net.practice.practice.game.queue.Queue;
import net.practice.practice.game.queue.type.RankedPartyQueue;
import net.practice.practice.game.queue.type.RankedSoloQueue;
import net.practice.practice.util.PlayerUtils;
import net.practice.practice.util.chat.C;
import org.bukkit.Bukkit;
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

        lines.add("&6In Game: &f" + Profile.getTotalInGame());
        lines.add("&6In Queue: &f" + Profile.getTotalQueueing());
        lines.add(" ");
        lines.add("&6Ping: &f" + PlayerUtils.getPing(player) + "ms");

        Profile profile = Profile.getByPlayer(player);
        if(profile.isQueueing()) {
            Queue queue = profile.getCurrentQueue();
            lines.add(" ");
            lines.add("&6Current: &e" + C.strip(queue.getLadder().getDisplayName()));
            lines.add("  &7Queue: &f" + queue.getSize());
            lines.add("  &7Position: &f" + (queue.getQueued().indexOf(player.getUniqueId()) + 1));
            if(queue instanceof RankedSoloQueue) {
                RankedSoloQueue rankedQueue = (RankedSoloQueue) queue;

                lines.add("  &7Range: &c" + rankedQueue.getRanges().get(player.getUniqueId()).getMin() + " &7-> &c" + rankedQueue.getRanges().get(player.getUniqueId()).getMax());
            }
        }

        if(profile.isInParty()) {
            Party party = profile.getParty();
            lines.add(" ");
            lines.add("&6Party: &f" + Bukkit.getPlayer(party.getLeader()).getName());
            lines.add("  &6Members: &f" + party.getSize());
            if(party.getCurrentQueue() != null) {
                lines.add("  &6Queue: &f" + C.strip(party.getCurrentQueue().getLadder().getDisplayName()));
                if(party.getCurrentQueue() instanceof RankedPartyQueue) {
                    RankedPartyQueue rankedQueue = (RankedPartyQueue) party.getCurrentQueue();

                    lines.add("  &7Range: &c" + rankedQueue.getRanges().get(party.getId()).getMin() + " &7-> &c" + rankedQueue.getRanges().get(party.getId()).getMax());
                }
            }
        }

        lines.add(" ");
        lines.add("&6nub.land");

        return lines;
    }
}
