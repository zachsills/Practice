package net.practice.practice.board.provider;

import net.practice.practice.board.BoardProvider;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class SpectatingProvider implements BoardProvider {

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public List<String> getLines(Player player) {
        return Collections.emptyList();
    }
}
