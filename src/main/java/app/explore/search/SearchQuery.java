package app.explore.search;

import java.util.ArrayList;
import java.util.List;

public class SearchQuery {
    private ArrayList<String> queryWords = new ArrayList<>();
    private List<String> genres = new ArrayList<>();
    private int beforeYear = -1;
    private int afterYear = -1;

    static SearchQuery parseFrom(String query) {
        SearchQuery result = new SearchQuery();
        result.parse(query);
        return result;
    }

    private void parse(String query) {
        String[] words = query.split(" ");
        for (String word : words) {
            //add genre constraint
            if (word.startsWith("genre:")) {
                genres.add(word.substring("genre:".length()));

            //add before year constraint
            } else if (word.startsWith("before:")) {
                try {
                    int year = Integer.parseInt(word.substring("before:".length()));
                    if (beforeYear > year || beforeYear == -1) {
                        beforeYear = year;
                    }
                } catch (NumberFormatException ignored) {
                }

            //add after year constraint
            } else if (word.startsWith("after:")) {
                try {
                    int year = Integer.parseInt(word.substring("after:".length()));
                    if (afterYear < year) {
                        afterYear = year;
                    }
                } catch (NumberFormatException ignored) {
                }
            }

            // if word is not a constraint, add it to search text
            else {
                queryWords.add(word);
            }
        }
    }

    String getText() {
        return String.join(" ", queryWords);
    }

    List<String> getGenres() {
        return genres;
    }

    int getBeforeYear() {
        return beforeYear;
    }

    int getAfterYear() {
        return afterYear;
    }
}
