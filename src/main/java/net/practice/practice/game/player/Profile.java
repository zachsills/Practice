package net.practice.practice.game.player;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import lombok.Getter;
import lombok.Setter;
import net.practice.practice.Practice;
import net.practice.practice.game.duel.Duel;
import net.practice.practice.game.duel.DuelRequest;
import net.practice.practice.game.duel.DuelType;
import net.practice.practice.game.duel.type.SoloDuel;
import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.game.party.Party;
import net.practice.practice.game.player.data.PlayerInv;
import net.practice.practice.game.player.data.ProfileSetting;
import net.practice.practice.game.player.data.ProfileState;
import net.practice.practice.game.queue.Queue;
import net.practice.practice.inventory.item.ItemStorage;
import net.practice.practice.spawn.SpawnHandler;
import net.practice.practice.util.InvUtils;
import net.practice.practice.util.RankingUtils;
import net.practice.practice.util.chat.C;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class Profile {

    @Getter private static final Map<String, Profile> profiles = new HashMap<>();
    @Getter public static long totalQueueing = 0, totalInGame = 0;

    @Getter private final UUID uuid;

    @Getter private final Map<Ladder, Integer> eloMap;
    @Getter private final Map<ProfileSetting, Object> settings;
    @Getter private final Map<Ladder, List<PlayerInv>> customInvs;

    @Getter private Map<String, DuelRequest> duelRequests;

    @Getter @Setter private Duel currentDuel, recentDuel;
    @Getter @Setter private Queue currentQueue, lastQueue;
    @Getter @Setter private Party party;

    @Getter @Setter private ProfileState state;

    @Getter @Setter private Integer rankedWins = 0, rankedLosses = 0;
    @Getter @Setter private Integer unrankedWins = 0, unrankedLosses = 0;

    public Profile(UUID uuid, boolean cache) {
        this.uuid = uuid;

        this.eloMap = new HashMap<>();
        this.settings = new HashMap<>();
        this.customInvs = new HashMap<>();
        this.duelRequests = new HashMap<>();

        Practice.getInstance().getBackend().loadProfile(this);

        if(cache)
            profiles.putIfAbsent(uuid.toString(), this);
    }

    public Profile(UUID uuid) {
        this(uuid, true);
    }

    public static Profile getByUuid(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);

        return (player != null && player.isOnline()) ? getByPlayer(player) : new Profile(uuid);
    }

    public static Profile getByPlayer(Player player) {
        if(profiles.containsKey(player.getUniqueId().toString()))
            return profiles.get(player.getUniqueId().toString());

        return new Profile(player.getUniqueId());
    }

    public static Profile getRemovedProfile(Player player) {
        return profiles.remove(player.getUniqueId().toString());
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public int getElo(Ladder ladder) {
        if(!eloMap.containsKey(ladder))
            return RankingUtils.STARTING_ELO;

        return eloMap.get(ladder);
    }

    public boolean hasCustomKits(Ladder ladder) {
        if(!customInvs.containsKey(ladder))
            return false;

        return customInvs.get(ladder).isEmpty();
    }

    public Object getSetting(ProfileSetting setting) {
        return settings.getOrDefault(setting, setting.getDefaultValue());
    }

    public boolean isQueueing() {
        return currentQueue != null && state == ProfileState.QUEUING;
    }

    public boolean isInGame() {
        return currentDuel != null && state == ProfileState.PLAYING;
    }

    public void setCurrentDuel(Duel duel) {
        currentDuel = duel;
        if(currentDuel == null) {
            setState(ProfileState.LOBBY);
            return;
        }

        if(getRecentDuel() != null) {
            if(recentDuel.getType() == DuelType.ONE_VS_ONE) {
                SoloDuel soloDuel = (SoloDuel) recentDuel;
                Player opponent = soloDuel.getPlayerOne() == getPlayer() ? soloDuel.getPlayerTwo() : soloDuel.getPlayerOne();
                if(opponent != null)
                    Profile.getByPlayer(opponent).cleanupRecent();
            }
        }

        setState(ProfileState.PLAYING);
    }

    public void setRecentDuel(Duel duel) {
        recentDuel = duel;
        if(duel == null) {
            cleanupRecent();
        }

        getPlayer().setExp(0.0F);
        getPlayer().setLevel(0);
    }

    public void cleanupRecent() {
        if(state == ProfileState.LOBBY) {
            if(getPlayer() != null && getPlayer().getInventory().getItem(3) != null)
                getPlayer().getInventory().setItem(3, null);
        }
    }

    public void sendRematch() {
        if(recentDuel == null) {
            cleanupRecent();
            return;
        }

        if(recentDuel.getType() == DuelType.ONE_VS_ONE) {
            SoloDuel soloDuel = (SoloDuel) recentDuel;
            Player opponent = soloDuel.getPlayerOne() == getPlayer() ? soloDuel.getPlayerTwo() : soloDuel.getPlayerOne();
            if(opponent != null) {
                if(Profile.getByPlayer(opponent).getDuelRequests().containsKey(getPlayer().getName())) {
                    Profile.getByPlayer(opponent).getDuelRequests().get(getPlayer().getName()).accept();
                    return;
                }

                DuelRequest request = new DuelRequest(getPlayer(), opponent, soloDuel.getLadder());
                request.setRematch(true);

                request.sendToRequested();

                cleanupRecent();
                getPlayer().sendMessage(C.color("&aYou have sent a rematch request to " + opponent.getName() + "."));
                return;
            }

            getPlayer().sendMessage(C.color("&cYour opponent was unable to accept a rematch."));
            cleanupRecent();
        }
    }

    public void addToQueue(Queue queue) {
        queue.add(uuid);
        setCurrentQueue(queue);

        setState(ProfileState.QUEUING);

        Player player = getPlayer();
        if(player != null) {
            InvUtils.clear(player);

            player.getInventory().setItem(4, ItemStorage.LEAVE_QUEUE);
        }
    }

    public void removeFromQueue() {
        setLastQueue(getCurrentQueue());

        getCurrentQueue().remove(uuid);
        setCurrentQueue(null);

        setState(ProfileState.LOBBY);
    }

    public void leaveQueue(boolean spawn) {
        leaveQueue(spawn, false);
    }

    public void leaveQueue(boolean spawn, boolean tp) {
        removeFromQueue();

        if(spawn) {
            Player player = getPlayer();
            if(player != null)
                SpawnHandler.spawn(player, tp);
        }
    }

    public void handleKill() {
        Player player = getPlayer();

        player.setGameMode(GameMode.CREATIVE);

        player.setHealth(20.0);
        player.setFoodLevel(20);

        player.getActivePotionEffects().forEach(potionEffect -> player.removePotionEffect(potionEffect.getType()));

        player.setAllowFlight(true);
        player.setFlying(true);
        player.setVelocity(player.getVelocity().setY(1.5F));

        player.getInventory().clear();
        player.getInventory().setArmorContents(null);
    }

    public void save() {
        Practice.getInstance().getBackend().saveProfile(this);
    }

    public void load(Document document) {
        setRankedWins(document.getInteger("rankedWins"));
        setRankedLosses(document.getInteger("rankedLosses"));
        setUnrankedWins(document.getInteger("unrankedWins"));
        setUnrankedLosses(document.getInteger("unrankedLosses"));

        if(document.containsKey("settings")) {
            Document settingsDoc = document.get("settings", Document.class);

            for(String key : settingsDoc.keySet()) {
                Object value = settingsDoc.get(key);
                ProfileSetting type = ProfileSetting.getByKey(key);

               settings.put(type, value);
            }
        }

        for(ProfileSetting setting : ProfileSetting.values())
            settings.putIfAbsent(setting, setting.getDefaultValue());

        if(document.containsKey("elo")) {
            Document eloDoc = document.get("elo", Document.class);

            for(String ladderName : eloDoc.keySet()) {
                Ladder ladder = Ladder.getLadder(ladderName);
                if(ladder == null)
                    continue;

                eloMap.putIfAbsent(ladder, eloDoc.getInteger(ladder));
            }
        }

        if(document.containsKey("inventories")) {
            Document invDoc = document.get("inventories", Document.class);

            for(String ladderName : invDoc.keySet()) {
                Ladder ladder = Ladder.getLadder(ladderName);
                if(ladder == null)
                    continue;

                List<String> invList = (ArrayList<String>) invDoc.get(ladderName, ArrayList.class);
                if(invList.size() <= 0) // Just a safe check
                    continue;

                customInvs.putIfAbsent(ladder, invList.stream()
                                                      .map(InvUtils::invFromString)
                                                      .collect(Collectors.toList()));
            }
        }

        for(Ladder ladder : Ladder.getAllLadders()) {
            eloMap.putIfAbsent(ladder, RankingUtils.STARTING_ELO);
            customInvs.putIfAbsent(ladder, null);
        }
    }

    public Document toDocument() {
        Document document = new Document().append("uuid", uuid);

        BasicDBObject eloStore = new BasicDBObject();
        for(Map.Entry<Ladder, Integer> eloEntry : eloMap.entrySet()) {
            if(eloEntry.getValue().intValue() == RankingUtils.STARTING_ELO) // No need to store if we can already get the value
                continue;

            eloStore.append(eloEntry.getKey().getName(), eloEntry.getValue());
        }

        BasicDBObject settingsStore = new BasicDBObject();
        for(ProfileSetting setting : settings.keySet())
            settingsStore.append(setting.getKey(), settings.get(setting));

        BasicDBObject invStore = new BasicDBObject();
        for(Map.Entry<Ladder, List<PlayerInv>> invEntry : customInvs.entrySet()) {
            if(invEntry.getValue() == null || invEntry.getValue().size() <= 0)
                continue;

            BasicDBList invList = new BasicDBList();
            for(PlayerInv inv : invEntry.getValue())
                invList.add(inv.toString());

            invStore.append(invEntry.getKey().getName(), invList);
        }

        document.append("rankedWins", rankedWins);
        document.append("rankedLosses", rankedLosses);
        document.append("unrankedWins", unrankedWins);
        document.append("unrankedLosses", unrankedLosses);
        if(!eloStore.isEmpty())
            document.append("elo", eloStore);
        if(!settingsStore.isEmpty())
            document.append("settings", settingsStore);
        if(!invStore.isEmpty())
            document.append("inventories", invStore);

        return document;
    }
}
