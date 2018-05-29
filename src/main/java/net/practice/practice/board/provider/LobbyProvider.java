package net.practice.practice.board.provider;

import net.practice.practice.board.BoardProvider;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LobbyProvider implements BoardProvider {

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public List<String> getLines(Player player) {
        List<String> lines = new ArrayList<>();

        lines.add("&7Online: &6" + Bukkit.getOnlinePlayers().size());
        lines.add("&7In Queue: &6" + 0);

        return lines;
    }
}
