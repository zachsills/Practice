package net.practice.practice.task;

import net.practice.practice.game.duel.DuelRequest;
import net.practice.practice.game.player.Profile;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.Map;

public class CleanerTask extends BukkitRunnable {

    @Override
    public void run() {
        for(Profile profile : Profile.getProfiles().values()) {
            if(profile.getRecentDuel() != null) {
                if(profile.getRecentDuel().getEndTime() != 0L && Math.abs(System.currentTimeMillis() - profile.getRecentDuel().getEndTime()) >= 30000)
                    profile.setRecentDuel(null);
            }

            Iterator<Map.Entry<String, DuelRequest>> requests = profile.getDuelRequests().entrySet().iterator();
            while(requests.hasNext()) {
                DuelRequest request = requests.next().getValue();
                if(request == null || request.getRequested() == null || !request.getRequested().isOnline()) {
                    requests.remove();
                    continue;
                }

                if(request.getRequestedTime() != 0L && Math.abs(System.currentTimeMillis() - profile.getRecentDuel().getEndTime()) >= 30000)
                    requests.remove();
            }
        }
    }
}
