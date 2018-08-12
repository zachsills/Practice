package net.practice.practice.game.party;

import lombok.Getter;
import lombok.Setter;
import net.practice.practice.game.duel.Duel;
import net.practice.practice.game.duel.PartyDuelRequest;
import net.practice.practice.game.queue.Queue;
import net.practice.practice.spawn.PartyHandler;
import net.practice.practice.util.chat.C;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class Party {

    @Getter @Setter private UUID id, leader;

    @Getter private List<Player> players, invites;

    @Getter private Map<Party, PartyDuelRequest> requests;

    @Getter @Setter private Duel currentDuel;
    @Getter @Setter private PartyState state;
    @Getter @Setter private Queue currentQueue;

    public Party(UUID leader) {
        this.id = UUID.randomUUID();
        this.leader = leader;

        this.requests = new HashMap<>();

        this.players = new ArrayList<>();
        this.invites = new ArrayList<>();

        this.state = PartyState.LOBBY;

        PartyManager.addParty(this);
    }

    public void sendMessage(String message) {
        Bukkit.getPlayer(leader).sendMessage(C.color(message));
        players.forEach(player -> player.sendMessage(C.color(message)));
    }

    public boolean contains(Player player) {
        for(Player ply : getAllPlayers())
            if(ply == player || ply.getUniqueId().equals(player.getUniqueId()))
                return true;

        return false;
    }

    public String getLeaderName() {
        return Bukkit.getPlayer(leader).getName();
    }

    public boolean isInGame() {
        return state == PartyState.PLAYING;
    }

    public List<Player> getAllPlayers() {
        List<Player> players = new ArrayList<>(getPlayers());
        players.add(Bukkit.getPlayer(leader));

        return players;
    }

    public int getSize() {
        return players.size() + 1;
    }

    public void leaveQueue() {
        Player leader = Bukkit.getPlayer(getLeader());
        PartyHandler.spawn(leader, true);

        currentQueue.remove(id);
        setCurrentQueue(null);
    }

    public static Party createParty(Player player) {
        return new Party(player.getUniqueId());
    }
}
