package net.practice.practice.board;

import lombok.Getter;
import net.practice.practice.Practice;
import net.practice.practice.util.RunnableShorthand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BoardManager implements Listener {
    private final Practice plugin = Practice.getInstance();

    private Map<UUID, Board> scoreboards;
    private BukkitTask updateTask;

    @Getter private BoardProvider provider;

    public BoardManager(BoardProvider provider) {
        this.scoreboards = new HashMap<>();
        this.updateTask = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::updateAll, 2, 2);

        this.provider = provider;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void setupAll() {
        Bukkit.getOnlinePlayers().forEach(this::setup);
    }

    private void setup(Player player) {
        Board current = scoreboards.remove(player.getUniqueId());
        if(current != null)
            current.clear();

        if(player.getScoreboard() == Bukkit.getScoreboardManager().getMainScoreboard())
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());

        scoreboards.put(player.getUniqueId(), new Board(player));
    }

    private void remove(Player player) {
        Board current = scoreboards.remove(player.getUniqueId());

        if(current != null)
            current.clear();
    }

    public void updateAll() {
        scoreboards.values().forEach(Board::send);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onJoin(final PlayerJoinEvent e) {
        RunnableShorthand.runNextTick(() -> {
            this.setup(e.getPlayer());
        });
    }

    @EventHandler
    public void onQuit(final PlayerQuitEvent e) {
        this.remove(e.getPlayer());
    }

    public void onDisable() {
        try {
            this.updateTask.cancel();
        } catch(IllegalStateException exception) {
        }

        Bukkit.getOnlinePlayers().forEach(this::remove);
        HandlerList.unregisterAll(this);
        this.scoreboards.clear();
    }
}
