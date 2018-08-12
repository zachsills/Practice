package land.nub.practice.board;

import land.nub.practice.Practice;
import org.bukkit.entity.Player;

import java.util.List;

public interface BoardProvider {

    Practice plugin = Practice.getInstance();

    String getTitle();

    List<String> getLines(Player player);

}
