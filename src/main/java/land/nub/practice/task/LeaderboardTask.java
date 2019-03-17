package land.nub.practice.task;

import land.nub.practice.Practice;
import land.nub.practice.game.ladder.Ladder;
import land.nub.practice.game.player.Profile;
import land.nub.practice.inventory.inventories.StatsInv;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class LeaderboardTask extends BukkitRunnable {

    @Override
    public void run() {
        Map<Ladder, Map<String, Integer>> top = new HashMap<>();

        for(Ladder ladder : Ladder.getLadders().values()) {
            Map<String, Integer> ladderTop = new LinkedHashMap<>();
            List<Profile> topProfiles = Practice.getInstance().getBackend().getTopProfiles(ladder);
            topProfiles.forEach(profile -> {
                ladderTop.put(profile.getName(), profile.getElo(ladder));
            });

            top.put(ladder, ladderTop);
        }

        StatsInv.setTop(top);
    }
}
