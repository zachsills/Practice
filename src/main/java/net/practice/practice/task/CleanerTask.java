package net.practice.practice.task;

import net.practice.practice.game.duel.DuelRequest;
import net.practice.practice.game.player.Profile;
import org.bukkit.scheduler.BukkitRunnable;

public class CleanerTask extends BukkitRunnable {

    @Override
    public void run() {
        for(Profile profile : Profile.getProfiles().values()) {
            if(profile.getRecentDuel() != null) {
                if(profile.getRecentDuel().getEndTime() != 0L && Math.abs(System.currentTimeMillis() - profile.getRecentDuel().getEndTime()) >= 30000)
                    profile.setRecentDuel(null);
            }

            for(DuelRequest request : profile.getDuelRequests().values()) {
                if(request.getRequestedTime() != 0L && Math.abs(System.currentTimeMillis() - profile.getRecentDuel().getEndTime()) >= 30000)
                    profile.getDuelRequests().remove(request.getRequested().getName());
            }
        }
    }
}
