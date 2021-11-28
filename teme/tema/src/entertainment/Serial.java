package entertainment;

import fileio.SerialInputData;
import fileio.UserInputData;
import java.util.HashMap;
import java.util.Map;

public class Serial {
    public SerialInputData getSerial() {
        return serial;
    }

    private final SerialInputData serial;
    private final Map<Pair<UserInputData, Season>, Double> ratings;

    public Serial(final SerialInputData serial) {
        this.serial = serial;
        this.ratings = new HashMap<>();
    }

    /**
     *
     * @param user
     * @param rate
     * @param seasonNumber
     * @throws Exception
     */
    public void addRating(final UserInputData user,
                          final Double rate, final Season seasonNumber) throws Exception {
        Pair<UserInputData, Season> pair = new Pair<>(user, seasonNumber);
        if (ratings.containsKey(pair)) {
            throw new Exception();
        }
        ratings.put(pair, rate);
        seasonNumber.getRatings().add(rate);
    }

    /**
     *
     * @return
     */
    public double getRating() {
        double sum = 0;
        if (ratings.isEmpty()) {
            return 0;
        }
        double ct = serial.getNumberSeason();
        for (Season season : serial.getSeasons()) {
            if (season.getRatings().size() != 0) {
                double summ = season.getRatings().stream().reduce(0.0, Double::sum)
                        / season.getRatings().size();
                sum = sum + summ;
            }
        }
        return sum / ct;
    }

    public int totalDuration() {
        int sum = 0;
        for (Season season : serial.getSeasons()) {
            sum = sum + season.getDuration();
        }
        return sum;
    }
}
