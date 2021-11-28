package actions;

import entertainment.Genre;
import entertainment.Movie;
import entertainment.Pair;
import entertainment.Serial;
import fileio.ActionInputData;
import fileio.Input;
import fileio.UserInputData;
import org.json.JSONObject;
import utils.Utils;

import java.util.*;

public final class Recommendation {
    private Recommendation() { };

    /**
     * Functie ce returneaza filmul cu nume dat ca parametru
     * @param movie numele filmului cautat
     * @param movies lista in care se cauta numele
     * @return filmul cautat
     */
    public static Movie returnMovie(final String movie, final List<Movie> movies) {
        for (Movie iter : movies) {
            if (iter.getMovie().getTitle().equals(movie)) {
                return iter;
            }
        }
        return null;
    }

    /**
     * Functie ce returneaza un serial cu nume dat ca parametru
     * @param serial numele serialului cautat
     * @param serials lista de seriale in care se face cautarea
     * @return serialul cautat
     */
    public static Serial returnSerial(final String serial, final List<Serial> serials) {
            for (Serial iter : serials) {
                if (iter.getSerial().getTitle().equals(serial)) {
                    return iter;
                }
            }
        return null;
    }

    /**
     * Functie ce intoarce primul videoclip nevizionat de catre utilizator
     * Caut utilizatorul cu numele username-ul dat ca input in actiune, apoi parcurg
     * pe rand filmele si serialele. Cand gasesc un videoclip nevizualizat il returnez.
     * @param input pentru peluarea listei de useri
     * @param action pentru preluarea id-ului si a username-ului utilizatorului
     * @param movies lista de filme din input
     * @param serials lista de seriale din input
     * @return fisierul JSON cu datele cerute
     */
    public static JSONObject standardRecommendation(final Input input,
                                                    final ActionInputData action,
                                                    final List<Movie> movies,
                                                    final List<Serial> serials) {
        JSONObject file = new JSONObject();
        file.put("id", action.getActionId());
        UserInputData user = null;
        for (UserInputData elem : input.getUsers()) {
            if (elem.getUsername().equals(action.getUsername())) {
                user = elem;
            }
        }
        if (user == null) {
            file.put("message", "StandardRecommendation cannot be applied!");
            return file;
        }
        for (Movie movie : movies) {
            if (!user.getHistory().containsKey(movie.getMovie().getTitle())) {
                file.put("message", "StandardRecommendation result: "
                        + movie.getMovie().getTitle());
                return file;
            }
        }
        for (Serial serial : serials) {
            if (!user.getHistory().containsKey(serial.getSerial().getTitle())) {
                file.put("message", "StandardRecommendation result: "
                        + serial.getSerial().getTitle());
                return file;
            }
        }
        file.put("message", "StandardRecommendation cannot be applied!");
        return file;
    }

    /**
     * Functie ce returneaza cel mai bun video nevizualizat de catre utilizator
     *
     * @param input
     * @param action
     * @param movies
     * @param serials
     * @return
     */
    public static JSONObject bestUnseenRecommendation(final Input input,
                                                      final ActionInputData action,
                                                      final List<Movie> movies,
                                                      final List<Serial> serials) {
        JSONObject file = new JSONObject();
        file.put("id", action.getActionId());
        List<Pair<String, Double>> unseen = new ArrayList<>();
        UserInputData user = null;
        for (UserInputData elem : input.getUsers()) {
            if (elem.getUsername().equals(action.getUsername())) {
                user = elem;
            }
        }
        if (user == null) {
            file.put("message", "BestRatedUnseenRecommendation cannot be applied!");
            return file;
        }
        for (Movie movie : movies) {
            if (!user.getHistory().containsKey(movie.getMovie().getTitle())) {
                unseen.add(new Pair(movie.getMovie().getTitle(), movie.getRating()));
            }
        }
        for (Serial serial : serials) {
            if (!user.getHistory().containsKey(serial.getSerial().getTitle())) {
                unseen.add(new Pair(serial.getSerial().getTitle(), serial.getRating()));
            }
        }

        if (unseen.isEmpty()) {
            file.put("message", "BestRatedUnseenRecommendation cannot be applied!");
            return file;
        }

        Double maxRate = Double.valueOf(-1);
        String rec = "";
        for (Pair<String, Double> iter : unseen) {
            if (iter.getT2() > maxRate) {
                maxRate = iter.getT2();
                rec = iter.getT1();
            }
        }

        file.put("message", "BestRatedUnseenRecommendation result: " + rec);
        return file;
    }

