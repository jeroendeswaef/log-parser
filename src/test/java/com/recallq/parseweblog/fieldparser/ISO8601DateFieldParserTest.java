package com.recallq.parseweblog.fieldparser;

import static org.junit.Assert.*;

/**
 *
 * @author Jeroen De Swaef
 */
public class ISO8601DateFieldParserTest {
    
    /**
     * Test of parse method, of class ISO8601DateFieldParser.
     */
    @org.junit.Test
    public void testParse() {
        String input = "2014-03-25T01:04:11+00:00";
        ISO8601DateFieldParser instance = new ISO8601DateFieldParser();
        String expResult = "1395709451000";
        String result = (String) instance.parse(input);
        assertEquals(expResult, result);
    }
    
}
