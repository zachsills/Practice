package net.practice.practice.task;

import land.nub.core.ranks.Rank;
import net.practice.practice.Practice;
import net.practice.practice.util.chat.C;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class AutoBroadcastTask extends BukkitRunnable {

    private int normalIndex = 0, allIndex = 0;

    @Override
    public void run() {
        List<String> normalMessages = Practice.getInstance().getAutoBroadcastFile().getMessages();
        List<String> nonDonorMessages = Practice.getInstance().getAutoBroadcastFile().getNonDonorMessages();

        List<String> allMessages = new ArrayList<>();
        allMessages.addAll(normalMessages);
        allMessages.addAll(nonDonorMessages);
        
        if (allIndex >= allMessages.size()) {
            allIndex = 0;
        }
        String allMessage = allMessages.get(allIndex);
        allIndex++;

        if (normalIndex >= normalMessages.size()) {
            normalIndex = 0;
        }
        String normalMessage = normalMessages.get(normalIndex);
        normalIndex++;
        
        for (Player player : Bukkit.getOnlinePlayers()) {
            land.nub.core.player.Profile coreProfile = land.nub.core.player.Profile.getByPlayer(player);

            if (player.isOp() || coreProfile.getRank() == Rank.getDefault()) {
                player.sendMessage(" ");
                player.sendMessage(C.color(allMessage));
                player.sendMessage(" ");
            } else {
                player.sendMessage(" ");
                player.sendMessage(C.color(normalMessage));
                player.sendMessage(" ");
            }
        }
    }
}
