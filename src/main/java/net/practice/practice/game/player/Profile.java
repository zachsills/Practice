package net.practice.practice.game.player;

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
import net.practice.practice.game.party.PartyManager;
import net.practice.practice.game.player.data.PlayerKit;
import net.practice.practice.game.player.data.ProfileSetting;
import net.practice.practice.game.player.data.ProfileState;
import net.practice.practice.game.queue.Queue;
import net.practice.practice.inventory.item.ItemStorage;
import net.practice.practice.spawn.PartyHandler;
import net.practice.practice.spawn.SpawnHandler;
import net.practice.practice.util.InvUtils;
import net.practice.practice.util.RankingUtils;
import net.practice.practice.util.RunnableShorthand;
import net.practice.practice.util.chat.C;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

public class Profile {

    @Getter private static final Map<String, Profile> profiles = new HashMap<>();
    @Getter public static long totalQueueing = 0, totalInGame = 0;

    @Getter private final UUID uuid;

    @Getter @Setter private String name;

    @Getter private final Map<Ladder, Integer> eloMap;
    @Getter private final Map<ProfileSetting, Object> settings;
    @Getter private final Map<Ladder, List<PlayerKit>> customKits;

    @Getter private Map<String, DuelRequest> duelRequests;

    @Getter @Setter private Duel currentDuel, recentDuel, spectating;
    @Getter @Setter private Queue currentQueue, lastQueue;
    @Getter @Setter private Party party;
    @Getter @Setter private Ladder editing;
    @Getter @Setter private int longestCombo;

    @Getter @Setter private ProfileState state;

    @Getter @Setter private Integer rankedWins = 0, rankedLosses = 0;
    @Getter @Setter private Integer unrankedWins = 0, unrankedLosses = 0;

