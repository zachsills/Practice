package net.practice.practice.game.player;

import com.mongodb.BasicDBObject;
import lombok.Getter;
import lombok.Setter;
import net.practice.practice.Practice;
import net.practice.practice.game.ladder.Ladder;
import net.practice.practice.game.match.Duel;
import net.practice.practice.game.player.data.ProfileSetting;
import net.practice.practice.game.player.data.ProfileState;
import net.practice.practice.util.RankingUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Profile {

    @Getter private static final Map<String, Profile> profiles = new HashMap<>();

    @Getter private final UUID uuid;

    @Getter private final Map<Ladder, Integer> eloMap;
    @Getter private final Map<ProfileSetting, Object> settings;

    @Getter @Setter private Duel currentDuel;
    @Getter @Setter private ProfileState profileState;

    @Getter @Setter private Integer rankedWins, rankedLosses;
    @Getter @Setter private Integer unrankedWins, unrankedLosses;

    public Profile(UUID uuid, boolean cache) {
        this.uuid = uuid;

        this.eloMap = new HashMap<>();
        this.settings = new HashMap<>();

        Practice.getInstance().getBackend().loadProfile(this);

        if(cache)
            profiles.put(uuid.toString(), this);
    }

    public Profile(UUID uuid) {
        this(uuid, false);
    }

    public static Profile getByUuid(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);

        return player != null ? getByPlayer(player) : new Profile(uuid);
    }

    public static Profile getByPlayer(Player player) {
        return profiles.getOrDefault(player.getUniqueId().toString(), new Profile(player.getUniqueId()));
    }

    public static Profile getRemovedProfile(Player player) {
        return profiles.remove(player.getUniqueId().toString());
    }

    public int getElo(Ladder ladder) {
        if(!eloMap.containsKey(ladder))
            eloMap.put(ladder, RankingUtils.STARTING_ELO);

        return eloMap.get(ladder);
    }

    public Object getSetting(ProfileSetting setting) {
        return settings.getOrDefault(setting, setting.getDefaultValue());
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

        for(Ladder ladder : Ladder.getAllLadders())
            eloMap.putIfAbsent(ladder, RankingUtils.STARTING_ELO);
    }

    public Document toDocument() {
        Document document = new Document();

        document.append("uuid", uuid);

        BasicDBObject eloStore = new BasicDBObject();
        for(Map.Entry<Ladder, Integer> eloEntry : eloMap.entrySet())
            eloStore.append(eloEntry.getKey().getName(), eloEntry.getValue());

        BasicDBObject settingsStore = new BasicDBObject();
        for(ProfileSetting setting : settings.keySet())
            settingsStore.append(setting.getKey(), settings.get(setting));

        document.append("elo", eloStore);
        document.append("settings", settingsStore);
        document.append("rankedWins", rankedWins.intValue());
        document.append("rankedLosses", rankedLosses.intValue());
        document.append("unrankedWins", unrankedWins.intValue());
        document.append("unrankedLosses", unrankedLosses.intValue());

        return document;
    }
}
