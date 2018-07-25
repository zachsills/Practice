package net.practice.practice.game.duel;

import lombok.Getter;
import lombok.Setter;
import net.practice.practice.game.arena.map.MapLoc;
import net.practice.practice.game.duel.type.DuoDuel;
import net.practice.practice.game.duel.type.SoloDuel;
import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.game.party.Party;
import net.practice.practice.game.player.Profile;
import net.practice.practice.spawn.SpawnHandler;
import net.practice.practice.util.chat.C;
import net.practice.practice.util.chat.JsonMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PartyDuelRequest {

    @Getter private Party requester, requested;
    @Getter private Ladder ladder;

    @Getter private long requestedTime;

    @Getter @Setter private boolean rematch;

    @Getter private boolean ranked;

    public PartyDuelRequest(Party requester, Party requested, Ladder ladder, boolean ranked) {
        this.requestedTime = System.currentTimeMillis();

        this.requester = requester;
        this.requested = requested;

        this.ladder = ladder;

        this.ranked = ranked;

        requester.getRequests().put(requested, this);
    }

    public PartyDuelRequest(Party requester, Party requested, Ladder ladder) {
        this(requester, requested, ladder, false);
    }

    public void sendToRequested() {
        Player requesterLeader = Bukkit.getPlayer(requester.getLeader());
        Player requestedLeader = Bukkit.getPlayer(requested.getLeader());

        if (requesterLeader == null || requestedLeader == null)
            return;

        requestedLeader.sendMessage(" ");

        if(rematch) {
            requestedLeader.sendMessage(C.color("&e" + requesterLeader.getName() + "'s Party wants a rematch!"));
        } else {
            requestedLeader.sendMessage(C.color("&cNew Duel Request: "));
            requestedLeader.sendMessage(C.color("  &7From: &6" + requesterLeader.getName()) + "'s Party");
        }

        requestedLeader.sendMessage(C.color("  &7Ladder: &6" + ladder.getDisplayName() + (ranked ? " (R)" : "")));
        new JsonMessage()
                .append(ChatColor.GREEN + "  ACCEPT").setClickAsExecuteCmd("/accept " + requesterLeader.getName()).setHoverAsTooltip(ChatColor.GREEN + "Accept " + requesterLeader.getName()).save()
                .append(C.color(" &7or ")).save()
                .append(ChatColor.RED + "DENY").setClickAsExecuteCmd("/deny " + requesterLeader.getName()).setHoverAsTooltip(ChatColor.RED + "Deny " + requesterLeader.getName()).save()
                .send(requestedLeader);
        requestedLeader.sendMessage(" ");
    }

    public void accept() {
        Player requesterLeader = Bukkit.getPlayer(requester.getLeader());
        Player requestedLeader = Bukkit.getPlayer(requested.getLeader());

        if (requesterLeader == null || requestedLeader == null)
            return;

        requesterLeader.sendMessage(C.color("&a" + requestedLeader.getName() + " has accepted your duel request."));
        requestedLeader.sendMessage(C.color("&aYou have accepted a duel request from " + requesterLeader.getName() + "."));

        MapLoc map = MapLoc.getRandomMap(getLadder().isBuildable(), getLadder().isSpleef());
        if (map != null)
            new DuoDuel(map, getLadder(), requester.getAllPlayers(), requested.getAllPlayers()).preStart();
        else {
            for (Player player : requester.getAllPlayers()) {
                player.sendMessage("No open arenas available!");
                SpawnHandler.spawn(player);
            }
            for (Player player : requested.getAllPlayers()) {
                player.sendMessage("No open arenas available!");
                SpawnHandler.spawn(player);
            }
        }

        deny();
    }

    public void deny() {
        requester.getRequests().remove(requested);
    }
}
