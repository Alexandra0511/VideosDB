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
     * Functie pentru adaugarea unui rating dat de catre un utilizator
     * @param user utilizatorul care da ratingul
     * @param rating nota data
     * @throws Exception in cazul in care utilizatorul a dat deja rating pentru film
     */
    public void addRating(final UserInputData user, final Double rating) throws Exception {
        if (ratings.containsKey(user)) {
            throw new Exception();
        }
        ratings.put(user, rating);
    }

    /**
     * Functie ce calculeaza ratingul mediu pentru un film
     * Calculez media aritmetica a tuturor ratingurilor unui film
     * @return ratingul total
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
