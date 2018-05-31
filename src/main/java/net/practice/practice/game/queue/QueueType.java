package net.practice.practice.game.queue;

import lombok.Getter;
import org.bukkit.ChatColor;

public enum QueueType {

    ONE_VS_ONE("One vs. One"),
    TWO_VS_TWO("Two vs. Two"),
    TEAM_VS_TEAM("Team vs. Team");

    @Getter
    private final String friendlyName;

    QueueType(String friendlyName) {
        this.friendlyName = ChatColor.GREEN + friendlyName;
    }
}
