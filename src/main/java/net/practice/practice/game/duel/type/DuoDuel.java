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
import net.practice.practice.util.chat.C;
import net.practice.practice.util.chat.JsonMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DuoDuel extends Duel {

    @Getter private List<Player> duoOne, duoTwo;

    @Getter @Setter private UUID winner;
    @Getter @Setter private boolean ranked;

    public DuoDuel(MapLoc map, Ladder ladder, List<Player> duoOne, List<Player> duoTwo) {
        super(map, ladder, DuelType.TWO_VS_TWO);

        this.duoOne = duoOne;
        this.duoTwo = duoTwo;
    }

    @Override
    public void preStart() {
        super.preStart();

        duoOne.forEach(player -> {
            player.teleport(getMap().getSpawnOne().toBukkit(MapLoc.getArenaWorld()));
        });
        duoTwo.forEach(player -> {
            player.teleport(getMap().getSpawnTwo().toBukkit(MapLoc.getArenaWorld()));
        });

        Profile.totalInGame += 4;
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void end(DuelEndReason reason) {
        super.end(reason);

        Party actualWinner = PartyManager.getByUuid(winner);

        sendMessage("&f&m---------------------------------");
        sendMessage("&6Winning Party: &e" + actualWinner.getLeaderName());

//        if(ranked) {
//            winnerProfile.setRankedWins(winnerProfile.getRankedWins() + 1);
//            loserProfile.setRankedLosses(loserProfile.getRankedLosses() + 1);
//
//            int oldWinnerElo = winnerProfile.getElo(getLadder()), oldLoserElo = loserProfile.getElo(getLadder());
//
//            int eloChange = handleElo(winnerProfile, loserProfile, getLadder());
//            sendMessage("&6Elo Changes: &e" + winner.getName() + " &a" + oldWinnerElo + " &7(&a+" + eloChange + "&7) &6- &e"
//                    + getLoser().getName() + " &c" + oldLoserElo + " &7(&c-" + eloChange + "&7)");
//        } else {
//            winnerProfile.setUnrankedWins(winnerProfile.getUnrankedWins() + 1);
//            loserProfile.setUnrankedLosses(loserProfile.getUnrankedLosses() + 1);
//        }

        JsonMessage message = new JsonMessage().append(ChatColor.GOLD + "Inventories " + ChatColor.GRAY + "(Click to view) ").save();

        for(InventorySnapshot snapshot : getSnapshots()) {
            if(actualWinner.contains(Bukkit.getPlayer(snapshot.getName()))) {
                message.append(ChatColor.GREEN + snapshot.getName()).setClickAsExecuteCmd("/_ " + snapshot.getName()).setHoverAsTooltip(ChatColor.GREEN + snapshot.getName() + "'s Inventory").save();
                if(duoOne.get(0).getName().equals(snapshot.getName()))
                    message.append(" &7or ").save();
                if(duoOne.get(1).getName().equals(snapshot.getName()))
                    message.append("\n").save();
            } else {
                message.append(ChatColor.RED + snapshot.getName()).setClickAsExecuteCmd("/_ " + snapshot.getName()).setHoverAsTooltip(ChatColor.RED + snapshot.getName() + "'s Inventory").save();
                if(duoTwo.get(0).getName().equals(snapshot.getName()))
                    message.append(" &7or ").save();
            }
        }

        message.send(Stream.concat(getPlayers().stream(), getSpectators().stream().map(Profile::getPlayer)).collect(Collectors.toList()).toArray(new Player[] {}));

        String spectatorMessage = getSpectatorMessage();
        if(spectatorMessage != null)
            sendMessage(spectatorMessage);

        sendMessage("&f&m---------------------------------");

        new BukkitRunnable() {
            @Override
            public void run() {
                getPlayers().stream()
                        .filter(Player::isOnline)
                        .forEach(SpawnHandler::spawn);

                getSpectators().forEach(profile -> {
                    SpawnHandler.spawn(profile.getPlayer());
                });

                Profile.totalInGame -= 4;
            }
        }.runTaskLater(Practice.getInstance(), 100L);
    }

    @Override
    public void kill(Player player) {
        saveInventory(player.getUniqueId());


    }

    @Override
    public void quit(Player player) {

    }

    @Override
    public Collection<Player> getPlayers() {
        return Stream.concat(duoOne.stream(), duoTwo.stream())
                .collect(Collectors.toList());
    }

    @Override
    public void sendMessage(String message) {
        super.sendMessage(message);

        duoOne.forEach(player -> player.sendMessage(C.color(message)));
        duoTwo.forEach(player -> player.sendMessage(C.color(message)));
    }

    @Override
    public boolean hasPlayer(Player player) {
        return getDuoOne().contains(player) || getDuoTwo().contains(player);
    }

    @Override
    public void saveInventories() {
        duoOne.stream()
                .filter(player -> !hasSnapshot(player))
                .forEach(this::saveInventory);

        duoTwo.stream()
                .filter(player -> !hasSnapshot(player))
                .forEach(this::saveInventory);
    }
}
