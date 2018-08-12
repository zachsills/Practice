package land.nub.practice.util;

// Not mine
public class EloUtils {

    public static int getNewRating(int rating, int opponentRating, double score) {
        double kFactor = 32;
        double expectedScore = getExpectedScore(rating, opponentRating);

        return calculateNewRating(rating, score, expectedScore, kFactor);
    }

    private static int calculateNewRating(int oldRating, double score, double expectedScore, double kFactor) {
        return oldRating + (int) (kFactor * (score - expectedScore));
    }

    private static double getExpectedScore (int rating, int opponentRating) {
        return 1.0 / (1.0 + Math.pow(10.0, ((double) (opponentRating - rating) / 400.0)));
    }
}
