package net.practice.practice.listener.listeners;

import net.practice.practice.game.player.Profile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

public class PlayerListener implements Listener {

    @EventHandler
    public void onAsyncLogin(AsyncPlayerPreLoginEvent event) {
        new Profile(event.getUniqueId(), true);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Profile profile = Profile.getByPlayer(event.getPlayer());

        // TODO: Teleport to spawn and initialize initial inventory
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        handleLeave(event);
    }

    @EventHandler
    public void onKick(PlayerKickEvent event) {
        handleLeave(event);
    }

    private void handleLeave(PlayerEvent event) {
        Profile profile = Profile.getRemovedProfile(event.getPlayer());

        profile.save();
    }
}
