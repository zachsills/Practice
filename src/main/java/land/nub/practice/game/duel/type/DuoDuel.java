package land.nub.practice.game.duel.type;

import land.nub.practice.Practice;
import land.nub.practice.game.arena.map.MapLoc;
import land.nub.practice.game.duel.DuelEndReason;
import land.nub.practice.game.duel.DuelType;
import land.nub.practice.game.ladder.Ladder;
import land.nub.practice.game.party.PartyManager;
import land.nub.practice.game.player.Profile;
import land.nub.practice.util.RunnableShorthand;
import land.nub.practice.util.chat.C;
import lombok.Getter;
import lombok.Setter;
import land.nub.practice.game.duel.Duel;
import land.nub.practice.game.party.Party;
import land.nub.practice.game.player.data.InventorySnapshot;
import land.nub.practice.spawn.SpawnHandler;
import land.nub.practice.util.chat.JsonMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DuoDuel extends Duel {

    @Getter private List<Player> duoOne, duoTwo;

    @Getter @Setter private UUID winner;
    @Getter @Setter private boolean ranked;

    @Getter private List<Player> alive, dead;

    public DuoDuel(MapLoc map, Ladder ladder, List<Player> duoOne, List<Player> duoTwo) {
        super(map, ladder, DuelType.TWO_VS_TWO);

        this.duoOne = duoOne;
        this.duoTwo = duoTwo;

        this.alive = new ArrayList<>();
        this.dead = new ArrayList<>();

        alive.addAll(duoOne);
        alive.addAll(duoTwo);
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

        Party actualWinners = PartyManager.getByUuid(winner);

        sendMessage("&f&m---------------------------------");
        sendMessage("&6Winning Party: &e" + actualWinners.getLeaderName());

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

        sendMessage(ChatColor.GOLD + "Inventories " + ChatColor.GRAY + "(Click to view) ");
        JsonMessage message = new JsonMessage();

        List<Player> winningDuo = actualWinners.contains(duoOne.get(0)) ? duoOne : duoTwo;
        for(Player player : winningDuo) {
            InventorySnapshot snapshot = getSnapshot(player);
            message.append(ChatColor.GREEN + snapshot.getName()).setClickAsExecuteCmd("/_ " + snapshot.getName()).setHoverAsTooltip(ChatColor.GREEN + snapshot.getName() + "'s Inventory").save();
            if(winningDuo.indexOf(player) == 0)
                message.append(ChatColor.GRAY + " or ").save();
        }
        message.append("\n").save();

        List<Player> losingDuo = actualWinners.contains(duoOne.get(0)) ? duoTwo : duoOne;
        for(Player player : losingDuo) {
            InventorySnapshot snapshot = getSnapshot(player);
            message.append(ChatColor.RED + snapshot.getName()).setClickAsExecuteCmd("/_ " + snapshot.getName()).setHoverAsTooltip(ChatColor.RED + snapshot.getName() + "'s Inventory").save();
            if(losingDuo.indexOf(player) == 0)
                message.append(ChatColor.GRAY + " or ").save();
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
                        .filter(Objects::nonNull)
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
    public boolean canHit(Player playerOne, Player playerTwo) {
        List<Player> duo = getDuo(playerOne);

        return !duo.contains(playerTwo);
    }

    @Override
    public Collection<Player> getPlayers() {
        return Stream.concat(duoOne.stream(), duoTwo.stream())
                .filter(Objects::nonNull)
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

    private boolean hasWinner() {
        List<Player> winner = findWinner();
        if(winner != null) {
            Player alive = winner.stream().filter(getAlive()::contains).findFirst().orElse(winner.get(0));

            setWinner(Profile.getByPlayer(alive).getParty().getId());
        }

        return winner != null;
    }

    private List<Player> findWinner() {
        int deadCountOne = (int) duoOne.stream().filter(dead::contains).count();
        int deadCountTwo = (int) duoTwo.stream().filter(dead::contains).count();

        if(deadCountOne >= 2)
            return duoTwo;
        if(deadCountTwo >= 2)
            return duoOne;

        return null;
    }

    public List<Player> getDuo(Player player) {
        return duoOne.contains(player) ? duoOne : duoTwo;
    }
}
