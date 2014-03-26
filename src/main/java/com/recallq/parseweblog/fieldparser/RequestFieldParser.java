package com.recallq.parseweblog.fieldparser;

import com.recallq.parseweblog.FieldParserException;
import com.recallq.parseweblog.MultipleResultFieldParser;
import java.util.HashMap;
import java.util.Map;

/**
 * Parses the log field for the request.
 * f.e. GET /fr/index.html HTTP/1.0
 *  -> { "method": "GET", "url": "/fr/index.html", "http" : "HTTP/1.0" }
 * 
 * @author Jeroen De Swaef <j@recallq.com>
 */
public class RequestFieldParser extends MultipleResultFieldParser {

    public static final String KEY_METHOD = "method";
    public static final String KEY_URL = "url";
    public static final String KEY_HTTP = "http";
    
    @Override
    public Map<String, String> parse(String input) throws FieldParserException {
        String[] parts = input.split(" ");
        if (parts.length != 3) {
            throw new FieldParserException(String.format(
                    "Unexpected #parts for RequestFieldParser: %d parts for %s",
                    parts.length, input), null);
        }
        Map<String, String> fields = new HashMap<String, String>();
        fields.put(KEY_METHOD, parts[0]);
        fields.put(KEY_URL, parts[1]);
        fields.put(KEY_HTTP, parts[2]);
        return fields;
    }
    
}
