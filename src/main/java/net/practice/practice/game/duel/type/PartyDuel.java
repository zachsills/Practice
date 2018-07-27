package net.practice.practice.game.duel.type;

import lombok.Getter;
import lombok.Setter;
import net.practice.practice.Practice;
import net.practice.practice.game.arena.map.MapLoc;
import net.practice.practice.game.duel.Duel;
import net.practice.practice.game.duel.DuelEndReason;
import net.practice.practice.game.duel.DuelType;
import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.game.party.Party;
import net.practice.practice.game.party.PartyManager;
import net.practice.practice.game.player.Profile;
import net.practice.practice.game.player.data.InventorySnapshot;
import net.practice.practice.spawn.SpawnHandler;
import net.practice.practice.util.RunnableShorthand;
import net.practice.practice.util.chat.C;
import net.practice.practice.util.chat.JsonMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PartyDuel extends Duel {

    @Getter private Party partyOne, partyTwo;

    @Getter private List<Player> alive, dead;
    @Getter private List<Player> teamOne, teamTwo;

    @Getter @Setter private Party winner;

    @Getter private int partySizeOne, partySizeTwo;

    private int initialSize;

    public PartyDuel(MapLoc map, Ladder ladder, Party partyOne, Party partyTwo) {
        super(map, ladder, DuelType.TEAM_VS_TEAM);

        this.partyOne = partyOne;
        this.partyTwo = partyTwo;

        this.teamOne = partyOne.getAllPlayers();
        this.teamTwo = partyTwo.getAllPlayers();

        this.partySizeOne = partyOne.getSize();
        this.partySizeTwo = partyTwo.getSize();

        this.initialSize = partySizeOne + partySizeTwo;

        this.alive = new ArrayList<>();
        this.dead = new ArrayList<>();
    }

    @Override
    public void preStart() {
        super.preStart();

        teamOne.forEach(player -> {
            player.teleport(getMap().getSpawnOne().toBukkit(MapLoc.getArenaWorld()));
        });
        teamTwo.forEach(player -> {
            player.teleport(getMap().getSpawnTwo().toBukkit(MapLoc.getArenaWorld()));
        });

        Profile.totalInGame += initialSize;
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void end(DuelEndReason reason) {
        super.end(reason);

        sendMessage(ChatColor.GOLD + "Inventories " + ChatColor.GRAY + "(Click to view) ");
        JsonMessage message = new JsonMessage();

        List<Player> winningTeam = winner.contains(teamOne.get(0)) ? teamOne : teamTwo;
        for(int i = 0; i < winningTeam.size(); i++) {
            InventorySnapshot snapshot = getSnapshot(winningTeam.get(i));
            if(i < winningTeam.size() - 2)
                message.append(ChatColor.GREEN + snapshot.getName() + ChatColor.GRAY + ", ").setClickAsExecuteCmd("/_ " + snapshot.getName()).setHoverAsTooltip(ChatColor.GREEN + snapshot.getName() + "'s Inventory").save();
            else
                message.append(" " + ChatColor.GREEN + snapshot.getName()).setClickAsExecuteCmd("/_ " + snapshot.getName()).setHoverAsTooltip(ChatColor.GREEN + snapshot.getName() + "'s Inventory").save();
        }
        message.append("\n").save();

        List<Player> losingTeam = winner.contains(teamOne.get(0)) ? teamTwo : teamOne;
        for(int i = 0; i < losingTeam.size(); i++) {
            InventorySnapshot snapshot = getSnapshot(losingTeam.get(i));
            if(i < losingTeam.size() - 2)
                message.append(ChatColor.RED + snapshot.getName() + ChatColor.GRAY + ", ").setClickAsExecuteCmd("/_ " + snapshot.getName()).setHoverAsTooltip(ChatColor.RED + snapshot.getName() + "'s Inventory").save();
            else
                message.append(" " + ChatColor.RED + snapshot.getName()).setClickAsExecuteCmd("/_ " + snapshot.getName()).setHoverAsTooltip(ChatColor.RED + snapshot.getName() + "'s Inventory").save();
        }

        message.send(Stream.concat(getPlayers().stream(), getSpectators().stream().map(Profile::getPlayer)).collect(Collectors.toList()).toArray(new Player[] {}));

        String spectatorMessage = getSpectatorMessage();
        if(spectatorMessage != null)
            sendMessage(spectatorMessage);

        new BukkitRunnable() {
            @Override
            public void run() {
                getPlayers().stream()
                        .filter(Objects::nonNull)
                        .filter(Player::isOnline)
                        .forEach(SpawnHandler::spawn);

                getSpectators().forEach(profile -> {
                    SpawnHandler.spawn(profile.getPlayer());
                });

                Profile.totalInGame -= initialSize;
            }
        }.runTaskLater(Practice.getInstance(), 100L);
    }

    @Override
    public Collection<Player> getPlayers() {
        return Stream.concat(teamOne.stream(), teamTwo.stream())
                .collect(Collectors.toList());
    }

    @Override
    public boolean canHit(Player playerOne, Player playerTwo) {
        Party party = getParty(playerOne);

        return !party.contains(playerTwo);
    }

    @Override
    public void sendMessage(String message) {
        super.sendMessage(message);

        getPlayers().forEach(player -> {
            player.sendMessage(C.color(message));
        });
    }

    @Override
    public void kill(Player player) {
        super.kill(player);

        dead.add(player);
        alive.remove(player);

        RunnableShorthand.runNextTick(() -> {
            Profile profile = Profile.getByPlayer(player);
            profile.setSpectating(this, false);
        });

        if(hasWinner())
            end(DuelEndReason.DIED);
    }

    @Override
    public void quit(Player player) {
        super.quit(player);

        dead.add(player);
        alive.remove(player);

        if(hasWinner())
            end(DuelEndReason.QUIT);
    }

    @Override
    public boolean hasPlayer(Player player) {
        return partyOne.contains(player) || partyTwo.contains(player);
    }

    @Override
    public void saveInventories() {
        partyOne.getPlayers().stream()
                .filter(player -> !hasSnapshot(player))
                .forEach(this::saveInventory);

        partyTwo.getPlayers().stream()
                .filter(player -> !hasSnapshot(player))
                .forEach(this::saveInventory);
    }

    public Party getParty(Player player) {
        return partyOne.contains(player) ? partyOne : partyTwo;
    }

    public int getAlive(Party party) {
        List<Player> team = teamOne.contains(party.getAllPlayers().get(0)) ? teamOne : teamTwo;

        return (int) team.stream().filter(alive::contains).count();
    }

    private boolean hasWinner() {
        List<Player> winner = findWinner();
        if(winner != null) {
            Player alive = winner.stream().filter(getAlive()::contains).findFirst().orElse(winner.get(0));

            setWinner(Profile.getByPlayer(alive).getParty());
        }

        return winner != null;
    }

    private List<Player> findWinner() {
        int deadCountOne = (int) teamOne.stream().filter(dead::contains).count();
        int deadCountTwo = (int) teamTwo.stream().filter(dead::contains).count();

        if(deadCountOne >= initialSize)
            return teamOne;
        if(deadCountTwo >= initialSize)
            return teamTwo;

        return null;
    }
}
