package com.recallq.parseweblog;

/**
 * Defines parsers that do transformations on log fields.
 * 
 * @author Jeroen De Swaef
 */
public interface FieldParser {
    String parse(String input) throws FieldParserException;
}
