package net.practice.practice.game.duel;

import lombok.Getter;
import org.bukkit.ChatColor;

public enum DuelType {

    ONE_VS_ONE("One vs. One"),
    TWO_VS_TWO("Two vs. Two"),
    FREE_FOR_ALL("Free For All"),
    TEAM_VS_TEAM("Team vs. Team");

    @Getter private final String friendlyName;

    DuelType(String friendlyName) {
        this.friendlyName = ChatColor.GREEN + friendlyName;
    }
}
