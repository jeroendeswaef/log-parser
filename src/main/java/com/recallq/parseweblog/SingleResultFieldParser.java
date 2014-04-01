package com.recallq.parseweblog;

/**
 * FieldParser that only return 1 string result.
 * 
 * @author Jeroen De Swaef <j@recallq.com>
 */
public abstract class SingleResultFieldParser implements FieldParser {
    @Override
    public boolean isSingleOutput() {
        return true;
    }
    
    public abstract Object parse(String input) throws FieldParserException;
}