    /**
     *
     * @param input
     * @param action
     * @param movies
     * @param serials
     * @return
     */
    public static JSONObject popularRecommendation(final Input input,
                                                   final ActionInputData action,
                                                   final List<Movie> movies,
                                                   final List<Serial> serials) {
        JSONObject file = new JSONObject();
        file.put("id", action.getActionId());
        Map<String, Integer> popular = new HashMap<>();
        UserInputData user = null;
        for (UserInputData elem : input.getUsers()) {
            if (elem.getUsername().equals(action.getUsername())) {
                user = elem;
            }
            for (Map.Entry<String, Integer> video : elem.getHistory().entrySet()) {
                Movie movie = returnMovie(video.getKey(), movies);
                if (movie != null) {
                    for (String genre : movie.getMovie().getGenres()) {
                        if (!popular.containsKey(genre)) {
                            popular.put(genre, video.getValue());
                        } else {
                            popular.replace(genre, popular.get(genre),
                                    popular.get(genre) + video.getValue());
                        }
                    }
                } else {
                    Serial serial = returnSerial(video.getKey(), serials);
                    for (String genre : serial.getSerial().getGenres()) {
                        if (!popular.containsKey(genre)) {
                            popular.put(genre, video.getValue());
                        } else {
                            popular.replace(genre, popular.get(genre),
                                    popular.get(genre) + video.getValue());
                        }
                    }
                }
            }
        }
        if(user.getSubscriptionType().equals("BASIC")) {
            file.put("message", "PopularRecommendation cannot be applied!");
            return file;
        }
        List<Map.Entry<String, Integer>> sortedList =
                new LinkedList<>(popular.entrySet());

        sortedList.sort((o1, o2) -> {
            Integer favNr1 = o1.getValue();
            Integer favNr2 = o2.getValue();
            return favNr1.compareTo(favNr2);
        });
        Collections.reverse(sortedList);

        for (int i = 0; i < sortedList.size(); i++) {
            for (Movie movie : movies) {
                if (!user.getHistory().containsKey(movie.getMovie().getTitle())) {
                    if (movie.getMovie().getGenres().contains(sortedList.get(i).getKey())) {
                        file.put("message", "PopularRecommendation result: "
                                + movie.getMovie().getTitle());
                        return file;
                    }
                }
            }
            for (Serial serial : serials) {
                if (!user.getHistory().containsKey(serial.getSerial().getTitle())) {
                    if (serial.getSerial().getGenres().contains(sortedList.get(i).getKey())) {
                        file.put("message", "PopularRecommendation result: "
                                + serial.getSerial().getTitle());
                        return file;
                    }
                }
            }
        }
        file.put("message", "PopularRecommendation cannot be applied!");
        return file;
    }

