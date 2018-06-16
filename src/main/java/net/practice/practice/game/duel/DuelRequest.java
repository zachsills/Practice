package net.practice.practice.game.duel;

import lombok.Getter;
import lombok.Setter;
import net.practice.practice.game.arena.Arena;
import net.practice.practice.game.duel.type.SoloDuel;
import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.game.player.Profile;
import net.practice.practice.util.chat.C;
import net.practice.practice.util.chat.JsonMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class DuelRequest {

    @Getter private Player requester, requested;
    @Getter private Ladder ladder;

    @Getter private long requestedTime;

    @Getter @Setter private boolean rematch;

    @Getter private boolean ranked;

    public DuelRequest(Player requester, Player requested, Ladder ladder, boolean ranked) {
        this.requestedTime = System.currentTimeMillis();

        this.requester = requester;
        this.requested = requested;

        this.ladder = ladder;

        this.ranked = ranked;

        Profile.getByPlayer(requester).getDuelRequests().putIfAbsent(requested.getName(), this);
    }

    public DuelRequest(Player requester, Player requested, Ladder ladder) {
        this(requester, requested, ladder, false);
    }

    public void sendToRequested() {
        requested.sendMessage(" ");

        if(rematch) {
            requested.sendMessage(C.color("&e" + requester.getName() + " wants a rematch!"));
        } else {
            requested.sendMessage(C.color("&cNew Duel Request: "));
            requested.sendMessage(C.color("  &7From: &6" + requester.getName()));
        }

        requested.sendMessage(C.color("  &7Ladder: &6" + ladder.getDisplayName() + (ranked ? " (R)" : "")));
        new JsonMessage()
                .append(ChatColor.GREEN + "  ACCEPT").setClickAsExecuteCmd("/accept " + requester.getName()).setHoverAsTooltip(ChatColor.GREEN + "Accept " + requester.getName()).save()
                .append(C.color(" &7or ")).save()
                .append(ChatColor.RED + "DENY").setClickAsExecuteCmd("/deny " + requester.getName()).setHoverAsTooltip(ChatColor.RED + "Deny " + requester.getName()).save()
                .send(requested);
        requested.sendMessage(" ");
    }

    public void accept() {
        requester.sendMessage(C.color("&a" + requested.getName() + " has accepted your duel request."));
        requested.sendMessage(C.color("&aYou have accepted a duel request from " + requester.getName() + "."));

        new SoloDuel(Arena.getRandomArena(getLadder()), getLadder(), requester, requested, ranked).preStart();

        deny();
    }

    public void deny() {
        Profile.getByPlayer(requester).getDuelRequests().remove(requested.getName());
    }
}
