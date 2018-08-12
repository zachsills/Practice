package land.nub.practice.listener.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerOptions;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import land.nub.practice.util.chat.C;
import land.nub.practice.Practice;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ServerListener {

    public ServerListener() {
        loadPingListener();
    }

    private void loadPingListener() {
        final List<WrappedGameProfile> messages = new ArrayList<>();
        messages.add(new WrappedGameProfile("1", C.color("&6&lNub Land &7[1.7 | 1.8]")));
        messages.add(new WrappedGameProfile("2", C.color(" ")));
        messages.add(new WrappedGameProfile("3", C.color("&b&lTWITTER &f@Nub_Land")));
        messages.add(new WrappedGameProfile("4", C.color("&e&lFORUMS &fwww.nub.land")));
        messages.add(new WrappedGameProfile("5", " "));
        messages.add(new WrappedGameProfile("6", C.color("&6&lSTORE &fdonate.nub.land")));

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Practice.getInstance(), ListenerPriority.NORMAL, Arrays.asList(PacketType.Status.Server.SERVER_INFO), ListenerOptions.ASYNC) {
            @Override
            public void onPacketSending(PacketEvent event) {
                event.getPacket().getServerPings().read(0).setPlayers(messages);
            }
        });

        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Practice.getInstance(), ListenerPriority.NORMAL, Arrays.asList(PacketType.Play.Server.TAB_COMPLETE), ListenerOptions.ASYNC) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player player = event.getPlayer();
                if(player.isOp() || player.hasPermission("moderation.staff"))
                    return;

                List<String> stringList = new ArrayList<>(Arrays.asList(event.getPacket().getStringArrays().read(0)));
                Iterator<String> it = stringList.iterator();
                while(it.hasNext()) {
                    String outgoing = it.next();
                    Player other = Bukkit.getPlayer(outgoing);
                    if(other == null || !other.isOnline())
                        continue;

                    land.nub.core.player.Profile coreProfile = land.nub.core.player.Profile.getByPlayer(other);
                    if(coreProfile.getModMode() != null && coreProfile.getModMode().isVanished())
                        it.remove();
                }

                event.getPacket().getStringArrays().write(0, stringList.toArray(new String[stringList.size()]));
            }
        });

        /*ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Practice.getInstance(), PacketType.Play.Server.PLAYER_INFO) {
            @Override
            public void onPacketSending(PacketEvent event) {
                List<PlayerInfoData> dataList = event.getPacket().getPlayerInfoDataLists().read(0);
                Iterator<PlayerInfoData> it = dataList.iterator();
                while(it.hasNext()) {
                    PlayerInfoData data = it.next();
                    if(C.strip(data.getProfile().getName()).replaceAll(" ", "").isEmpty())
                        continue;

                    try {
                        Field field = PlayerInfoData.class.getDeclaredField("profile");
                        field.setAccessible(true);

                        Field field1 = AbstractWrapper.class.getDeclaredField("handle");
                        field1.setAccessible(true);

                        Field field2 = GameProfile.class.getDeclaredField("name");
                        field2.setAccessible(true);

                        Field field3 = GameProfile.class.getDeclaredField("properties");
                        field3.setAccessible(true);

                        WrappedGameProfile profile = data.getProfile();
                        GameProfile oldGp = (GameProfile) profile.getHandle();

                        GameProfile newGp = new GameProfile(oldGp.getId(), oldGp.getName());

                        field3.set(newGp, oldGp.getProperties());
                        field2.set(newGp, "Thryl");
                        field1.set(profile, newGp);
                        field.set(data, profile);
                    } catch(Exception ex) {

                    }
                }

                event.getPacket().getPlayerInfoDataLists().write(0, dataList);
            }
        });*/
    }
}
