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

    public int getNumberOfRatings() {
        return numberOfRatings;
    }

    public void addRating() {
        this.numberOfRatings++;
    }


}
