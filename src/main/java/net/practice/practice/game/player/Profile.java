package net.practice.practice.game.player;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import lombok.Getter;
import lombok.Setter;
import net.practice.practice.Practice;
import net.practice.practice.game.duel.Duel;
import net.practice.practice.game.ladder.Ladder;
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

    @Getter @Setter private Duel currentDuel, recentDuel;
    @Getter @Setter private Queue currentQueue;
    @Getter @Setter private ProfileState profileState;

    @Getter @Setter private Integer rankedWins = 0, rankedLosses = 0;
    @Getter @Setter private Integer unrankedWins = 0, unrankedLosses = 0;

    public Profile(UUID uuid, boolean cache) {
        this.uuid = uuid;

        this.eloMap = new HashMap<>();
        this.settings = new HashMap<>();
        this.customInvs = new HashMap<>();

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
            eloMap.put(ladder, RankingUtils.STARTING_ELO);

        return eloMap.get(ladder);
    }

    public Object getSetting(ProfileSetting setting) {
        return settings.getOrDefault(setting, setting.getDefaultValue());
    }

    public boolean isQueueing() {
        return currentQueue != null && profileState == ProfileState.QUEUING;
    }

    public boolean isInGame() {
        return currentDuel != null && profileState == ProfileState.PLAYING;
    }

    public void addToQueue(Queue queue) {
        queue.add(uuid);
        setCurrentQueue(queue);

        setProfileState(ProfileState.QUEUING);

        Player player = getPlayer();
        if(player != null) {
            InvUtils.clear(player);

            player.getInventory().setItem(4, ItemStorage.LEAVE_QUEUE);
        }
    }

    public void removeFromQueue() {
        getCurrentQueue().remove(uuid);
        setCurrentQueue(null);

        setProfileState(ProfileState.LOBBY);
    }

    public void leaveQueue(boolean spawn) {
        leaveQueue(spawn, false);
    }

    public void leaveQueue(boolean spawn, boolean tp) {
        if(spawn) {
            Player player = getPlayer();
            if(player != null) {
                SpawnHandler.spawn(player, tp);
                player.sendMessage(C.color("&f\u00BB &eLeft the queue for " + getCurrentQueue().getLadder().getDisplayName() + "."));
            }
        }

        removeFromQueue();
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
        Document document = new Document();

        document.append("uuid", uuid);

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
