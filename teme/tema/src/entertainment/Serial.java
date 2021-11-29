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
     * Functie ce adauga un rating pentru un sezon din serial
     * Pentru a memora atat utilizatorul cat si sezonul caruia i-a fost atribuita
     * nota, m-am folosit de o structura pereche, implementata in clasa Pair
     * @param user utilizatorul care a dat ratingul
     * @param rate nota data
     * @param seasonNumber numarul sezonului caruia i-a fost dat ratingul
     * @throws Exception pentru cazul in care utilizatorul a mai oferit rating sezonului
     *          respectiv
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
     * Functie ce calculeaza ratingul mediu al unui serial
     * Pentru fiecare sezon, calculez media rating-urilor diferite de 0,
     * apoi aflu media ratingurilor pentru tot serialul.
     * @return ratingul final
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

    /**
     * Functie ce calculeaza durata totala a unui serial, prin adunarea duratelor
     * fiecarui sezon
     * @return durata totala
     */
    public int totalDuration() {
        int sum = 0;
        for (Season season : serial.getSeasons()) {
            sum = sum + season.getDuration();
        }
        return sum;
    }
}
