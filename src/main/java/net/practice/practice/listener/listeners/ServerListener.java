package net.practice.practice.listener.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import net.practice.practice.Practice;
import net.practice.practice.util.chat.C;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerListener {

    public ServerListener() {
        loadPingListener();
    }

    private void loadPingListener() {
        final List<WrappedGameProfile> messages = new ArrayList<>();
        messages.add(new WrappedGameProfile("1", C.color("&4&lNUB&C&LLAND&7[1.7 -> 1.8]")));
        messages.add(new WrappedGameProfile("2", C.color(" ")));
        messages.add(new WrappedGameProfile("3", C.color("&b&lTWITTER &f@NubLand")));
        messages.add(new WrappedGameProfile("4", C.color("&e&lFORUMS &fwww.nub.land")));
        messages.add(new WrappedGameProfile("5", C.color("&a&lVOTE &fwww.nub.land/vote")));
        messages.add(new WrappedGameProfile("6", " "));
        messages.add(new WrappedGameProfile("7", C.color("&6&lSTORE &fstore.nub.land")));

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Practice.getInstance(), ListenerPriority.NORMAL, Arrays.asList(PacketType.Status.Server.SERVER_INFO), ListenerOptions.ASYNC) {
            @Override
            public void onPacketSending(final PacketEvent event) {
                event.getPacket().getServerPings().read(0).setPlayers(messages);
            }
        });
    }
}
