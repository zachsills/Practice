package net.practice.practice.storage.backend;

import net.practice.practice.game.player.Profile;

public interface IBackend {

    void createProfile(Profile profile);

    void saveProfile(Profile profile);

    void saveProfileSync(Profile profile);

    void loadProfile(Profile profile);

    void saveProfiles();
}
