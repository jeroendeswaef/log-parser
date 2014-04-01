package com.recallq.parseweblog.fieldparser;

import com.recallq.parseweblog.FieldParserException;
import com.recallq.parseweblog.SingleResultFieldParser;

/**
 * Parser that treats a field as a long.
 * The default is to treat each field as a String.
 * 
 * @author Jeroen De Swaef <j@recallq.com>
 */
public class SimpleLongParser extends SingleResultFieldParser {

    @Override
    public Object parse(String input) throws FieldParserException {
        return Long.parseLong(input);
    }

}
