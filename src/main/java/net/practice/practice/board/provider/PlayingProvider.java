package net.practice.practice.board.provider;

import net.practice.practice.board.BoardProvider;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class PlayingProvider implements BoardProvider {

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public List<String> getLines(Player player) {
        return Arrays.asList("hi.");
    }
}
