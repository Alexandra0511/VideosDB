package actions;

import entertainment.Genre;
import entertainment.Movie;
import entertainment.Pair;
import entertainment.Serial;
import fileio.ActionInputData;
import fileio.Input;
import fileio.UserInputData;
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
     * Functie ce returneaza serialul cu nume dat ca parametru
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
     * @param action pentru preluarea username-ului utilizatorului
     * @param movies lista de filme din input
     * @param serials lista de seriale din input
     * @return videoclipul cerut
     */
    public static String standardRecommendation(final Input input,
                                                    final ActionInputData action,
                                                    final List<Movie> movies,
                                                    final List<Serial> serials) {
        UserInputData user = null;
        for (UserInputData elem : input.getUsers()) {
            if (elem.getUsername().equals(action.getUsername())) {
                user = elem;
            }
        }
        if (user == null) {
            return "StandardRecommendation cannot be applied!";
        }
        for (Movie movie : movies) {
            if (!user.getHistory().containsKey(movie.getMovie().getTitle())) {
                return "StandardRecommendation result: " + movie.getMovie().getTitle();
            }
        }
        for (Serial serial : serials) {
            if (!user.getHistory().containsKey(serial.getSerial().getTitle())) {
                return "StandardRecommendation result: " + serial.getSerial().getTitle();
            }
        }
        return "StandardRecommendation cannot be applied!";
    }

    /**
     * Functie ce returneaza cel mai bun video nevizualizat de catre utilizator
     * Am adaugat fiecare film si serial intr-o lista de perechi, alaturi de ratingul
     * fiecaruia. Nu am folosit map pentru a pastra ordinea din input. Apoi, parcurgand toata
     * lista, am returnat videoclipul cu rate maxim.
     * @param input pentru preluarea listei de useri
     * @param action pentru username-ul utilizatorului
     * @param movies lista de filme din input
     * @param serials lista de seriale din input
     * @return videoclipul cerut
     */
    public static String bestUnseenRecommendation(final Input input,
                                                      final ActionInputData action,
                                                      final List<Movie> movies,
                                                      final List<Serial> serials) {
        List<Pair<String, Double>> unseen = new ArrayList<>();
        UserInputData user = null;
        for (UserInputData elem : input.getUsers()) {
            if (elem.getUsername().equals(action.getUsername())) {
                user = elem;
            }
        }
        if (user == null) {
            return "BestRatedUnseenRecommendation cannot be applied!";
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
            return "BestRatedUnseenRecommendation cannot be applied!";
        }

        Double maxRate = (double) -1;
        String rec = "";
        for (Pair<String, Double> iter : unseen) {
            if (iter.getT2() > maxRate) {
                maxRate = iter.getT2();
                rec = iter.getT1();
            }
        }
        return "BestRatedUnseenRecommendation result: " + rec;
    }

    /**
     * Functie ce returneaza primul videoclip nevizualizat din cel mai popular gen
     * Parcurgand genurile videoclipurilor din istoricul fiecarui utilizator, am folosit o mapa
     * pentru a memora numele genului si de cate ori a fost vizualizat. Apoi, am sortat mapa
     * descrescator dupa numarul de vizualizari pentru a afla cel mai popular gen. Parcurgand
     * genurile, am gasit primul videoclip nevizualizat de catre utilizator din acel gen si
     * l-am returnat.
     * @param input pentru preluarea listei de useri
     * @param action pentru preluarea username-ului utilizatorului
     * @param movies lista de filme din input
     * @param serials lista de seriale din input
     * @return videoclipul cerut
     */
    public static String popularRecommendation(final Input input,
                                                   final ActionInputData action,
                                                   final List<Movie> movies,
                                                   final List<Serial> serials) {
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
        if (user.getSubscriptionType().equals("BASIC")) {
            return "PopularRecommendation cannot be applied!";
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
                        return "PopularRecommendation result: " + movie.getMovie().getTitle();
                    }
                }
            }
            for (Serial serial : serials) {
                if (!user.getHistory().containsKey(serial.getSerial().getTitle())) {
                    if (serial.getSerial().getGenres().contains(sortedList.get(i).getKey())) {
                        return "PopularRecommendation result: " + serial.getSerial().getTitle();
                    }
                }
            }
        }
        return "PopularRecommendation cannot be applied!";
    }

    /**
     * Functie ce returneaza videoclipul care e cel mai des intalnit in lista de favorite
     * nevizualizat de catre utilizatorul dat.
     * Initializez mapa cu fiecare videoclip in ordinea aparitiei in baza de date si valoarea 0.
     * Pentru fiecare user, parcurg lista de favorite si incrementez valoarea aparitiilor in
     * listele de favorite pentru fiecare videoclip din aceasta. Returnez primul videoclip
     * nevizualizat de catre user cu valoare maxima.
     * @param input pentru preluarea listei de useri
     * @param action pentru preluarea username-ului utilizatorului
     * @param movies lista de filme din input
     * @param serials lista de seriale din input
     * @return videoclipul cerut
     */
    public static String favoriteRecommendation(final Input input,
                                                    final ActionInputData action,
                                                    final List<Movie> movies,
                                                    final List<Serial> serials) {
        Map<String, Integer> favs = new HashMap<>();
        for (Movie movie : movies) {
            favs.put(movie.getMovie().getTitle(), 0);
        }
        for (Serial serial : serials) {
            favs.put(serial.getSerial().getTitle(), 0);
        }
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
            return "FavoriteRecommendation cannot be applied!";
        }
        return "FavoriteRecommendation result: " + best;
    }

    /**
     * Functie ce returneaza toate videoclipurile nevăzute de user dintr-un anumit gen, dat ca
     * filtru în input.
     * Pentru fiecare videoclip nevazut de catre utilizator, am testat daca cuprinde genul
     * dat in input, caz in care l-am adaugat in mapa, alaturi de ratingul sau.
     * Apoi am sortat mapa si am returnat lista cu toate videoclipurile.
     * @param input pentru preluarea listei de useri
     * @param action pentru preluarea username-ului utilizatorului si a genului
     * @param movies lista de filme din input
     * @param serials lista de seriale din input
     * @return videoclipul cerut
     */
    public static String searchRecommendation(final Input input,
                                                  final ActionInputData action,
                                                  final List<Movie> movies,
                                                  final List<Serial> serials) {
        Map<String, Double> searchResult = new HashMap<>();
        final StringBuilder list = new StringBuilder();
        UserInputData user = null;
        for (UserInputData elem : input.getUsers()) {
            if (elem.getUsername().equals(action.getUsername())) {
                user = elem;
            }
        }

        if (user == null || user.getSubscriptionType().equals("BASIC")) {
            return "SearchRecommendation cannot be applied!";
        }

        Genre genre = Utils.stringToGenre(action.getGenre());
        if (genre == null) {
            return "SearchRecommendation cannot be applied!";
        }
        for (Movie movie : movies) {
            if (!user.getHistory().containsKey(movie.getMovie().getTitle())) {
                for (String genreString : movie.getMovie().getGenres()) {
                    if (Utils.stringToGenre(genreString).equals(genre)) {
                        searchResult.put(movie.getMovie().getTitle(), movie.getRating());
                        break;
                    }
                }
            }
        }
        for (Serial serial : serials) {
            if (!user.getHistory().containsKey(serial.getSerial().getTitle())) {
                for (String genreString : serial.getSerial().getGenres()) {
                    if (Utils.stringToGenre(genreString).equals(genre)) {
                        searchResult.put(serial.getSerial().getTitle(), serial.getRating());
                        break;
                    }
                }
            }
        }
        if (searchResult.isEmpty()) {
            return "SearchRecommendation cannot be applied!";
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
        return "SearchRecommendation result: [" + list + "]";
    }
}
