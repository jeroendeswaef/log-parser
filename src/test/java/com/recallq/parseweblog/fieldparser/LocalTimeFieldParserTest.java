package com.recallq.parseweblog.fieldparser;

import com.recallq.parseweblog.FieldParserException;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Jeroen De Swaef
 */
public class LocalTimeFieldParserTest {

    /**
     * Test of parse method, of class LocalTimeFieldParser.
     * @throws com.recallq.parseweblog.FieldParserException
     */
    @Test
    public void testParse() throws FieldParserException {
        String input = "24/Mar/2014:06:59:28 +0100";
        LocalTimeFieldParser instance = new LocalTimeFieldParser();
        String expResult = "1395640768000";
        String result = instance.parse(input);
        assertEquals(expResult, result);
    }
    
}
