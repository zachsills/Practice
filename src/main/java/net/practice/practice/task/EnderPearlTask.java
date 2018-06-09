package net.practice.practice.task;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class EnderPearlTask extends BukkitRunnable {

    private Player player;
    private long start;

    public EnderPearlTask(Player player) {
        this.player = player;

        this.start = System.currentTimeMillis();
    }

    @Override
    public void run() {
        player.setExp((float) (player.getExp() - (1F / (16 * 20))));
        player.setLevel(16 - ((int) (System.currentTimeMillis() - start) / 1000));

        if(player.getExp() <= 1F) {
            player.sendMessage(ChatColor.GREEN + "Your can now throw an ender pearl again.");
            player.setExp(0F);
            player.setLevel(0);
            this.cancel();
            return;
        }
    }
}
