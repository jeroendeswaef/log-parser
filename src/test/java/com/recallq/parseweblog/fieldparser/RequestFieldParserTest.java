package com.recallq.parseweblog.fieldparser;

import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jeroen De Swaef <j@recallq.com>
 */
public class RequestFieldParserTest {
    
    public RequestFieldParserTest() {
    }

    /**
     * Test of parse method, of class RequestFieldParser.
     */
    @Test
    public void testParse() throws Exception {
        String input = "GET /misc/drupal.js?myviua HTTP/1.1";
        RequestFieldParser instance = new RequestFieldParser();
        Map<String, String> result = instance.parse(input);
        assertEquals(3, result.keySet().size());
        assertEquals("GET", result.get("method"));
        assertEquals("/misc/drupal.js?myviua", result.get("url"));
        assertEquals("HTTP/1.1", result.get("http"));
    }
    
}
