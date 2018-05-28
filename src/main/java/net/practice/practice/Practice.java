package net.practice.practice;

import lombok.Getter;
import net.practice.practice.board.BoardManager;
import net.practice.practice.board.provilder.ProviderResolver;
import net.practice.practice.game.arena.ArenaManager;
import net.practice.practice.game.ladder.LadderManager;
import net.practice.practice.listener.ListenerHandler;
import net.practice.practice.storage.MongoBackend;
import net.practice.practice.util.command.CommandFramework;
import org.bukkit.plugin.java.JavaPlugin;

public class Practice extends JavaPlugin {

    @Getter private static Practice instance;

    @Getter private MongoBackend backend;

    @Getter private BoardManager boardManager;
    @Getter private ArenaManager arenaManager;
    @Getter private LadderManager ladderManager;

    @Getter private CommandFramework commandFramework;

    @Override
    public void onEnable() {
        instance = this;

        /* Initialize storage */
        saveDefaultConfig();

        backend = new MongoBackend(this);

        /* Initialize managers */
        boardManager = new BoardManager(new ProviderResolver());

        arenaManager = new ArenaManager();
        arenaManager.loadArenas();

        ladderManager = new LadderManager();
        ladderManager.loadLadders();

        /* Commands and Listeners */
        commandFramework = new CommandFramework(this);

        ListenerHandler.initListeners();
    }

    @Override
    public void onDisable() {
        saveConfig();

        backend.saveProfiles();

        arenaManager.saveArenas();
        ladderManager.saveLadders();

        backend.close();
    }
}
