package com.recallq.parseweblog;

import java.util.Map;

/**
 * Defines parsers that do transformations on log fields.
 * 
 * If isSingleOutput returns true: parseMany has to be called,
 * otherwise parse has to be called.
 * 
 * @author Jeroen De Swaef
 */
public interface FieldParser {
    boolean isSingleOutput();
}
