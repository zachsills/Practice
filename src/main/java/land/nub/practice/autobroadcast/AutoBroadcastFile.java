package net.practice.practice.autobroadcast;

import net.practice.practice.util.file.Configuration;

import java.util.List;

public class AutoBroadcastFile extends Configuration {

    public AutoBroadcastFile() {
        super("autobroadcast");
    }

    public boolean isEnabled() {
        return getConfig().getBoolean("autobroadcast.enabled");
    }

    public int getSeconds() {
        return getConfig().getInt("autobroadcast.seconds");
    }

    public List<String> getMessages() {
        return getConfig().getStringList("autobroadcast.messages");
    }

    public List<String> getNonDonorMessages() {
        return getConfig().getStringList("autobroadcast.non-donor-messages");
    }
}