    public Profile(UUID uuid, boolean cache) {
        this.uuid = uuid;

        this.eloMap = new HashMap<>();
        this.settings = new HashMap<>();
        this.customKits = new HashMap<>();
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

    public void sendMessage(String message) {
        getPlayer().sendMessage(C.color(message));
    }

    public int getElo(Ladder ladder) {
        if(!eloMap.containsKey(ladder))
            return RankingUtils.STARTING_ELO;

        return eloMap.get(ladder);
    }

    public boolean hasCustomKits(Ladder ladder) {
        if(!customKits.containsKey(ladder))
            return false;

        return customKits.get(ladder) != null && !customKits.get(ladder).isEmpty() && hasKit(customKits.get(ladder));
    }

    private static boolean hasKit(List<PlayerKit> playerKits) {
        return playerKits.stream()
                .anyMatch(PlayerKit::hasKit);
    }

    public Object getSetting(ProfileSetting setting) {
        if(!settings.containsKey(setting))
            return setting.getDefaultValue();

        return settings.get(setting);
    }

    public boolean isQueueing() {
        return currentQueue != null && state == ProfileState.QUEUING;
    }

    public boolean isInLobby() {
        return state == ProfileState.LOBBY || state == ProfileState.EDITING || state == ProfileState.QUEUING;
    }

    public boolean isInGame() {
        return currentDuel != null && state == ProfileState.PLAYING;
    }

    public boolean isSpectating() {
        return spectating != null && state == ProfileState.SPECTATING;
    }

    public boolean isInParty() {
        return party != null;
    }

    public void setCurrentDuel(Duel duel) {
        currentDuel = duel;
        duelRequests.clear();

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

    public void setSpectating(Duel spectating) {
        setSpectating(spectating, true);
    }

    public void setSpectating(Duel spectating, boolean sendMsg) {
        if(spectating == null) {
            if(this.spectating != null)
                this.spectating.getSpectators().remove(this);

            SpawnHandler.spawn(getPlayer());
            this.spectating = null;
            return;
        }

        this.spectating = spectating;

        InvUtils.clear(getPlayer());

        getPlayer().setGameMode(GameMode.CREATIVE);
        getPlayer().setAllowFlight(true);
        getPlayer().setFlying(true);

        setState(ProfileState.SPECTATING);
//        spectating.getSpectators().add(this);

        for (Player player : spectating.getPlayers()) {
            player.hidePlayer(getPlayer());
            getPlayer().showPlayer(player);
        }

        PlayerInventory inventory = getPlayer().getInventory();
        inventory.setItem(0, ItemStorage.SPECTATOR_INVENTORY);
        inventory.setItem(1, ItemStorage.SPECTATOR_INFO);
        inventory.setItem(8, ItemStorage.SPECTATOR_LEAVE);

        if (sendMsg)
            spectating.sendMessage("&b" + getName() + " &eis now spectating.");
    }

    public void cleanupRecent() {
        if(state == ProfileState.LOBBY) {
            if(getPlayer() != null && getPlayer().getInventory().getItem(2) != null && getPlayer().getInventory().getItem(2).getType() == Material.INK_SACK)
                getPlayer().getInventory().setItem(2, null);
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

                DuelRequest request = new DuelRequest(getPlayer(), opponent, recentDuel.getLadder(), false);
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

    public void handleDeath() { // TODO: Move to duel
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

    public void createParty() {
        setParty(Party.createParty(getPlayer()));

        RunnableShorthand.runNextTick(() -> {
            SpawnHandler.spawn(getPlayer(), false);
        });

        getPlayer().sendMessage(C.color("&aYou have successfully created a party."));
    }

    public void joinParty(Party party) {
        setParty(party);

        PartyHandler.spawn(getPlayer(), party.getLeader().equals(getUuid()));
    }

    public void leaveParty() {
        party.getPlayers().remove(getPlayer());

        setParty(null);

        SpawnHandler.spawn(getPlayer(), false);
    }

    public void handleLeaveParty() {
        if(party.getCurrentQueue() != null)
            party.leaveQueue();

        if(party.getLeader().equals(uuid)) {
            if(party.getPlayers().size() > 0) {
                UUID newLeader = party.getPlayers().get(0).getUniqueId();
                party.getPlayers().remove(0);

                Bukkit.getPlayer(newLeader).sendMessage(C.color("&aYou have been made the leader of the party."));
                party.sendMessage("&b" + Bukkit.getPlayer(newLeader).getName() + " &ehas been made the leader of your party.");

                party.setLeader(newLeader);
                PartyHandler.spawn(Bukkit.getPlayer(newLeader), true);
            } else {
                PartyManager.removeParty(party);
            }
            party.sendMessage("&b" + getName() + " &ehas left the party.");
            leaveParty();

            sendMessage("&aYou have left your party.");
            return;
        }

        party.getPlayers().remove(getPlayer());
        party.sendMessage("&b" + getName() + " &ehas left the party.");

        leaveParty();
        sendMessage("&aYou have left your party.");
    }

    public void sendPartyInfo() {
        sendPartyInfo(getParty());
    }

    public void sendPartyInfo(Party party) {
        sendMessage(C.color("&f&m---------------------------------"));
        sendMessage(C.color("&6" + Bukkit.getPlayer(party.getLeader()).getName() + "&e's Party (" + party.getSize() + "): "));
        if(party.getSize() == 1) {
            sendMessage(C.color("&e  * No Members *"));
        } else {
            StringBuilder builder = new StringBuilder("&e  ");
            List<Player> players = party.getPlayers();
            for(int i = 0; i < players.size(); i++)
                builder.append(players.get(i).getName()).append(i + 1 >= players.size() ? "" : "&7,&e ");
            sendMessage(C.color(builder.toString()));
        }
        sendMessage(C.color("&f&m---------------------------------"));
    }

    public void openSettings() {
        Inventory inventory = Bukkit.createInventory(getPlayer(), 9, C.color("&6Settings"));

        int i = 1;
        for(ProfileSetting setting : ProfileSetting.values()) {
            inventory.setItem(i, ProfileSetting.getSettingItem(setting, getSetting(setting)));

            i += 2;
        }

        getPlayer().openInventory(inventory);
    }

    public void toggleSetting(ProfileSetting setting) {
        Object nextOption = ProfileSetting.getNextOption(setting, getSetting(setting));
        settings.put(setting, nextOption);

        ProfileSetting.toggleFor(getPlayer(), setting, nextOption);
        getPlayer().playSound(getPlayer().getLocation(), Sound.NOTE_PLING, 0.7F, 1.4F);
    }

    public void beginEditing(Ladder ladder) {
        editing = ladder;
        setState(ProfileState.EDITING);

        InvUtils.clear(getPlayer());

        getPlayer().teleport(Practice.getInstance().getEditor());

        if(ladder.getDefaultInv() != null)
            ladder.getDefaultInv().apply(getPlayer());
    }

    public void stopEditing() {
        SpawnHandler.spawn(getPlayer());
    }

    public void save() {
        Practice.getInstance().getBackend().saveProfile(this);
    }

    public void load(Document document) {
        customKits.clear();
        eloMap.clear();

        setRankedWins(document.getInteger("rankedWins"));
        setRankedLosses(document.getInteger("rankedLosses"));
        setUnrankedWins(document.getInteger("unrankedWins"));
        setUnrankedLosses(document.getInteger("unrankedLosses"));

        if(document.containsKey("name"))
            setName(document.getString("name"));

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

                eloMap.putIfAbsent(ladder, eloDoc.getInteger(ladderName));
            }
        }

        if(document.containsKey("kits")) {
            Document invDoc = document.get("kits", Document.class);

            for(String ladderName : invDoc.keySet()) {
                Ladder ladder = Ladder.getLadder(ladderName);
                if(ladder == null)
                    continue;

                Document kitDoc = invDoc.get(ladderName, Document.class);
                if(kitDoc.size() <= 0) // Just a safe check
                    continue;

                List<PlayerKit> playerKits = new ArrayList<>();
                for(String key : kitDoc.keySet()) {
                    Document kitStore = kitDoc.get(key, Document.class);
                    PlayerKit kit = new PlayerKit(kitStore.getString("name"));
                    if(kitStore.containsKey("inv"))
                        kit.setPlayerInv(InvUtils.invFromString(kitStore.getString("inv")));

                    playerKits.add(kit);
                }

                if(!playerKits.isEmpty()) {
                    customKits.put(ladder, playerKits);
                } else {
                    List<PlayerKit> kits = new ArrayList<>();
                    for(int i = 0; i < 5; i++)
                        kits.add(new PlayerKit("&b&lKit " + (i + 1)));

                    customKits.put(ladder, kits);
                }
            }
        }

        for(Ladder ladder : Ladder.getLadders().values()) {
            if(customKits.containsKey(ladder))
                continue;

            List<PlayerKit> kits = new ArrayList<>();
            for(int i = 0; i < 5; i++)
                kits.add(new PlayerKit("&b&lKit " + (i + 1)));

            customKits.putIfAbsent(ladder, kits);
        }
    }

    public Document toDocument() {
        Document document = new Document().append("uuid", uuid.toString());

        BasicDBObject eloStore = new BasicDBObject();
        for(Map.Entry<Ladder, Integer> eloEntry : eloMap.entrySet()) {
            if(eloEntry.getValue().intValue() == RankingUtils.STARTING_ELO) // No need to store if we can already get the value
                continue;

            eloStore.append(eloEntry.getKey().getName(), eloEntry.getValue());
        }

        BasicDBObject settingsStore = new BasicDBObject();
        for(ProfileSetting setting : settings.keySet())
            settingsStore.append(setting.getKey(), settings.get(setting));

        BasicDBObject kitStore = new BasicDBObject();
        for(Map.Entry<Ladder, List<PlayerKit>> invEntry : customKits.entrySet()) {
            if(invEntry.getValue() == null || invEntry.getValue().size() <= 0)
                continue;

            BasicDBObject kitDocument = new BasicDBObject();
            int i = 1;
            for(PlayerKit kit : invEntry.getValue()) {
//                kitDocument.append("name", kit.getName());
//                kitDocument.append("inv", (kit.getPlayerInv() != null) ? kit.getPlayerInv().toString() : null);
                BasicDBObject object = new BasicDBObject();
                object.append("name", kit.getName());
                object.append("inv", (kit.getPlayerInv() != null) ? kit.getPlayerInv().toString() : null);

                kitDocument.append(i + "", object);
                i += 1;
            }

            kitStore.append(invEntry.getKey().getName(), kitDocument);
        }

        if(name != null)
            document.append("name", name);
        document.append("rankedWins", rankedWins);
        document.append("rankedLosses", rankedLosses);
        document.append("unrankedWins", unrankedWins);
        document.append("unrankedLosses", unrankedLosses);
        if(!eloStore.isEmpty())
            document.append("elo", eloStore);
        if(!settingsStore.isEmpty())
            document.append("settings", settingsStore);
        if(!kitStore.isEmpty())
            document.append("kits", kitStore);

        return document;
    }
}
