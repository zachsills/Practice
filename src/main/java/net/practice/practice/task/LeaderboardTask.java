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

        Map<Ladder, List<Profile>> top = new HashMap<>();

        for(Ladder ladder : Ladder.getLadders().values()) {
            List<Profile> topProfiles = profiles.stream()
                    .sorted(Comparator.comparingInt(profile -> profile.getElo(ladder)))
                    .collect(Collectors.toList());
            Collections.reverse(topProfiles);

            top.put(ladder, topProfiles);
        }

        StatsInv.setTop(top);
    }
}
