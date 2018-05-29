package net.practice.practice.util.player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.util.UUIDTypeAdapter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * https://gist.github.com/Jofkos/d0c469528b032d820f42
 */
public class UUIDFetcher {

    private static Gson gson = new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();

    private static final String UUID_URL = "https://api.mojang.com/users/profiles/minecraft/%s?at=%d";
    private static final String NAME_URL = "https://api.mojang.com/user/profiles/%s/names";

    private static Map<String, String> uuidCache = new HashMap<>(), nameCache = new HashMap<>();

    private static ExecutorService pool = Executors.newCachedThreadPool();

    private UUID id;
    private String name;

    /**
     * Fetches the uuid asynchronously and passes it to the consumer
     *
     * @param name The name
     * @param action Do what you want to do with the uuid her
     */
    public static void getUUID(final String name, final Consumer<UUID> action) {
        pool.execute(() -> action.accept(getUUID(name)));
    }

    /**
     * Fetches the uuid synchronously and returns it
     *
     * @param name The name
     * @return The uuid
     */
    public static UUID getUUID(final String name) {
        return getUUIDAt(name, System.currentTimeMillis());
    }

    /**
     * Fetches the uuid synchronously for a specified name and time and passes the result to the consumer
     *
     * @param name The name
     * @param timestamp Time when the player had this name in milliseconds
     * @param action Do what you want to do with the uuid her
     */
    public static void getUUIDAt(final String name, final long timestamp, final Consumer<UUID> action) {
        pool.execute(() -> action.accept(getUUIDAt(name, timestamp)));
    }

    /**
     * Fetches the uuid synchronously for a specified name and time
     *
     * @param name The name
     * @param timestamp Time when the player had this name in milliseconds
     */
    public static UUID getUUIDAt(String name, final long timestamp) {
        name = name.toLowerCase();
        if(uuidCache.containsKey(name))
            return UUID.fromString(uuidCache.get(name));

        try {
            final HttpURLConnection connection = (HttpURLConnection) new URL(String.format(UUID_URL, name, timestamp / 1000)).openConnection();
            connection.setReadTimeout(5000);

            final UUIDFetcher data = gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), UUIDFetcher.class);

            uuidCache.put(name, data.id.toString());
            nameCache.put(data.id.toString(), data.name);

            return data.id;
        } catch(final Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    /**
     * Fetches the name asynchronously and passes it to the consumer
     *
     * @param uuid The uuid
     * @param action Do what you want to do with the name her
     */
    public static void getName(final UUID uuid, final Consumer<String> action) {
        pool.execute(() -> action.accept(getName(uuid)));
    }

    /**
     * Fetches the name synchronously and returns it
     *
     * @param uuid The uuid
     * @return The name
     */
    public static String getName(final UUID uuid) {
        if(nameCache.containsKey(uuid.toString()))
            return nameCache.get(uuid.toString());

        try {
            final HttpURLConnection connection = (HttpURLConnection) new URL(String.format(NAME_URL, UUIDTypeAdapter.fromUUID(uuid))).openConnection();
            connection.setReadTimeout(5000);

            final UUIDFetcher[] nameHistory = gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), UUIDFetcher[].class);
            final UUIDFetcher currentNameData = nameHistory[nameHistory.length - 1];

            uuidCache.put(currentNameData.name.toLowerCase(), uuid.toString());
            nameCache.put(uuid.toString(), currentNameData.name);

            return currentNameData.name;
        } catch(final Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }
}