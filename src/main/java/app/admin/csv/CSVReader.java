package app.admin.csv;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Simple CSV Reader, built from scratch.
 */
public class CSVReader {
    private BufferedReader reader;
    Map<Integer, String> headerIndexMap = new HashMap<>();

    public static CSVReader fromStream(InputStream s) throws HeaderNotFoundException {
        CSVReader out = new CSVReader();
        out.reader = new BufferedReader(new InputStreamReader(s));
        out.readHeader();
        return out;
    }

    public void readHeader() throws HeaderNotFoundException {
        String firstLine = reader.lines()
                .findFirst()
                .orElseThrow(HeaderNotFoundException::new);
        String[] headers = firstLine.split(",");
        for (int i = 0; i < headers.length; i++) {
            headerIndexMap.put(i, headers[i].trim());
        }
    }

    public Stream<CSVLine> stream() {
        return reader.lines()
                .skip(1)
                .map(str -> new CSVLine(str, headerIndexMap));
    }
}
