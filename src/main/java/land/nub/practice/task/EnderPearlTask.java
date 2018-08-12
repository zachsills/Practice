package land.nub.practice.task;

import land.nub.practice.game.duel.DuelState;
import land.nub.practice.game.player.Profile;
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
        Profile profile = Profile.getByPlayer(player);
        if(profile == null || !profile.isInGame()) {
            cancel();
            return;
        }
        if(profile.getCurrentDuel().getState() != DuelState.PLAYING) {
            cancel();
            return;
        }

        if(System.currentTimeMillis() - start >= 16000) {
            cancel();
            return;
        }

        player.setExp((float) (player.getExp() - (1F / (16 * 20))));
        player.setLevel(16 - ((int) (System.currentTimeMillis() - start) / 1000));
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        if(player != null && player.isOnline()) {
            player.sendMessage(ChatColor.GREEN + "Your can now throw an ender pearl again.");
            player.setExp(0F);
            player.setLevel(0);
        }

        super.cancel();
    }
}
