package net.practice.practice.storage.backend;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import net.practice.practice.Practice;
import net.practice.practice.game.player.Profile;
import net.practice.practice.util.RunnableShorthand;
import org.bson.Document;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class MongoBackend implements IBackend {

    private final Practice plugin;

    private MongoClient mongoClient;
    private MongoCollection<Document> profiles;

    public MongoBackend(final Practice plugin) {
        this.plugin = plugin;

        if(!load(plugin))
            plugin.getServer().getPluginManager().disablePlugin(plugin);
    }

    @Override
    public void createProfile(Profile profile) {
        RunnableShorthand.runAsync(() -> {
            profiles.insertOne(profile.toDocument());
        });
    }

    @Override
    public void saveProfile(Profile profile) {
        RunnableShorthand.runAsync(() -> {
            saveProfileSync(profile);
        });
    }

    @Override
    public void saveProfileSync(Profile profile) {
        if(profile.getName() == null)
            return;

        profiles.findOneAndReplace(eq("uuid", profile.getUuid().toString()), profile.toDocument());
    }

    @Override
    public void loadProfile(Profile profile) {
        Document document = profiles.find(eq("uuid", profile.getUuid().toString())).first();

        if(document != null)
            profile.load(document);
        else
            createProfile(profile);
    }

    @Override
    public void saveProfiles() {
        Profile.getProfiles().values().forEach(this::saveProfileSync);
    }

    public List<Profile> getAllProfiles() {
        List<Profile> all = new ArrayList<>(Profile.getProfiles().values());

        for(Document document : profiles.find()) {
            UUID uuid = UUID.fromString(document.getString("uuid"));
            if(Profile.getProfiles().containsKey(uuid.toString()))
                continue;

            all.add(Profile.getByUuid(uuid));
        }

        return all;
    }

    @SuppressWarnings("deprecation")
    public boolean load(JavaPlugin plugin) {
        try {
            final MongoInformation information = new MongoInformation(plugin.getConfig().getConfigurationSection("database"));
            final ServerAddress serverAddress = new ServerAddress(information.getAddress(), information.getPort());

            if(information.isAuthEnabled())
                mongoClient = new MongoClient(serverAddress, Arrays.asList(information.getMongoCredentials())/*, MongoClientOptions.builder().connectionsPerHost(2).threadsAllowedToBlockForConnectionMultiplier(750).build()*/);
            else
                mongoClient = new MongoClient(serverAddress);

            final MongoDatabase database = mongoClient.getDatabase(information.getDbName());
            profiles = database.getCollection("profiles");

            return true;
        } catch(Exception ex) {
            plugin.getLogger().severe("Unable to establish a Mongo connection: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        }
    }

    public void close() {
        if(mongoClient != null)
            mongoClient.close();
    }

    private class MongoInformation {

        @Getter private String dbName;
        @Getter private String address;
        @Getter private int port;

        @Getter private boolean authEnabled;
        @Getter private String authDb;
        @Getter private String authUser;
        @Getter private char[] authPass;

        public MongoInformation(final ConfigurationSection section) {
            dbName = section.getString("name");
            address = section.getString("address");
            port = section.getInt("port");

            authEnabled = section.getBoolean("auth.enabled");
            authDb = section.getString("auth.authDb");
            authUser = section.getString("auth.username");
            authPass = section.getString("auth.password").toCharArray();
        }

        public MongoCredential getMongoCredentials() {
            return MongoCredential.createCredential(authUser, authDb, authPass);
        }
    }
}
