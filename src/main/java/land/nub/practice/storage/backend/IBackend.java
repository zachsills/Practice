package land.nub.practice.storage.backend;

import land.nub.practice.game.ladder.Ladder;
import land.nub.practice.game.player.Profile;

import java.util.List;

public interface IBackend {

    void createProfile(Profile profile);

    void saveProfile(Profile profile);

    void saveProfileSync(Profile profile);

    void loadProfile(Profile profile);

    void saveProfiles();

    List<Profile> getTopProfiles(Ladder ladder);
}
