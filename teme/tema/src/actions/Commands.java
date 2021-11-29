package actions;

import entertainment.Movie;
import entertainment.Serial;
import entertainment.User;

import fileio.ActionInputData;
import fileio.Input;
import fileio.UserInputData;

import java.util.List;

public final class Commands {
    private Commands() { };

    /**
     * Functie pentru gasirea userului caruia ii corespunde username-ul din input action
     * @param input pentru parcurgerea listei de useri
     * @param action pentru preluarea username-ului user-ului cautat
     * @return user-ul cu numele cautat
     */
    public static UserInputData returnUser(final Input input, final ActionInputData action) {
        UserInputData user = null;
        for (UserInputData elem : input.getUsers()) {
            if (elem.getUsername().equals(action.getUsername())) {
                user = elem;
            }
        }
        return user;
    }
    /**
     * Functie pentru a adauga titlul videoclipului primit in inputul pentru actiune in lista
     * de favorite a user-ului cu username-ul primit in input.
     * Am testat cazurile particulare pentru user-ul cautat: daca userul nu a vizionat
     * videoclipul din input (se afisaza mesajul cerut) si daca videoclipul se afla deja in lista
     * de favorite. Daca niciunul dintre aceste cazuri nu sunt intalnite, se adauga videoclipul
     * in lista de favorite.
     * Mentionez ca am folosit lista favoriteMovies din UserInputData, atat pentru adaugarea
     * filmelor favorite, cat si a serialelor (aceasta fiind de tip String).
     * @param input pentru preluarea listei de useri
     * @param action pentru preluarea id-ului actiunii, a username-ului si a titlului videoului
     * @return mesajele cerute
     */
    public static String favorite(final Input input, final ActionInputData action) {

        UserInputData user = returnUser(input, action);

        for (String video : user.getFavoriteMovies()) {
            if (video.equals(action.getTitle())) {
                return "error -> " + video + " is already in favourite list";
            }
        }
        if (!user.getHistory().containsKey(action.getTitle())) {
            return "error -> " + action.getTitle() + " is not seen";
        }
        user.getFavoriteMovies().add(action.getTitle());
        return "success -> " + action.getTitle() + " was added as favourite";
    }

    /**
     * Functie pentru adaugarea unui video in istoricul unui user.
     * Daca videoclipul se afla deja in istoric, incrementez numarul de views ale acestuia
     * in map si apoi returnez fisierul JSON.
     * Daca viseoclipul nu a fost deja vizional, il adaug in map-ul de istoric cu numarul
     * de views egal cu 1.
     * @param input pentru preluarea listei de useri
     * @param action pentru preluarea usernam-ului utilizatorului, id-ului actiunii si titlului
     *               videoclipului ce trebuie adaugat in lista
     * @return mesajele cerute
     */
    public static String view(final Input input, final ActionInputData action) {
        UserInputData user = returnUser(input, action);

        if (user.getHistory().containsKey(action.getTitle())) {
            user.getHistory().replace(action.getTitle(),
                    user.getHistory().get(action.getTitle()),
                    user.getHistory().get(action.getTitle()) + 1);
            return "success -> " + action.getTitle() + " was viewed with total views of "
                    + user.getHistory().get(action.getTitle());
        } else {
            user.getHistory().put(action.getTitle(), 1);
            return "success -> " + action.getTitle() + " was viewed with total views of 1";
        }
    }

    /**
     * Functie pentru oferirea unui rating de catre utilizator.
     * Am cautat obiectul de tip User cu username-ul cerut, am testat situatia particulara in care
     * videoclipul se afla deja in istoricul userului, caz in care am afisat mesajul de eroare.
     * Apoi, am parcurs toate filmele si serialele din input, pentru a afla tipul videoclipului.
     * In acealsi timp, am testat daca nu a fost deja oferit un rating pentru video de catre user,
     * caz in care am afisat mesajul de eroare. In caz contrar, am adaugat ratingul in lista de
     * ratinguri pentru film, respectiv serial, si am incrementat campul pentru numarul de
     * ratinguri date de utilizator.
     * @param movies lista de filme din input
     * @param action pentru preluarea id-ului, username-ului, titlului videoclipului
     *               si notei date in actiunea de rating
     * @param serials lista de seriale din input
     * @param users lista de useri din input
     * @return mesajele cerute
     */
    public static String rating(final List<Movie> movies, final ActionInputData action,
                                    final List<Serial> serials, final List<User> users) {
        User user = null;
        for (User elem : users) {
            if (elem.getUser().getUsername().equals(action.getUsername())) {
                user = elem;
            }
        }

        if (!user.getUser().getHistory().containsKey(action.getTitle())) {
            return "error -> " + action.getTitle() + " is not seen";
        } else {
            for (Movie movie : movies) {
                if (movie.getMovie().getTitle().equals(action.getTitle())) {
                    try {
                        movie.addRating(user.getUser(), action.getGrade());
                    } catch (Exception e) {
                        return "error -> " + action.getTitle() + " has been already rated";
                    }
                    user.addRating();
                    return "success -> " + action.getTitle() + " was rated with "
                            + action.getGrade() + " by " + user.getUser().getUsername();
                }
            }
            for (Serial serial : serials) {
                if (serial.getSerial().getTitle().equals(action.getTitle())) {
                    try {
                        serial.addRating(user.getUser(), action.getGrade(),
                                serial.getSerial().getSeasons().get(action.getSeasonNumber() - 1));
                    } catch (Exception e) {
                        return "error -> " + action.getTitle() + " has been already rated";
                    }
                    user.addRating();
                    return "success -> " + action.getTitle() + " was rated with "
                            + action.getGrade() + " by " + user.getUser().getUsername();
                }
            }
        }
        return null;
    }
}
