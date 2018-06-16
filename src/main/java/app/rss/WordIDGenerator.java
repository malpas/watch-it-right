package app.rss;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generator for "Word ID"s -- 4 random words from a dictionary (located in resources) separated
 * by hyphens.
 */
class WordIDGenerator {
    private final ArrayList<String> words = new ArrayList<>();

    WordIDGenerator() {
        String RESOURCE_PATH = "/filtered.words.english";
        InputStream dictFileStream = WordIDGenerator.class.getResourceAsStream(RESOURCE_PATH);
        BufferedReader wordReader = new BufferedReader(new InputStreamReader(dictFileStream));
        wordReader.lines().forEach(words::add);
    }

    /**
     * Get a new Word ID.
     *
     * @return A string containing 4 random words separated by hyphens
     */
    public String newID() {
        ArrayList<String> randWords = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            String randomWord = words.get(ThreadLocalRandom.current().nextInt(words.size()));
            randWords.add(randomWord);
        }
        return String.join("-", randWords);
    }
}
