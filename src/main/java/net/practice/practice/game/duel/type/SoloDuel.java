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
import net.practice.practice.util.InvUtils;
import net.practice.practice.util.chat.C;
import net.practice.practice.util.chat.JsonMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SoloDuel extends Duel {

    @Getter private final Player playerOne, playerTwo;

    @Getter @Setter private Player winner;

    public SoloDuel(Arena arena, Ladder ladder, Player playerOne, Player playerTwo) {
        super(arena, ladder, DuelType.ONE_VS_ONE);

        this.playerOne = playerOne;
        this.playerTwo = playerTwo;
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
    }

    @Override
    public void start() {
        super.start();
    }

    @Override
    public void end(DuelEndReason reason) {
        super.end(reason);

        if(reason == DuelEndReason.DIED)
            sendMessage("&6" + winner.getName() + " &ehas won the match.");
        else
            sendMessage("&6" + winner.getName() + " &ehas forcefully won through forfeit.");

        Profile profileOne = Profile.getByPlayer(playerOne);
        Profile profileTwo = Profile.getByPlayer(playerTwo);
        profileOne.setRecentDuel(this);
        profileTwo.setRecentDuel(this);

        if(playerOne == winner)
            new JsonMessage().append(ChatColor.YELLOW + "Inventories: ").save()
                    .append(ChatColor.GREEN + playerOne.getName()).setClickAsExecuteCmd("/inv " + playerOne.getName()).save()
                    .append(ChatColor.GRAY + " or ").save()
                    .append(ChatColor.RED + playerTwo.getName()).setClickAsExecuteCmd("/inv " + playerTwo.getName()).save()
                    .send(playerOne, playerTwo);
        else
            new JsonMessage().append(ChatColor.YELLOW + "Inventories: ").save()
                    .append(ChatColor.GREEN + playerTwo.getName()).setClickAsExecuteCmd("/inv " + playerTwo.getName()).save()
                    .append(ChatColor.GRAY + " or ").save()
                    .append(ChatColor.RED + playerOne.getName()).setClickAsExecuteCmd("/inv " + playerOne.getName()).save()
                    .send(playerOne, playerTwo);

        new BukkitRunnable() {
            @Override
            public void run() {
                if(playerOne.isOnline())
                    SpawnHandler.spawn(playerOne, true);

                if(playerTwo.isOnline())
                    SpawnHandler.spawn(playerTwo, true);
            }
        }.runTaskLater(Practice.getInstance(), 100L);
    }

    @Override
    public void sendMessage(String message) {
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
