package net.practice.practice.command;

import net.practice.practice.Practice;
import net.practice.practice.command.commands.*;
import org.junit.Test;

import java.util.stream.Stream;

public class CommandHandler {

    public static void registerCommands() {
        Stream.of(
                new LadderCommand(),
                new PracticeCommand(),
                //new ArenaCommand(),
                new TestArenaCommand(),
                new SpawnCommand(),
                new StatsCommand(),
                new InventoryCommand(),
                new DuelCommand(),
                new SettingsCommand(),
                new SpectateCommand(),
                new PartyCommand()
        ).forEach(command -> Practice.getInstance().getCommandFramework().registerCommands(command));
    }

    public static void unregisterCommands() {
        Stream.of(
                new LadderCommand(),
                new PracticeCommand(),
                //new ArenaCommand(),
                new TestArenaCommand(),
                new SpawnCommand(),
                new StatsCommand(),
                new InventoryCommand(),
                new DuelCommand(),
                new SettingsCommand(),
                new SpectateCommand(),
                new PartyCommand()
        ).forEach(command -> Practice.getInstance().getCommandFramework().unregisterCommands(command));
    }
}
