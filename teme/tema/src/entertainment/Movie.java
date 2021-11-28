package entertainment;

import fileio.MovieInputData;
import fileio.UserInputData;

import java.util.HashMap;
import java.util.Map;

public class Movie {
    private final MovieInputData movie;
    public MovieInputData getMovie() {
        return movie;
    }

    public Movie(final MovieInputData movie) {
        this.movie = movie;
        this.ratings = new HashMap<>();
    }

    private final Map<UserInputData, Double> ratings;

    /**
     *
     * @param user
     * @param rate
     * @throws Exception
     */
    public void addRating(final UserInputData user, final Double rate) throws Exception {
        if (ratings.containsKey(user)) {
            throw new Exception();
        }
        ratings.put(user, rate);
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
        double ct = ratings.size();
        for (Double elem : ratings.values())  {
            sum += elem;
        }
        return sum / ct;
    }

}
