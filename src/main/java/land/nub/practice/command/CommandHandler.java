package land.nub.practice.command;

import land.nub.practice.Practice;
import land.nub.practice.command.commands.*;

import java.util.stream.Stream;

public class CommandHandler {

    public static void registerCommands() {
        Stream.of(
                new LadderCommand(),
                new PracticeCommand(),
                new ArenaCommand(),
                new SpawnCommand(),
                new StatsCommand(),
                new InventoryCommand(),
                new DuelCommand(),
                new SettingsCommand(),
                new SpectateCommand(),
                new PartyCommand(),
                new MapCommand(),
                new FlyCommand(),
                new CosmeticCommand()
        ).forEach(command -> Practice.getInstance().getCommandFramework().registerCommands(command));
    }

    public static void unregisterCommands() {
        Stream.of(
                new LadderCommand(),
                new PracticeCommand(),
                new ArenaCommand(),
                new SpawnCommand(),
                new StatsCommand(),
                new InventoryCommand(),
                new DuelCommand(),
                new SettingsCommand(),
                new SpectateCommand(),
                new PartyCommand(),
                new MapCommand(),
                new FlyCommand(),
                new CosmeticCommand()
        ).forEach(command -> Practice.getInstance().getCommandFramework().unregisterCommands(command));
    }
}
