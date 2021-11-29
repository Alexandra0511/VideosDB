package entertainment;

import fileio.UserInputData;

public class User {
    private final UserInputData user;
    private int numberOfRatings;

    public UserInputData getUser() {
        return user;
    }
    public User(final UserInputData user) {
        this.user = user;
        this.numberOfRatings = 0;
    }

    /**
     * Getter
     * @return numarul total de ratinguri date de utilizator
     */
    public int getNumberOfRatings() {
        return numberOfRatings;
    }

    /**
     * Functie ce incrementeaza numarul de ratinguri date de user
     */
    public void addRating() {
        this.numberOfRatings++;
    }


}
