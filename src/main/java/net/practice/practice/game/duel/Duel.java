package net.practice.practice.game.duel;

import lombok.Getter;
import lombok.Setter;
import net.practice.practice.Practice;
import net.practice.practice.game.arena.map.MapLoc;
import net.practice.practice.game.arena.map.MapState;
import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.game.player.Profile;
import net.practice.practice.game.player.data.InventorySnapshot;
import net.practice.practice.game.player.data.PlayerKit;
import net.practice.practice.util.InvUtils;
import net.practice.practice.util.chat.C;
import net.practice.practice.util.itemstack.I;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Duel {

    //@Getter private Arena oldarena;
    @Getter private MapLoc map;
    @Getter private Ladder ladder;
    @Getter private DuelType type;

    @Getter private long startTime, endTime;

    @Getter @Setter private DuelState state;

    @Getter private Set<InventorySnapshot> snapshots;

    @Getter private Map<Player, Integer> missedPots, thrownPots;
    @Getter private Map<Player, List<Integer>> comboes;

    @Getter private List<Profile> spectators;

    private BukkitRunnable countDownTask;
    @Getter private int countDown = 5;

    public Duel(MapLoc map, Ladder ladder, DuelType type) {
        this.map = map;
        this.ladder = ladder;
        this.type = type;

        this.snapshots = new HashSet<>();

        this.missedPots = new HashMap<>();
        this.thrownPots = new HashMap<>();
        this.comboes = new HashMap<>();

        this.spectators = new ArrayList<>();

        this.state = DuelState.STARTING;
    }

    public Duel(Ladder ladder, DuelType type) {
        this(null, ladder, type);
    }

    public void preStart() {
        setState(DuelState.STARTING);
        getMap().setState(MapState.INGAME);

        List<Player> duelers = new ArrayList<>(getPlayers());
        for(Player dueler : duelers) {
            for(Player player : Bukkit.getOnlinePlayers()) {
                if(!getPlayers().contains(player)) {
                    dueler.hidePlayer(player);
                    player.hidePlayer(dueler);
                }
            }
        }

        duelers.forEach(InvUtils::clear);
        duelers.forEach(this::giveKits);
        duelers.stream().map(Profile::getByPlayer).forEach(profile -> {
            profile.setCurrentDuel(this);
        });

        countDownTask = new BukkitRunnable() {
            @Override
            public void run() {
                if(countDown > 0) {
                    sendMessage(C.color("&7Duel starting in &e" + countDown + " &e" + (countDown == 1 ? "second" : "seconds") + "&7."));
                    countDown--;
                } else {
                    start();
                    this.cancel();
                }
            }
        };
        countDownTask.runTaskTimer(Practice.getInstance(), 0, 20L);
    }

    public void start() {
        this.startTime = System.currentTimeMillis();
        setState(DuelState.PLAYING);

        sendMessage(C.color("&aThe match has now started!"));

//        new BukkitRunnable() {
//            public void run() {
//                saveInitialPots();
//            }
//        }.runTaskLaterAsynchronously(Practice.getInstance(), 20L * 5L);
    }

    public void giveKits(Player player) {
        Profile profile = Profile.getByPlayer(player);

        if(!profile.hasCustomKits(ladder)) {
            if(ladder.getDefaultInv() != null)
                ladder.getDefaultInv().apply(player);

            player.sendMessage(C.color("&eYou were given the default " + ladder.getDisplayName() + " &ekit."));
        } else {
            int i = 0;
            for(PlayerKit kit : profile.getCustomKits().get(ladder)) {
                if(kit.getPlayerInv() == null)
                    continue;

                player.getInventory().setItem(i, new I(Material.ENCHANTED_BOOK).name(kit.getName()));
                i++;
            }

            player.getInventory().setItem(8, new I(Material.ENCHANTED_BOOK).name("&e&lDefault " + ladder.getName() + " Kit"));
        }
    }

//    public void saveInitialPots() {
//        getPlayers().forEach(player -> {
//           initialPots.put(player, (int) Arrays.stream(player.getInventory().getContents())
//                   .filter(Objects::nonNull)
//                   .filter(itemStack -> itemStack.getType() == Material.POTION && itemStack.getDurability() == 16421)
//                   .count());
//           Bukkit.broadcastMessage(player.getName() + "'s Pots: " + initialPots.get(player));
//        });
//    }

    public void end(DuelEndReason reason) {
        endTime = System.currentTimeMillis();

        if(getLadder().isBuildable() || getLadder().isSpleef())
            getMap().clean();

        if(countDownTask != null)
            countDownTask.cancel();

        setState(DuelState.ENDED);

        saveInventories();

        for(Player player : getPlayers()) {
            for(Profile profile : getSpectators())
                player.showPlayer(profile.getPlayer());
        }

        getPlayers().stream()
                .filter(Player::isOnline)
                .map(Profile::getByPlayer)
                .forEach(profile -> {
                    profile.setRecentDuel(this);
                });
    }

    public boolean hasSnapshot(Player player) {
        return snapshots.stream()
                .anyMatch(snapshot -> snapshot.getName().equals(player.getName()));
    }

    public InventorySnapshot getSnapshot(Player player) {
        return snapshots.stream()
                .filter(snapshot -> snapshot.getName().equals(player.getName()))
                .findFirst()
                .orElse(null);
    }

    public String getSpectatorMessage() {
        if(spectators.size() == 0)
            return null;

        StringBuilder sb = new StringBuilder();
        for(Profile profile : spectators)
            sb.append(profile.getName()).append((spectators.indexOf(profile) + 1 >= spectators.size() ? "" : ", "));

        return C.color("&bSpectators (" + spectators.size() + "): &7" + sb.toString());
    }

    public void sendMessage(String message) {
        spectators.stream()
                .map(Profile::getPlayer)
                .filter(Player::isOnline)
                .forEach(player -> player.sendMessage(C.color(message)));
    }

    public int getLongestCombo(Player player) {
        Profile.getByPlayer(player).setLongestCombo(0);
        if(!comboes.containsKey(player))
            return 0;

        return Collections.max(comboes.get(player));
    }

    public void kill(Player player) {
        sendMessage("&6" + player.getName() + "&e has died.");
    }

    public void quit(Player player) {
        sendMessage("&6" + player.getName() + "&e has quit.");
    }

    public abstract Collection<Player> getPlayers();

    public abstract boolean hasPlayer(Player player);

    public void saveInventory(Player player) {
        snapshots.add(new InventorySnapshot(player));
    }

    public void saveInventory(UUID uuid) {
        saveInventory(Bukkit.getPlayer(uuid));
    }

    public abstract void saveInventories();
}
