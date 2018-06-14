package net.practice.practice.game.duel.type;

import lombok.Getter;
import lombok.Setter;
import net.practice.practice.Practice;
import net.practice.practice.game.arena.Arena;
import net.practice.practice.game.duel.Duel;
import net.practice.practice.game.duel.DuelEndReason;
import net.practice.practice.game.duel.DuelType;
import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.game.player.Profile;
import net.practice.practice.spawn.SpawnHandler;
import net.practice.practice.util.EloUtils;
import net.practice.practice.util.InvUtils;
import net.practice.practice.util.chat.C;
import net.practice.practice.util.chat.JsonMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SoloDuel extends Duel {

    @Getter private final Player playerOne, playerTwo;

    @Getter @Setter private Player winner;

    @Getter private boolean ranked;

    public SoloDuel(Arena arena, Ladder ladder, Player playerOne, Player playerTwo, boolean ranked) {
        super(arena, ladder, DuelType.ONE_VS_ONE);

        this.playerOne = playerOne;
        this.playerTwo = playerTwo;

        this.ranked = ranked;
    }

    public SoloDuel(Arena arena, Ladder ladder, Player playerOne, Player playerTwo) {
        this(arena, ladder, playerOne, playerTwo, false);
    }

    @Override
    public void preStart() {
        super.preStart();

        InvUtils.clear(playerOne);
        InvUtils.clear(playerTwo);

        playerOne.teleport(getArena().getSpawnOne());
        playerTwo.teleport(getArena().getSpawnTwo());

        Profile profileOne = Profile.getByPlayer(playerOne);
        Profile profileTwo = Profile.getByPlayer(playerTwo);
        profileOne.setCurrentDuel(this);
        profileTwo.setCurrentDuel(this);

        giveKits(playerOne);
        giveKits(playerTwo);

        Profile.totalInGame += 2;
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void end(DuelEndReason reason) {
        super.end(reason);

        sendMessage("&f&m---------------------------------");
        sendMessage("&6Winner: &e" + winner.getName());

        Profile winnerProfile = Profile.getByPlayer(winner);
        Profile loserProfile = Profile.getByPlayer(getLoser());
        winnerProfile.setRecentDuel(this);
        loserProfile.setRecentDuel(this);

        if(ranked) {
            winnerProfile.setRankedWins(winnerProfile.getRankedWins() + 1);
            loserProfile.setRankedLosses(loserProfile.getRankedLosses() + 1);

            int eloChange = handleElo(winnerProfile, loserProfile, getLadder());
            sendMessage("&6Elo Changes: &e" + winner.getName() + " &7(&a+" + eloChange + "&7) &6- &e" + getLoser().getName() + " &7(&c-" + eloChange + "&7)");
        } else {
            winnerProfile.setUnrankedWins(winnerProfile.getUnrankedWins() + 1);
            loserProfile.setUnrankedLosses(loserProfile.getUnrankedLosses() + 1);
        }

        new JsonMessage().append(ChatColor.GOLD + "Inventories " + ChatColor.GRAY + "(Click to view) ").save()
                .append(ChatColor.GREEN + winner.getName()).setClickAsExecuteCmd("/_ " + winner.getName()).setHoverAsTooltip(ChatColor.GREEN + winner.getName() + "'s Inventory").save()
                .append(ChatColor.GRAY + " - ").save()
                .append(ChatColor.RED + getLoser().getName()).setClickAsExecuteCmd("/_ " + getLoser().getName()).setHoverAsTooltip(ChatColor.RED + getLoser().getName() + "'s Inventory").save()
                .send(playerOne, playerTwo);

        String spectatorMessage = getSpectatorMessage();
        if(spectatorMessage != null)
            sendMessage(spectatorMessage);

        sendMessage("&f&m---------------------------------");

        new BukkitRunnable() {
            @Override
            public void run() {
                if(playerOne.isOnline())
                    SpawnHandler.spawn(playerOne, true);

                if(playerTwo.isOnline())
                    SpawnHandler.spawn(playerTwo, true);

                Profile.totalInGame -= 2;
            }
        }.runTaskLater(Practice.getInstance(), 100L);
    }

    private Player getLoser() {
        return playerOne == winner ? playerTwo : playerOne;
    }

    private int handleElo(Profile winnerProfile, Profile loserProfile, Ladder ladder) {
        int winnerNewElo = EloUtils.getNewRating(winnerProfile.getElo(ladder), loserProfile.getElo(ladder), 1);
        int loserNewElo = EloUtils.getNewRating(loserProfile.getElo(ladder), winnerProfile.getElo(ladder), 0);

        int winnderOldElo = winnerProfile.getElo(ladder);

        winnerProfile.getEloMap().put(ladder, winnerNewElo);
        loserProfile.getEloMap().put(ladder, loserNewElo);

        return (winnerNewElo - winnderOldElo);
    }

    @Override
    public void sendMessage(String message) {
        super.sendMessage(message);

        playerOne.sendMessage(C.color(message));
        playerTwo.sendMessage(C.color(message));
    }

    @Override
    public void kill(Player player) {
        setWinner(playerOne != player ? playerOne : playerTwo);
        saveInventory(winner.getUniqueId());

        end(DuelEndReason.DIED);
    }

    @Override
    public void quit(Player player) {
        setWinner(playerOne != player ? playerOne : playerTwo);
        end(DuelEndReason.QUIT);
    }

    @Override
    public boolean hasPlayer(Player player) {
        return player.getUniqueId().toString().equals(getPlayerOne().getUniqueId().toString()) || player.getUniqueId().toString().equals(getPlayerTwo().getUniqueId().toString());
    }

    @Override
    public void saveInventories() {
        if(!hasSnapshot(playerOne))
            saveInventory(playerOne.getUniqueId());

        if(!hasSnapshot(playerTwo))
            saveInventory(playerTwo.getUniqueId());
    }
}
