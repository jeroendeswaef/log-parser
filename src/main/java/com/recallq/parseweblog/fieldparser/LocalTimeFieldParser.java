package com.recallq.parseweblog.fieldparser;

import com.recallq.parseweblog.FieldParser;
import com.recallq.parseweblog.FieldParserException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Converts a time string from nginx' $local_time into seconds since Epoch time.
 * 
 * @author Jeroen De Swaef
 */
public class LocalTimeFieldParser implements FieldParser {
    public static final String DATE_FORMAT = "dd/MMM/yyyy:HH:mm:ssZ";
    
    @Override
    public String parse(String input) throws FieldParserException {
        try {
            SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
            Date d = format.parse(input);
            return String.valueOf(d.getTime());
        } catch (ParseException ex) {
            throw new FieldParserException("Cannot convert " + input + " to a valid local_time date", ex);
        }
    }
}
