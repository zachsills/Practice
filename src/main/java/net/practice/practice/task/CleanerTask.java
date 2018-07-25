package net.practice.practice.task;

import net.practice.practice.game.duel.DuelRequest;
import net.practice.practice.game.duel.PartyDuelRequest;
import net.practice.practice.game.duel.type.PartyDuel;
import net.practice.practice.game.party.Party;
import net.practice.practice.game.party.PartyManager;
import net.practice.practice.game.player.Profile;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CleanerTask extends BukkitRunnable {

    @Override
    public void run() {
        for(Profile profile : Profile.getProfiles().values()) {
            if(profile.getRecentDuel() != null) {
                if(profile.getRecentDuel().getEndTime() != 0L && Math.abs(System.currentTimeMillis() - profile.getRecentDuel().getEndTime()) >= (1000 * 90))
                    profile.setRecentDuel(null);
            }

            Iterator<Map.Entry<String, DuelRequest>> requests = profile.getDuelRequests().entrySet().iterator();
            while(requests.hasNext()) {
                DuelRequest request = requests.next().getValue();
                if(request == null || request.getRequested() == null || !request.getRequested().isOnline()) {
                    requests.remove();
                    continue;
                }

                if(request.getRequestedTime() != 0L && Math.abs(System.currentTimeMillis() - request.getRequestedTime()) >= (1000 * 10))
                    requests.remove();
            }
        }

        for(Party party : PartyManager.getParties().values()) {
            Iterator<Map.Entry<Party, PartyDuelRequest>> requests = party.getRequests().entrySet().iterator();
            while(requests.hasNext()) {
                Map.Entry<Party, PartyDuelRequest> entry = requests.next();
                Party next = entry.getKey();
                if(next.getPlayers().size() == 0) {
                    requests.remove();
                    return;
                }

                if(Math.abs(System.currentTimeMillis() - entry.getValue().getRequestedTime()) >= (1000 * 90))
                    requests.remove();
            }
        }
    }
}