    /**
     *
     * @param input
     * @param action
     * @param movies
     * @param serials
     * @return
     */
    public static JSONObject favoriteRecommendation(final Input input,
                                                    final ActionInputData action,
                                                    final List<Movie> movies,
                                                    final List<Serial> serials) {
        JSONObject file = new JSONObject();
        file.put("id", action.getActionId());
        Map<String, Integer> favs = new HashMap<>();
        for (Movie movie : movies) {
            favs.put(movie.getMovie().getTitle(), 0);
        }
        for (Serial serial : serials) {
            favs.put(serial.getSerial().getTitle(), 0);
        }
        final StringBuilder list = new StringBuilder();
        UserInputData user = null;
        for (UserInputData elem : input.getUsers()) {
            if (elem.getUsername().equals(action.getUsername())) {
                user = elem;
            } else {
                for (String fav : elem.getFavoriteMovies()) {
                    favs.replace(fav, favs.get(fav), favs.get(fav) + 1);
                }
            }
        }
        String best = movies.get(0).getMovie().getTitle();
        Integer max = 0;
        for (Map.Entry<String, Integer> iter : favs.entrySet()) {
            if (!user.getHistory().containsKey(iter.getKey())) {
                if (iter.getValue() > max) {
                    best = iter.getKey();
                    max = iter.getValue();
                }
            }
        }
        if (max == 0) {
            file.put("message", "FavoriteRecommendation cannot be applied!");
            return file;
        }
        file.put("message", "FavoriteRecommendation result: " + best);
        return file;
    }

    /**
     *
     * @param input
     * @param action
     * @param movies
     * @param serials
     * @return
     */
    public static JSONObject searchRecommendation(final Input input,
                                                  final ActionInputData action,
                                                  final List<Movie> movies,
                                                  final List<Serial> serials) {
        JSONObject file = new JSONObject();
        file.put("id", action.getActionId());
        Map<String, Double> searchResult = new HashMap<>();
        final StringBuilder list = new StringBuilder();
        UserInputData user = null;
        for (UserInputData elem : input.getUsers()) {
            if (elem.getUsername().equals(action.getUsername())) {
                user = elem;
            }
        }

//        String str = action.getGenre();
//        String[] arrOfStr = str.split(" & ");
//        boolean validGenre = false;
//        for (String a : arrOfStr) {
//            validGenre = false;
//            for (Genre g : Genre.values()) {
//                if (g.name().equals(a.toUpperCase())) {
//                    validGenre = true;
//                    break;
//                }
//            }
//        }

        if (user == null || user.getSubscriptionType().equals("BASIC")) {
            file.put("message", "SearchRecommendation cannot be applied!");
            return file;
        }

        Genre genre = Utils.stringToGenre(action.getGenre());
        if(genre == null) {
            file.put("message", "SearchRecommendation cannot be applied!");
            return file;
        }
        for (Movie movie : movies) {
            if (!user.getHistory().containsKey(movie.getMovie().getTitle())) {
                for(String genreString : movie.getMovie().getGenres()) {
                    if (Utils.stringToGenre(genreString).equals(genre)) {
                        searchResult.put(movie.getMovie().getTitle(), movie.getRating());
                        break;
                    }
                }
            }
        }

        for (Serial serial : serials) {
            if (!user.getHistory().containsKey(serial.getSerial().getTitle())) {
                for(String genreString : serial.getSerial().getGenres()) {
                    if (Utils.stringToGenre(genreString).equals(genre)) {
                        searchResult.put(serial.getSerial().getTitle(), serial.getRating());
                        break;
                    }
                }
            }
        }
        if (searchResult.isEmpty()) {
            file.put("message", "SearchRecommendation cannot be applied!");
            return file;
        }
        List<Map.Entry<String, Double>> sortedList =
                new LinkedList<>(searchResult.entrySet());

        sortedList.sort((o1, o2) -> {
            Double rate1 = o1.getValue();
            Double rate2 = o2.getValue();
            int x = rate1.compareTo(rate2);
            if (x != 0) {
                return x;
            }
            String name1 = o1.getKey();
            String name2 = o2.getKey();
            return name1.compareTo(name2);
        });

        for (int i = 0; i < sortedList.size() - 1; i++) {
            list.append(sortedList.get(i).getKey());
            list.append(", ");
        }
        list.append(sortedList.get(sortedList.size() - 1).getKey());
        file.put("message", "SearchRecommendation result: [" + list + "]");
        return file;

    }
}
