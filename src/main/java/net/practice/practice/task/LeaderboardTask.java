package net.practice.practice.task;

import net.practice.practice.Practice;
import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.game.player.Profile;
import net.practice.practice.inventory.inventories.StatsInv;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class LeaderboardTask extends BukkitRunnable {

    @Override
    public void run() {
        List<Profile> profiles = Practice.getInstance().getBackend().getAllProfiles();

        Map<Ladder, Map<String, Integer>> top = new HashMap<>();

        for(Ladder ladder : Ladder.getLadders().values()) {
            Map<String, Integer> ladderTop = new LinkedHashMap<>();
            List<Profile> topProfiles = profiles.stream()
                    .filter(profile -> {
                        return profile.getName() != null;
                    })
                    .sorted(Comparator.comparingInt(profile -> profile.getElo(ladder)))
                    .collect(Collectors.toList());
            Collections.reverse(topProfiles);
            topProfiles.forEach(profile -> {
                ladderTop.put(profile.getName(), profile.getElo(ladder));
            });

            top.put(ladder, ladderTop);
        }

        StatsInv.setTop(top);
    }
}
