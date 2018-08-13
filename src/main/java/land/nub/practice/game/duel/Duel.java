package land.nub.practice.game.duel;

import land.nub.practice.Practice;
import land.nub.practice.game.arena.map.MapLoc;
import land.nub.practice.game.arena.map.MapState;
import land.nub.practice.game.ladder.Ladder;
import land.nub.practice.game.player.Profile;
import land.nub.practice.game.player.data.PlayerKit;
import land.nub.practice.util.InvUtils;
import land.nub.practice.util.RunnableShorthand;
import land.nub.practice.util.chat.C;
import land.nub.practice.util.itemstack.I;
import lombok.Getter;
import lombok.Setter;
import land.nub.practice.game.player.data.InventorySnapshot;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class Duel {

    @Getter private MapLoc map;
    @Getter private Ladder ladder;
    @Getter private DuelType type;

    @Getter private long startTime, endTime;

    @Getter @Setter private DuelState state;

    @Getter private Set<InventorySnapshot> snapshots;

    @Getter private Map<Player, Integer> missedPots, thrownPots;
    @Getter private Map<Player, List<Integer>> comboes;

    @Getter private List<Profile> spectators;

    @Getter private BukkitRunnable countDownTask;
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
        for (Player nonDueler : Bukkit.getOnlinePlayers()) {
            if (duelers.contains(nonDueler)) continue;
            for (Player dueler : duelers) {
                dueler.hidePlayer(nonDueler);
                nonDueler.hidePlayer(dueler);
            }
        }
        for (Player dueler : duelers) {
            for (Player otherDueler : duelers) {
                if (dueler.getUniqueId().equals(otherDueler.getUniqueId())) continue;
                dueler.showPlayer(otherDueler);
                otherDueler.showPlayer(dueler);
            }
        }

        duelers.forEach(InvUtils::clear);
        duelers.forEach(player -> {
            player.setAllowFlight(false);
            player.setFlying(false);
        });
        duelers.stream().map(Profile::getByPlayer).forEach(profile -> {
            profile.setCurrentDuel(this);
        });

        countDownTask = new BukkitRunnable() {
            @Override
            public void run() {
                if(countDown > 0) {
                    sendMessage(C.color("&7Duel starting in &e" + countDown + " &e" + (countDown == 1 ? "second" : "seconds") + "&7."));
                    duelers.forEach(player -> {
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1f, 1f);
                    });
                    countDown--;
                } else {
                    start();
                    duelers.forEach(player -> {
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 2f, 2f);
                    });
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

        if (ladder.getName().contains("Gapple")) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999, 1));
            player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 9999, 0));
        }

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
//           initialPots.put(player, (int) Arrays.stream(player.getSoloRanked().getContents())
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
        saveInventory(player.getUniqueId());
        sendMessage("&6" + player.getName() + "&e has died.");
    }

    public void quit(Player player) {
        saveInventory(player.getUniqueId());
        sendMessage("&6" + player.getName() + "&e has quit.");
    }

    public List<Player> getAllPlayers() {
        return Stream.concat(getPlayers().stream(), getSpectators().stream().map(Profile::getPlayer))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public void initialTeleport(Player player, Location location) {
        player.teleport(location);
        giveKits(player);
    }

    public abstract boolean canHit(Player playerOne, Player playerTwo);

    public abstract Collection<Player> getPlayers();

    public abstract boolean hasPlayer(Player player);

    public void saveInventory(Player player) {
        snapshots.add(new InventorySnapshot(player));
    }

    public void saveInventory(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null && player.isOnline()) {
            saveInventory(Bukkit.getPlayer(uuid));
        }
    }

    public abstract void saveInventories();
}
