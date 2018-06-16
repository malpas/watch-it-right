package app.admin.csv;

import javax.ws.rs.NotFoundException;
import java.util.HashMap;
import java.util.Map;

/**
 * One line of data from CSV file
 */
public class CSVLine {
    Map<String, String> data = new HashMap<>();

    CSVLine(String line, Map<Integer, String> headerIndexMap) {
        boolean quoteBalanced = true;
        int headerNum = 0;
        StringBuilder currentValue = new StringBuilder();
        for (int index = 0; index < line.length(); index++) {
            char c = line.charAt(index);
            // handle string parsing
            if (c == '"') {
                // check double quotes
                if (index < line.length() - 1 && line.charAt(index + 1) == '"') {
                    currentValue.append('"');
                    index += 1; // skip the quote after this quote
                    continue;
                }
                // if quote was balanced, then it must be the start of a new string. otherwise, the end of one.
                quoteBalanced = !quoteBalanced;
            }
            // handle end of a value
            if (c == ',' && quoteBalanced) {
                // delete one set of quotation marks if needed
                if (currentValue.length() >= 2) {
                    if (currentValue.charAt(0) == '"' && currentValue.charAt(currentValue.length() - 1) == '"') {
                        currentValue.deleteCharAt(0);
                        currentValue.deleteCharAt(currentValue.length() - 1);
                    }
                }
                data.put(headerIndexMap.get(headerNum), currentValue.toString());
                headerNum += 1;
                currentValue = new StringBuilder();
            } else {
                currentValue.append(c);
            }
        }
    }

    public String get(String header) throws NotFoundException {
        if (!data.containsKey(header)) {
            throw new NotFoundException(String.format("could not find %s\n%s", header, data));
        }
        return data.get(header);
    }
}
