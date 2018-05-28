package net.practice.practice.board.provilder;

import net.practice.practice.board.BoardProvider;
import net.practice.practice.util.ConfigValues;
import net.practice.practice.util.chat.C;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class PlayingProvider implements BoardProvider {

    @Override
    public String getTitle() {
        return C.color(ConfigValues.SCOREBOARD_TITLE);
    }

    @Override
    public List<String> getLines(Player player) {
        return Arrays.asList("hi.");
    }
}
