package land.nub.practice;

import com.bizarrealex.azazel.Azazel;
import land.nub.practice.autobroadcast.AutoBroadcastFile;
import land.nub.practice.board.BoardManager;
import land.nub.practice.board.provider.ProviderResolver;
import land.nub.practice.game.arena.ArenaManager;
import land.nub.practice.game.arena.map.MapLoc;
import land.nub.practice.game.arena.map.MapManager;
import land.nub.practice.game.ladder.LadderManager;
import land.nub.practice.game.party.PartyManager;
import land.nub.practice.game.player.Profile;
import land.nub.practice.game.queue.QueueRunnable;
import land.nub.practice.listener.ListenerHandler;
import land.nub.practice.storage.backend.MongoBackend;
import land.nub.practice.tab.PracticeTabAdapter;
import land.nub.practice.task.AutoBroadcastTask;
import land.nub.practice.task.CleanerTask;
import land.nub.practice.task.LeaderboardTask;
import land.nub.practice.task.UpdateInventoryTask;
import land.nub.practice.util.LocUtils;
import land.nub.practice.util.command.CommandFramework;
import lombok.Getter;
import land.nub.practice.command.CommandHandler;
import land.nub.practice.spawn.SpawnHandler;
import land.nub.practice.storage.file.BuycraftFile;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Practice extends JavaPlugin {

    @Getter private static Practice instance;

    @Getter private MongoBackend backend;

    @Getter private AutoBroadcastFile autoBroadcastFile;
    @Getter private BuycraftFile buycraftFile;

    @Getter private BoardManager boardManager;
    @Getter private ArenaManager arenaManager;
    @Getter private LadderManager ladderManager;
    @Getter private MapManager mapManager;

    @Getter private CommandFramework commandFramework;

    @Getter private Location spawn, editor;

    @Override
    public void onEnable() {
        instance = this;

        /* Initialize storage */
        saveDefaultConfig();
        autoBroadcastFile = new AutoBroadcastFile();

        backend = new MongoBackend(this);

        /* Map world loading (This must be done before arenas are loaded because otherwise, the arenas will load in with world as null) */
        MapLoc.loadWorld();

        /* Initialize managers */
        boardManager = new BoardManager(new ProviderResolver());
        boardManager.setupAll();
        //getServer().getPluginManager().registerEvents(boardManager, this);
        arenaManager = new ArenaManager();
        ladderManager = new LadderManager();
        mapManager = new MapManager();

        /* Commands and Listeners */
        commandFramework = new CommandFramework(this);

        CommandHandler.registerCommands();
        ListenerHandler.registerListeners();

        getServer().getOnlinePlayers().forEach(SpawnHandler::spawn);

        /* Tasks */
        new QueueRunnable().runTaskTimer(this, 20L, 20L);
        new CleanerTask().runTaskTimerAsynchronously(this, 20L, 5L * 20L);
        new LeaderboardTask().runTaskTimerAsynchronously(this, 100L, 180L * 20L);
        new UpdateInventoryTask().runTaskTimerAsynchronously(this, 20L, 30L);
        if(getAutoBroadcastFile().isEnabled()) {
            int seconds = getAutoBroadcastFile().getSeconds();
            new AutoBroadcastTask().runTaskTimerAsynchronously(this, 20L * seconds, 20L * seconds);
        }

        new Azazel(this, new PracticeTabAdapter());
    }

    @Override
    public void onDisable() {
        CommandHandler.unregisterCommands();

        boardManager.onDisable();

        arenaManager.saveArenas();
        ladderManager.saveLadders();
        mapManager.saveMaps();

        backend.saveProfiles();
        backend.close();

        Profile.getProfiles().clear();
        PartyManager.getParties().clear();

        for (Player player : Bukkit.getOnlinePlayers()) { // This is because if people are in the arena world when we stop, it won't unload it below.
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        }

        getServer().unloadWorld(MapLoc.getArenaWorld(), false);
    }

    public Location getSpawn() {
        if (spawn == null) {
            String configLoc = getConfig().getString("locations.spawn");
            if (configLoc != null) {
                return spawn = LocUtils.deserializeLocation(getConfig().getString("locations.spawn"));
            }
        } else {
            return spawn;
        }
        return null;
    }

    public Location getEditor() {
        if (editor == null) {
            String configLoc = getConfig().getString("locations.editor");
            if (configLoc != null) {
                return editor = LocUtils.deserializeLocation(getConfig().getString("locations.editor"));
            }
        } else {
            return editor;
        }
        return null;
    }

    public void setSpawn(Location location) {
        spawn = location;

        getConfig().set("locations.spawn", LocUtils.serializeLocation(location));
        saveConfig();
    }

    public void setEditor(Location location) {
        editor = location;

        getConfig().set("locations.editor", LocUtils.serializeLocation(location));
        saveConfig();
    }
}
