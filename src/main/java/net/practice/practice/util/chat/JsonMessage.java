package net.practice.practice.util.chat;

//Created by Justis Root. Released into the public domain.
//https://gist.github.com/justisr
//
//Source is licensed for any use, provided that this copyright notice is retained.
//Modifications not expressly accepted by the author should be noted in the license of any forks.
//No warranty for any purpose whatsoever is implied or expressed,
//and the author shall not be held liable for any losses, direct or indirect as a result of using this software.
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author JustisR
 *
 *         Any questions, comments, or suggestions, feel free to contact me via
 *         skype at "justis.root@gmail.com"
 *
 *         Version 2.0
 *
 */

public class JsonMessage {

    private String msg;

    /**
     * Create a new json message!
     */
    public JsonMessage() {
        msg = "[{\"text\":\"\",\"extra\":[{\"text\": \"\"}";
    }

    private static Class<?> getNmsClass(String nmsClassName) throws ClassNotFoundException {
        return Class.forName("net.minecraft.server." + getServerVersion() + "." + nmsClassName);
    }

    private static String getServerVersion() {
        return Bukkit.getServer().getClass().getPackage().getName().substring(23);
    }

    /**
     * Send the json string to all players on the server.
     */
    public void send() {
        List<Object> players = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) players.add(p);
        send(players.toArray(new Player[players.size()]));
    }

    /**
     * Send the json string to specified player(s)
     * @param player to send the message to.
     */
    public void send(Player... player) {
        sendRawJson(msg + "]}]", player);
    }

    /**
     * Send a raw json string to specified players.
     * @param json string to send
     * @param player to send the message to.
     */
    public static void sendRawJson(String json, Player... player) {
        String nmsClass = ((!getServerVersion().startsWith("v1_7_R")) ? "IChatBaseComponent$" : "") + "ChatSerializer";
        for (Player p : player) {
            try {
                Object comp = getNmsClass(nmsClass).getMethod("a", String.class).invoke(null, json);
                Object packet = getNmsClass("PacketPlayOutChat").getConstructor(getNmsClass("IChatBaseComponent")).newInstance(comp);
                Object handle = p.getClass().getMethod("getHandle").invoke(p);
                Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
                playerConnection.getClass().getMethod("sendPacket",getNmsClass("Packet")).invoke(playerConnection, packet);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Append text to the json message.
     * @param text to be appended
     * @return json string builder
     */
    public JsonStringBuilder append(String text) {
        return new JsonStringBuilder(this, esc(text));
    }

    private static String esc(String s) {
        return JSONObject.escape(s);
    }
    /**
     *
     * @author JustisR
     *
     */
    public static class JsonStringBuilder {

        private final JsonMessage message;
        //private final String string;
        private final String string = ",{\"text\":\"\",\"extra\":[";
        private final String[] strings;
        private String hover = "", click = "";

        /**
         * Settings for the json message's text
         * @param jsonMessage the original message
         * @param text the text to be appended to the message.
         */
        private JsonStringBuilder(JsonMessage jsonMessage, String text) {
            message = jsonMessage;
            String[] colors = text.split(String.valueOf(ChatColor.COLOR_CHAR));
            for (int i = 0; i < colors.length; i++) {
                if (i == 0 && !text.startsWith(String.valueOf(ChatColor.COLOR_CHAR))) {
                    colors[i] = "{\"text\":\"" + colors[i] + "\"}";
                } else if (colors[i].length() < 1) {
                    colors[i] = "{\"text\":\"\"}";
                } else {
                    ChatColor color = ChatColor.getByChar(colors[i].substring(0, 1));
                    colors[i] = "{\"text\":\"" + colors[i].substring(1, colors[i].length()) + "\",\"color\":\"" + color.name().toLowerCase(Locale.US) + "\"}";
                }
                if (i + 1 != colors.length) colors[i] = colors[i] + ",";
            }
            strings = colors;
        }

        /**
         * Set the hover event's action as showing a tooltip with the given text
         * @param lore the text to be displayed in the tooltip
         * @return the json string builder to which you are applying settings
         */
        public JsonStringBuilder setHoverAsTooltip(String... lore) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < lore.length; i++)
                if (i + 1 == lore.length) builder.append(lore[i]);
                else builder.append(lore[i] + "\n");
            hover = ",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"" + esc(builder.toString()) + "\"}";
            return this;
        }

        /**
         * @deprecated Achievements were replaced with Advancements as of v1_12, and are formatted individually through show_text.
         * Set the hover event's action as showing an achievement
         * @param ach the achievement to be displayed.
         * 	for list of achievements, visit <a href="http://minecraft.gamepedia.com/Achievements">here</a>
         * @return the json string builder to which you are applying settings
         */
        public JsonStringBuilder setHoverAsAchievement(String ach) {
            if (getServerVersion().startsWith("v1_12")) hover = ",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"" + esc(ach) + "\"}";
            else hover = ",\"hoverEvent\":{\"action\":\"show_achievement\",\"value\":\"achievement." + esc(ach) + "\"}";
            return this;
        }

        /**
         * Set the click event's action as redirecting to a URL
         * @param link to redirect to
         * @return the json string builder to which you are applying settings.
         */
        public JsonStringBuilder setClickAsURL(String link) {
            click = ",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"" + esc(link) + "\"}";
            return this;
        }

        /**
         * Set the click event's action as suggesting a command
         * @param cmd to suggest
         * @return the json string builder to which you are applying settings;
         */
        public JsonStringBuilder setClickAsSuggestCmd(String cmd) {
            click = ",\"clickEvent\":{\"action\":\"suggest_command\",\"value\":\"" + esc(cmd) + "\"}";
            return this;
        }

        /**
         * Set the click event's action as executing a command
         * @param cmd
         * @return
         */
        public JsonStringBuilder setClickAsExecuteCmd(String cmd) {
            click = ",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"" + esc(cmd) + "\"}";
            return this;
        }

        /**
         * Finalize the appending of the text, with settings.
         * @return
         */
        public JsonMessage save() {
            StringBuilder builder = new StringBuilder(message.msg + string);
            for (String string : strings) {
                builder.append(string);
            }
            builder.append("]" + hover + click + "}");
            message.msg = builder.toString();
            return message;
        }
    }

}